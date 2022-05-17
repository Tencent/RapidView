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
