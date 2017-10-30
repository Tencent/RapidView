package com.tencent.rapidview.server;


import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.utils.XLog;

/**
 * @Class RapidRuntimeEngine
 * @Desc 实时视图请求
 *
 * @author arlozhang
 * @date 2016.02.17
 */
public class RapidRuntimeEngine {

    private IListener mListener = null;

    public interface IListener{

        void onfinish(boolean succeed, String md5, String url, int limitLevel);
    }

    public synchronized int sendRequest(String rapidID, IListener listener){
        return -1;
    }

    protected void onRequestFailed(final int seq, final int errorCode) {
        XLog.d(RapidConfig.RAPID_ERROR_TAG, "实时视图数据协议请求失败，errorcode：" + Integer.toString(errorCode));

        if( mListener == null ){
            return;
        }

        mListener.onfinish(false, null, null, -1);
    }

    protected synchronized void onRequestSuccessed(int seq, Object resp) {

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "实时视图数据协议请求成功");

        if( mListener == null ){
            return;
        }

        //mListener.onfinish(true, resp.zipMd5, resp.zipUrl, resp.limitLevel);
    }
}
