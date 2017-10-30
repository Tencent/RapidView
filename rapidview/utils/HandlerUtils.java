package com.tencent.rapidview.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HandlerUtils {
	
	private static Handler mManinHandler;
	
	private static Object mMainHandlerLock = new Object();

	public static Handler getMainHandler(){
        synchronized (mMainHandlerLock) {
            if (mManinHandler == null) {
                mManinHandler = new Handler(Looper.getMainLooper());
            }

		    return mManinHandler;
        }
	}
	
	private static Map<String, Handler> mHandlerMap = Collections.synchronizedMap(new HashMap<String, Handler>());
	

	public static Handler getHandler(String threadName){
		if(TextUtils.isEmpty(threadName)){
			threadName = "rapidview-default-thread";
		}

		Handler handler = null;
		if (mHandlerMap.containsKey(threadName)){
			handler =  mHandlerMap.get(threadName);
		} else {
            try {
                HandlerThread handlerThread = new HandlerThread(threadName);
                handlerThread.start();
                Looper loop = handlerThread.getLooper();
                if( loop != null){
                    handler = new Handler(loop);
                    mHandlerMap.put(threadName, handler);
                } else {
                    handlerThread.quit();
                    handlerThread = null;
                }
            } catch (StackOverflowError ignore) {
                ignore.printStackTrace();
                // TODO: why java.lang.StackOverflowError: thread creation failed? crash:206955020
            }
		}

		return handler;
	}
}
