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
package com.tencent.rapidview.runtime;

import android.content.Context;
import android.widget.RelativeLayout;

import com.tencent.rapidview.RapidLoader;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.param.RelativeLayoutParams;
import com.tencent.rapidview.server.RapidDownload;
import com.tencent.rapidview.server.RapidDownloadWrapper;
import com.tencent.rapidview.server.RapidRuntimeEngine;
import com.tencent.rapidview.utils.FileUtil;
import com.tencent.rapidview.utils.HandlerUtils;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.XLog;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Class RapidRuntimeView
 * @Desc RapidView实时展示View，提供可选sandbox能力，可用于自定义实时样式或对CP开放。
 *
 * @author arlozhang
 * @date 2017.02.13
 */
public class RuntimeView extends RelativeLayout {

    private Context mContext = null;

    private String mRapidID = "";

    private String mXml = "main.xml";

    private RapidRuntimeEngine mEngine = new RapidRuntimeEngine();

    private Lock mLock = new ReentrantLock();

    private String mLocalMd5 = null;

    private String mServerMd5 = null;

    private String mServerUrl = null;

    private int mLimitLevel = -1;

    public IRapidView mRapidView = null;

    private IListener mListener = null;

    private RapidDownloadWrapper mWrapper = null;

    private Map<String, Var> mMapContext = null;

    private Map<String, Var> mMapParams = new ConcurrentHashMap<String, Var>();

    public interface IListener{

        void onFailed();

        void onSucceed(IRapidView rapidView);
    }

    public RuntimeView(Context context){
        super(context);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }

    public void setParam(String key, Var value){
        mMapParams.put(key, value);
    }

    public void loadDirect(String rapidID, String xml, int limitLevel, Map<String, Var> mapContext, IListener listener){
        mXml = xml;
        mLimitLevel = limitLevel;
        mRapidID = rapidID;
        mListener = listener;
        mMapContext = mapContext;

        if( listener == null ){
            return;
        }

        if( RapidStringUtils.isEmpty(mRapidID) ){
            listener.onFailed();
            return;
        }

        if( mMapContext == null ){
            mMapContext = new ConcurrentHashMap<String, Var>();
        }

        onFilesReady();
    }

