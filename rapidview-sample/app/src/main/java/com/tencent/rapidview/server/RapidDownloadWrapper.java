/***************************************************************************************************
 Tencent is pleased to support the open source community by making RapidView available.
 Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 Licensed under the MITLicense (the "License"); you may not use this file except in compliance
 withthe License. You mayobtain a copy of the License at

 http://opensource.org/licenses/MIT

 Unless required by applicable law or agreed to in writing, software distributed under the License is
 distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied. See the License for the specific language governing permissions and limitations under the
 License.
 ***************************************************************************************************/
package com.tencent.rapidview.server;

import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.utils.MD5;
import com.tencent.rapidview.utils.XLog;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Class RapidDownloadWrapper
 * @Desc 不同产品使用不同的下载组件也没关系，只要实现本类的IDownload接口
 *       就可以进行批量下载统一回调。本类目的是与下载组件解耦，便于移动。
 *       同时可以满足皮肤引擎需求进行批量下载，统一回调。
 *
 * @author arlozhang
 * @date 2016.04.26
 */
public class RapidDownloadWrapper {

    private Map<String, String> mDownloadMap = new ConcurrentHashMap<String, String>();

    private Map<String, String> mUrlMap = new ConcurrentHashMap<String, String>();

    private Map<String, String> mMd5Map = new ConcurrentHashMap<String, String>();

    private IDownload mDownloadComponent = null;

    private ICallback mCallback = null;

    private boolean mIsFinished = false;

    Lock mLock = new ReentrantLock();

    private volatile boolean mIsDownloading = false;

    public interface IDownload{

        interface ICallback{

            void onFinish(String ticket, boolean isSucceed, String filePath);
        }

        boolean download(String ticket, String url, ICallback callback);
    }

    public interface ICallback{

        void onFinish(boolean isSucceed, Map<String, String> mapFilePath);
    }

    public RapidDownloadWrapper(IDownload downloadComponent,
                                Map<String, String> urlMap,
                                Map<String, String> md5Map){

        mDownloadComponent = downloadComponent;

        mDownloadMap.clear();

        mUrlMap.clear();

        mMd5Map.clear();

        if( md5Map != null ){
            mMd5Map.putAll(md5Map);
        }

        if( urlMap == null ){
            return;
        }

        for( Map.Entry<String, String> entry : urlMap.entrySet() ){

            if( entry.getKey() == null || entry.getValue() == null ){
                continue;
            }

            mUrlMap.put(entry.getKey(), entry.getValue());
            mDownloadMap.put(entry.getKey(), "");
        }
    }

    public synchronized boolean download(ICallback callback){
        boolean isNeedDownload = false;

        if( mIsDownloading || callback == null || mDownloadComponent == null ){
            return false;
        }

        for( Map.Entry<String, String> entry : mDownloadMap.entrySet() ){
            if( entry.getValue() == null ||
                entry.getValue().compareTo("") == 0 ){

                isNeedDownload = true;
                break;
            }
        }

        if( !isNeedDownload ){
            callback.onFinish(true, mDownloadMap);
            return true;
        }

        mIsDownloading = true;

        mLock.lock();

        try{
            mCallback = callback;
        }
        finally {
            mLock.unlock();
        }

        for( Map.Entry<String, String> entry : mDownloadMap.entrySet() ){
            String url;

            if( entry.getKey() == null ){
                continue;
            }

            if( entry.getValue() != null && entry.getValue().compareTo("") != 0 ){
                continue;
            }

            url = mUrlMap.get(entry.getKey());
            if( url == null ){
                continue;
            }

            mDownloadComponent.download(entry.getKey(), url, new IDownload.ICallback() {
                @Override
                public void onFinish(String ticket, boolean isSucceed, String filePath) {
                    if( ticket == null ){
                        return;
                    }

                    mLock.lock();

                    if( mIsFinished ){
                        return;
                    }

                    try{
                        boolean downloadFinish = true;
                        boolean succeed = true;

                        if( mDownloadMap.get(ticket) == null ){
                            return;
                        }

                        if( !isSucceed || !isMd5Pass(ticket, filePath) ){
                            XLog.d(RapidConfig.RAPID_ERROR_TAG, "MD5校验失败或者下载失败");

                            mDownloadMap.put(ticket, "none");
                        }
                        else{
                            mDownloadMap.put(ticket, filePath);
                        }

                        for( Map.Entry<String, String> entry : mDownloadMap.entrySet() ){
                            if( entry.getValue() == null || entry.getValue().compareTo("") == 0 ){
                                downloadFinish = false;
                                break;
                            }

                            if( entry.getValue().compareTo("none") == 0 ){
                                succeed = false;
                            }
                        }

                        if( !downloadFinish ){
                            return;
                        }

                        mIsDownloading = false;

                        if( mCallback == null ){
                            return;
                        }

                        mIsFinished = true;
                        mCallback.onFinish(succeed, mDownloadMap);
                    }
                    finally {
                        mLock.unlock();
                    }
                }
            });
        }

        return true;
    }

    private boolean isMd5Pass(String ticket, String filePath){
        String refMd5;
        String md5;
        File downloadFile;

        if( ticket == null || filePath == null ){
            return false;
        }

        refMd5 = mMd5Map.get(ticket);
        if( refMd5 == null ){
            return true;
        }

        downloadFile = new File(filePath);
        if( !downloadFile.isFile() ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "下载的不是文件，MD5校验失败：" + filePath);
            return false;
        }

        md5 = MD5.getFileMD5(new File(filePath));
        if( md5 == null ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "计算后MD5为空，MD5校验失败：" + filePath);
            return false;
        }

        if( md5.compareToIgnoreCase(refMd5) != 0 ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "MD5匹配失败，参考MD5：" + refMd5 + "|文件MD5：" + md5);
            return false;
        }

        return true;
    }
}
