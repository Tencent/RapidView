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
import com.tencent.rapidview.framework.RapidEnv;
import com.tencent.rapidview.framework.RapidPool;
import com.tencent.rapidview.utils.FileUtil;
import com.tencent.rapidview.utils.MD5;
import com.tencent.rapidview.utils.RapidSkinFile;
import com.tencent.rapidview.utils.RapidSmallFileBatchDownloader;
import com.tencent.rapidview.utils.XLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Class RapidUpdate
 * @Desc 解析并下载更新的文件和视图信息
 *
 * @author arlozhang
 * @date 2016.04.26
 */
public class RapidUpdate {

    List<RapidPool.RapidFile> mUpdateFileList = null;

    List<String> mDeleteFileList = null;

    private static RapidUpdate mInstance = null;

    private volatile boolean mIsUpdating = false;

    private interface ILoadCallback{
        void onFinish(boolean bSucc);
    }

    private RapidUpdate(){
    }

    public static RapidUpdate getInstance(){
        if( mInstance == null ){
            mInstance = new RapidUpdate();
        }

        return mInstance;
    }

    public void load( List<RapidSkinFile> updateFileList,
                                   List<RapidSkinFile> deleteFileList ){
        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "准备更新数据");

        synchronized (this){
            if( mIsUpdating ){
                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "已在更新中，返回");
                return;
            }

            mIsUpdating = true;
        }

        try{
            loadDeleteFile(deleteFileList);
            loadUpdateFile(updateFileList, new ILoadCallback() {
                @Override
                public void onFinish(boolean bSucc) {
                    try{
                        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "下拉文件完毕，结果：" + Boolean.toString(bSucc));
                        if( !bSucc ){
                            return;
                        }

                        RapidPool.getInstance().update(RapidEnv.getApplication(),
                                mUpdateFileList, mDeleteFileList);

                    }
                    finally {
                        synchronized (RapidUpdate.this){
                            mIsUpdating = false;
                        }
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();

            synchronized (this){
                mIsUpdating = false;
            }
        }

    }

    private void loadUpdateFile(final List<RapidSkinFile> fileList, final ILoadCallback callback){
        List<String> ticketList = new ArrayList<String>();
        List<String> urlList = new ArrayList<String>();
        List<String> md5List = new ArrayList<String>();

        if( fileList == null ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "没有更新的文件");
            return;
        }

        for( int i = 0; i < fileList.size(); i++ ){
            RapidSkinFile node = fileList.get(i);

            ticketList.add(node.fileMd5);
            urlList.add(node.fileUrl);
            md5List.add(node.fileMd5);
        }

        RapidSmallFileBatchDownloader.getInstance().download(ticketList, urlList, md5List, new RapidSmallFileBatchDownloader.IListener() {
            @Override
            public void onFinish(boolean bSuccess, List<String> ticketList, List<String> filePathList) {
                List<RapidPool.RapidFile> list = null;

                if( !bSuccess || fileList.size() != filePathList.size() ){
                    XLog.d(RapidConfig.RAPID_ERROR_TAG, "下载失败或文件长度不匹配，下载结果：" + Boolean.toString(bSuccess) +
                            "|文件目录长度/下载列表长度" + Integer.toString(fileList.size())
                            + "/" + Integer.toString(filePathList.size()));
                    callback.onFinish(false);
                    return;
                }

                list = new ArrayList<RapidPool.RapidFile>();

                for( int i = 0; i < fileList.size(); i++ ){
                    RapidPool.RapidFile file = new RapidPool.RapidFile();
                    RapidSkinFile node = fileList.get(i);

                    try{
                        if( node.fileMd5.compareToIgnoreCase(MD5.getFileMD5(new File(filePathList.get(i)))) != 0 ){
                            XLog.d(RapidConfig.RAPID_ERROR_TAG, "文件再次校验时发现MD5不匹配：" + node.fileName);
                            callback.onFinish(false);
                            return;
                        }
                    }
                    catch (Exception e){
                        XLog.d(RapidConfig.RAPID_ERROR_TAG, "文件校验是抛出异常：" + node.fileName);
                        e.printStackTrace();
                    }

                    file.name = node.fileName;
                    file.content = FileUtil.readFromFile(filePathList.get(i));
                    file.version = node.fileVer;
                    file.md5 = node.fileMd5;
                    file.isView = node.fileType == 2;

                    list.add(file);
                }

                mUpdateFileList = list;

                callback.onFinish(true);
            }
        });
    }

    private void loadDeleteFile(List<RapidSkinFile> fileList){
        mDeleteFileList = new ArrayList<String>();

        if( fileList == null ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "没有需要删除的文件");
            return;
        }

        for( int i = 0; i < fileList.size(); i++ ){
            RapidSkinFile file = fileList.get(i);

            if( file.fileName == null ){
                continue;
            }

            mDeleteFileList.add(file.fileName);
        }
    }
}
