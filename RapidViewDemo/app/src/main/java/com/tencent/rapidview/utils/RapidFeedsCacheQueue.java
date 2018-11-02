package com.tencent.rapidview.utils;

import android.os.Process;

import com.tencent.rapidview.deobfuscated.utils.IRapidFeedsCacheQueue;
import com.tencent.rapidview.framework.RapidConfig;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Class RapidFeedsCacheQueue
 * @Desc feeds列表页无感滑动的缓存队列
 *
 * @author arlozhang
 * @date 2018.09.14
 */
public class RapidFeedsCacheQueue implements IRapidFeedsCacheQueue{

    private BlockingQueue<Object> mCacheQueue = null;

    private IRequestInterface mReqInterface = null;

    private Boolean mLastSucceed = false;

    private Boolean mIsFinish = false;

    private Object mLastRespStub = null;

    private Object mLastResp = null;

    private Object mWaitLock = new Object();

    private long mRetryIntervalSec = 0;

    public RapidFeedsCacheQueue(int cacheCount, Object reqStub) {

        mCacheQueue = new LinkedBlockingQueue<Object>(cacheCount);

        mLastRespStub = reqStub;
        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "创建缓存队列");
    }

    @Override
    public void start(IRequestInterface reqInterface){

        if( mReqInterface != null || reqInterface == null ){
            return;
        }

        mReqInterface = reqInterface;

        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {

                Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);

                while (true) {
                    try{
                        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "发起缓存请求");

                        synchronized (mWaitLock){
                            mReqInterface.request( mLastRespStub, RapidFeedsCacheQueue.this);
                            mWaitLock.wait();
                        }

                        try{
                            mCacheQueue.put(mLastResp);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        if( mIsFinish ){
                            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "终止请求，请求完成");
                            break;
                        }

                        if( !mLastSucceed ){
                            continue;
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void finish(Boolean succeed, Boolean isFinish, Object resp, Object stub){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "完成缓存请求");
            mLastSucceed = succeed;

            if( succeed ){

                mIsFinish = isFinish;
                mLastRespStub = stub;
                mRetryIntervalSec = 0;
                mLastResp = resp;
            }
            else{
                mRetryIntervalSec = mRetryIntervalSec * 2 + 1;
            }

            synchronized (mWaitLock) {
                mWaitLock.notify();
            }
    }

    public void get(final IResponseInterface respInterface){

        if( mCacheQueue.size() != 0 ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "请求缓存数据");
            _get(respInterface);
        }
        else{
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "缓存数据为空，等待网络数据缓存");
            RapidThreadPool.get().execute(new Runnable() {
                @Override
                public void run() {
                    _get(respInterface);
                }
            });
        }
    }

    private void _get(final IResponseInterface respInterface){
        final Object finalResp;
        Object resp = null;

        try {
            resp = mCacheQueue.take();
        }
        catch (InterruptedException e){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "获取缓存数据异常");
            e.printStackTrace();
        }

        finalResp = resp;

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "获取到缓存数据");

        if( respInterface != null ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "返回缓存数据");
            HandlerUtils.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    respInterface.onGetResponse(finalResp);
                }
            });

        }
    }
}
