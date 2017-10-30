package com.tencent.rapidview.server;

import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.framework.RapidEnv;
import com.tencent.rapidview.framework.RapidPool;
import com.tencent.rapidview.utils.FileUtil;
import com.tencent.rapidview.utils.RapidSkinFile;
import com.tencent.rapidview.utils.XLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private RapidDownloadWrapper mWrapper = null;

    private interface ILoadCallback{
        void onFinish(boolean bSucc);
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

    private void loadUpdateFile(List<RapidSkinFile> fileList, final ILoadCallback callback){
        Map<String, String> mapNameUrl = new ConcurrentHashMap<String, String>();
        Map<String, String> mapNameMD5 = new ConcurrentHashMap<String, String>();
        Map<String, RapidSkinFile> mapNameRapidFile = new ConcurrentHashMap<String, RapidSkinFile>();

        if( fileList == null ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "没有更新的文件");
            return;
        }

        for( int i = 0; i < fileList.size(); i++ ){
            RapidSkinFile file = fileList.get(i);

            if( file.fileName == null ||
                file.fileVer == null ||
                file.fileMd5 == null ||
                file.fileUrl == null ){
                continue;
            }

            mapNameUrl.put(file.fileName, file.fileUrl);
            mapNameMD5.put(file.fileName, file.fileMd5);
            mapNameRapidFile.put(file.fileName, file);
        }

        final Map<String, RapidSkinFile> fmapNameRapidFile  = mapNameRapidFile;

        mWrapper = new RapidDownloadWrapper(new RapidDownload(), mapNameUrl, mapNameMD5);
        mWrapper.download(new RapidDownloadWrapper.ICallback() {

            @Override
            public void onFinish(boolean isSucceed, Map<String, String> mapFilePath) {
                List<RapidPool.RapidFile> list = new ArrayList<RapidPool.RapidFile>();
                boolean bRet = false;

                try{
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "下载文件完毕：结果：" + Boolean.toString(isSucceed));

                    if( !isSucceed ){
                        return;
                    }

                    for( Map.Entry<String, String> entry : mapFilePath.entrySet() ){

                        RapidPool.RapidFile file = new RapidPool.RapidFile();

                        if( entry.getKey() == null || entry.getValue() == null ){
                            continue;
                        }

                        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "已下载文件：" + entry.getKey() + ":" + entry.getValue());

                        file.name = entry.getKey();
                        file.content = FileUtil.readFromFile(entry.getValue());
                        file.version = fmapNameRapidFile.get(entry.getKey()).fileVer;
                        file.md5 = fmapNameRapidFile.get(entry.getKey()).fileMd5;
                        file.isView = fmapNameRapidFile.get(entry.getKey()).fileType == 2;

                        if( file.name == null ||
                            file.content == null ||
                            file.version == null ||
                            file.md5 == null ){
                            continue;
                        }

                        list.add(file);
                    }

                    mUpdateFileList = list;

                    bRet = true;
                }
                catch (Exception e){
                    e.printStackTrace();
                    bRet = false;
                }
                finally {
                    callback.onFinish(bRet);
                    mWrapper = null;
                }
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
