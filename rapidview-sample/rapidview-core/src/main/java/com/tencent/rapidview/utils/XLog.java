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

import com.tencent.rapidview.framework.RapidConfig;

public class XLog {

    // Log.d
    public static void d(String tag, String msg) {
        if (RapidConfig.TEST_MODE) {
            msg = buildMsg(msg);
            StringBuilder sb = new StringBuilder();
            sb.append(msg);
            msg = sb.toString();
            android.util.Log.d(tag, msg);
        }
    }

    /**
     * 将一些日志附加的信息加上，方便日志输出更多的信息
     * @param msg
     * @return
     */
    public static String buildMsg(String msg) {
        try {
            StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
            String caller = "<unknown>";
            for (int i = 2; i < trace.length; i++) {
                Class<?> clazz = trace[i].getClass();
                if (!clazz.equals(XLog.class)) {
                    String callingClass = trace[i].getClassName();
                    try {
                        callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                        callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                    } catch (NoSuchMethodError e) {

                    }
                    caller = callingClass + "." + trace[i].getMethodName()+ "("+trace[i].getLineNumber()+")";
                    break;
                }
            }
            return String.format( "[%s:%d] %s: %s",Thread.currentThread().getName(), Thread.currentThread().getId(), caller, msg);
        } catch (Throwable t) {
            System.gc();
        }
        return "";
    }

}