    public void load(String rapidID, String md5, String url, int limitLevel, Map<String, Var> mapContext, IListener listener){
        mRapidID = rapidID;
        mListener = listener;
        mMapContext = mapContext;

        if( RapidStringUtils.isEmpty(rapidID) && !RapidConfig.DEBUG_MODE ){

            if( listener != null ){
                listener.onFailed();
            }

            XLog.d(RapidConfig.RAPID_ERROR_TAG, "rapidID为空");
            return;
        }

        mServerMd5 = md5;
        mServerUrl = url;
        mLimitLevel = limitLevel;

        if( mMapContext == null ){
            mMapContext = new ConcurrentHashMap<String, Var>();
        }

        RapidSandboxWrapper.getInstance().getPackageMD5(rapidID, new RapidSandboxWrapper.IMD5Listener() {
            @Override
            public void onFinish(String md5) {
                try{
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "本地MD5获取完毕");

                    mLock.lock();

                    mLocalMd5 = md5 == null ? "" : md5;

                    prepareFiles();
                }
                finally {
                    mLock.unlock();
                }
            }
        });
    }

    public void load(String rapidID, Map<String, Var> mapContext, IListener listener){
        mRapidID = rapidID;
        mListener = listener;
        mMapContext = mapContext;

        if( RapidStringUtils.isEmpty(rapidID) && !RapidConfig.DEBUG_MODE ){

            if( listener != null ){
                listener.onFailed();
            }

            XLog.d(RapidConfig.RAPID_ERROR_TAG, "rapidID为空");
            return;
        }

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "开始加载实时VIEW，rapidID:" + rapidID);

        if( mMapContext == null ){
            mMapContext = new ConcurrentHashMap<String, Var>();
        }

        RapidSandboxWrapper.getInstance().getPackageMD5(rapidID, new RapidSandboxWrapper.IMD5Listener() {
            @Override
            public void onFinish(String md5) {
                try{
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "本地MD5获取完毕");

                    mLock.lock();

                    mLocalMd5 = md5 == null ? "" : md5;

                    prepareFiles();
                }
                finally {
                    mLock.unlock();
                }
            }
        });

        mEngine.sendRequest(rapidID, new RapidRuntimeEngine.IListener() {
            @Override
            public void onfinish(boolean succeed, String md5, String url, int limitLevel) {

                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "请求实时数据完毕，结果：" + Boolean.toString(succeed));

                if( !succeed ){
                    onFailed();
                }

                try{
                    mLock.lock();

                    mServerMd5 = md5;
                    mServerUrl = url;
                    mLimitLevel = limitLevel;

                    prepareFiles();
                }
                finally {
                    mLock.unlock();
                }
            }
        });
    }

    private void prepareFiles(){
        if( mLocalMd5 == null || mServerMd5 == null || mServerUrl == null ){
            return;
        }

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "比较结果：localMD5:" + mLocalMd5 + "|ServerMD5:" + mServerMd5 + "|ServerUrl:" + mServerUrl);

        if( mLocalMd5.compareToIgnoreCase(mServerMd5) == 0 && !mLocalMd5.isEmpty() ){

            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "MD5结果匹配");

            RapidSandboxWrapper.getInstance().extraPackage(mRapidID, new RapidSandboxWrapper.IExtraListener() {
                @Override
                public void onFinish(boolean succeed) {
                    if( !succeed ){
                        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "解压失败，重新下载文件");
                        downloadZip();
                        return;
                    }

                    onFilesReady();
                }
            });

            return;
        }

        downloadZip();

    }

    private void downloadZip(){
        Map<String, String> mapNameUrl = new ConcurrentHashMap<String, String>();
        Map<String, String> mapNameMD5 = new ConcurrentHashMap<String, String>();

        mapNameUrl.put(mRapidID, mServerUrl);
        mapNameMD5.put(mRapidID, mServerMd5);

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "开始下载文件");

        mWrapper = new RapidDownloadWrapper(new RapidDownload(), mapNameUrl, mapNameMD5);
        mWrapper.download( new RapidDownloadWrapper.ICallback(){

            @Override
            public void onFinish(boolean isSucceed, Map<String, String> mapFilePath) {
                byte[] content = null;
                String tmpPath = null;
                String dstFilePath = FileUtil.getRapidSandBoxDir() + mRapidID + "/" + mRapidID + ".zip";
                File dir = new File(FileUtil.getRapidSandBoxDir() + mRapidID);

                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "文件下载完毕，结果：" + Boolean.toString(isSucceed));

                if( !isSucceed ){
                    onFailed();
                    return;
                }

                tmpPath = mapFilePath.get(mRapidID);
                if( tmpPath == null ){
                    onFailed();
                    return;
                }

                content = FileUtil.readFromFile(tmpPath);
                if( content == null ){
                    onFailed();
                    return;
                }

                dir.mkdirs();

                FileUtil.deleteFile(dstFilePath);
                if( !FileUtil.write2File(content, dstFilePath) ){
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "ZIP解压失败");
                    onFailed();
                    return;
                }

                RapidSandboxWrapper.getInstance().extraPackage(mRapidID, new RapidSandboxWrapper.IExtraListener() {
                    @Override
                    public void onFinish(boolean succeed) {
                        if( !succeed ){
                            onFailed();
                            return;
                        }

                        onFilesReady();
                    }
                });
            }
        });
    }


    private void onFailed(){
        if( RapidConfig.DEBUG_MODE ){
            String path = FileUtil.getRapidDebugDir() + "main.xml";
            if( FileUtil.isFileExists(path) ){
                onFilesReady();
            }
            return;
        }

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "加载失败");

        if( mListener != null ){
            HandlerUtils.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    mListener.onFailed();
                }
            });
        }
    }

    private void onFilesReady(){

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "文件准备完毕，开始加载");

        if( RapidConfig.DEBUG_MODE ){
            mLimitLevel = 0;
        }

        RapidLoader.load(mRapidID,
                          mXml,
                          mLimitLevel == 1 ? true : false,
                          HandlerUtils.getMainHandler(),
                          mContext,
                          RelativeLayoutParams.class,
                          null,
                          new RapidDataBinder(mMapParams),
                          mMapContext,
                          new RapidLoader.IListener() {

                              @Override
                              public void loadFinish(IRapidView rapidView) {
                                  if( rapidView == null || rapidView.getView() == null ){
                                      return;
                                  }

                                  mRapidView = rapidView;

                                  addView(mRapidView.getView(), mRapidView.getParser().getParams().getLayoutParams());

                                  if( mListener != null ){
                                      mListener.onSucceed(mRapidView);
                                  }
                              }
                          });

    }

}
