package com.tencent.rapidview.server;

import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.utils.XLog;

/**
 * @Class RapidUpdateEngine
 * @Desc 更新文件后台接口。
 *
 * @author arlozhang
 * @date 2015.10.14
 */
public class RapidUpdateEngine {

    public static RapidUpdateEngine msInstance;

    public static synchronized RapidUpdateEngine getInstance(){
        if(msInstance == null){
            msInstance = new RapidUpdateEngine();
        }
        return msInstance;
    }


    public synchronized int sendRequest(){
        return -1;
    }


    protected void onRequestFailed(final int seq, final int errorCode, Object request, Object response) {
        XLog.d(RapidConfig.RAPID_ERROR_TAG, "协议请求失败，errorcode：" + Integer.toString(errorCode));
    }


    protected synchronized void onRequestSuccessed(int seq, Object request, Object response) {
    }
}
