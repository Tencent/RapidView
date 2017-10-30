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
