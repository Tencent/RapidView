package com.tencent.rapidview.utils;

import android.os.Process;

import com.tencent.rapidview.framework.RapidConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Class RapidSmallFileBatchDownloader
 * @Desc 小文件后台批量下载器
 *
 * @author arlozhang
 * @date 2018.02.26
 */
public class RapidSmallFileBatchDownloader {

    private class CACHE_NODE{

        public ByteArrayOutputStream body = null;

        public String ticket = null;
    }

    private static RapidSmallFileBatchDownloader msInstance;

    private static final int MAX_CACHE_SIZE = 1 * 1024 * 1024;

    private volatile boolean mIsDownloading = false;

    private long mCacheSize = 0;

    private long mLastCacheTime = 0;

    private List<CACHE_NODE> mMemoryCache = new ArrayList<CACHE_NODE>();

    public interface IListener{

        void onFinish(boolean bSuccess, List<String> ticketList, List<String> filePathList);
    }

    private RapidSmallFileBatchDownloader(){}

    public static RapidSmallFileBatchDownloader getInstance(){

        if( msInstance == null ){
            msInstance = new RapidSmallFileBatchDownloader();
        }

        return msInstance;
    }



    public synchronized boolean download(final List<String> ticketList, final List<String> urlList, final List<String> md5List, final IListener listener){

        if( mIsDownloading ){
            return false;
        }

        if( ticketList == null || urlList == null || listener == null || ticketList.size() != urlList.size() || ticketList.size() != md5List.size() ){
            return false;
        }

        mIsDownloading = true;

        initialize();

        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient   = new DefaultHttpClient();
                boolean    isFinish     = false;
                List<String> listPath   = new ArrayList<String>();
                List<String> listTicket = new ArrayList<String>();

                Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);

                for( int i = 0; i < ticketList.size(); i++ ){

                    for( int j = 0; j < 3; j++ ){
                        if( FileUtil.isFileExists(FileUtil.getRapidTemporaryDir() + ticketList.get(i)) ){
                            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "文件已存在，跳过下载：" + ticketList.get(i));
                            break;
                        }

                        try{
                            HttpGet      httpGet      = new HttpGet(urlList.get(i));
                            HttpResponse httpResponse = null;
                            InputStream  inputStream  = null;
                            HttpEntity   httpEntity   = null;
                            long         totalLength  = 0;
                            int          readLength   = 0;
                            long         readedLength = 0;
                            CACHE_NODE   cacheNode    = new CACHE_NODE();
                            byte[]       buffer       = new byte[4096];
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                            httpResponse = httpClient.execute(httpGet);

                            httpEntity = httpResponse.getEntity();
                            totalLength = httpEntity.getContentLength();
                            inputStream = httpEntity.getContent();


                            while( (readLength = inputStream.read(buffer)) > 0 ){
                                outputStream.write(buffer, 0 , readLength);
                                readedLength += readLength;
                            }

                            if( totalLength != readedLength ){
                                XLog.d(RapidConfig.RAPID_ERROR_TAG, "文件下载长度不符：" + ticketList.get(i));
                                break;
                            }

                            mCacheSize += totalLength;

                            cacheNode.body = outputStream;
                            cacheNode.ticket = ticketList.get(i);

                            mMemoryCache.add(cacheNode);

                            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "已完成文件下载，并保存到内存中：" + ticketList.get(i));

                            writeFileToStorage(false);

                            break;
                        }
                        catch (Exception e){
                            XLog.d(RapidConfig.RAPID_ERROR_TAG, "下载文件发生异常：" + ticketList.get(i));
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(4000);
                        }
                        catch (Exception e){
                            XLog.d(RapidConfig.RAPID_ERROR_TAG, "线程等待失败，将立即开始下载：" + ticketList.get(i));
                            e.printStackTrace();
                        }

                    }


                    if( i == ticketList.size() - 1 ){
                        isFinish = true;
                    }
                }

                try{
                    httpClient.getConnectionManager().shutdown();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                writeFileToStorage(true);

                try{
                    for( int i = 0; i < ticketList.size(); i++ ){

                        if( md5List.get(i).compareToIgnoreCase(MD5.getFileMD5(new File(FileUtil.getRapidTemporaryDir() + ticketList.get(i)))) == 0 ){

                            listPath.add(FileUtil.getRapidTemporaryDir() + ticketList.get(i));
                            listTicket.add(ticketList.get(i));

                            continue;
                        }

                        FileUtil.deleteFileOrDir(FileUtil.getRapidTemporaryDir() + ticketList.get(i));
                        isFinish = false;
                        XLog.d(RapidConfig.RAPID_ERROR_TAG, "文件与MD5不匹配：" + ticketList.get(i));
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                    XLog.d(RapidConfig.RAPID_ERROR_TAG, "文件校验时找不到并抛出异常");
                    isFinish = false;
                }

                listener.onFinish(isFinish, listTicket, listPath);

                mIsDownloading = false;

                if( isFinish ){
                    clear();
                }
            }
        });

        return true;
    }

    public void clear(){
        if( mIsDownloading ){
            return;
        }

        FileUtil.deleteFileOrDir(FileUtil.getRapidTemporaryDir());
    }

    private void writeFileToStorage(boolean isForce){

        if( mCacheSize < MAX_CACHE_SIZE && System.currentTimeMillis() - mLastCacheTime < 20 * 1000 && !isForce ){
            return;
        }

        for( int i = 0; i < mMemoryCache.size(); i++ ){
            CACHE_NODE node = mMemoryCache.get(i);
            FileOutputStream fileOutputStream = null;

            try{
                if( FileUtil.isFileExists(FileUtil.getRapidTemporaryDir() + node.ticket) ){
                    XLog.d(RapidConfig.RAPID_ERROR_TAG, "文件已存在，无法写入缓存：" + node.ticket);
                    continue;
                }

                fileOutputStream = new FileOutputStream(new File(FileUtil.getRapidTemporaryDir() + node.ticket));

                node.body.writeTo(fileOutputStream);

                node.body.flush();
                node.body.close();
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "已将内存保存的文件同步到磁盘中");

        mLastCacheTime = System.currentTimeMillis();
        mCacheSize = 0;
        mMemoryCache.clear();
    }

    private void initialize(){

        mCacheSize = 0;

        mLastCacheTime = System.currentTimeMillis();

        mMemoryCache.clear();
    }

}
