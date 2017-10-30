package com.tencent.rapidview.framework;

import android.content.Context;

/**
 * @Class RapidEnv
 * @Desc 用于存储外部全局环境数据
 *
 * @author arlozhang
 * @date 2017.06.29
 */
public class RapidEnv {

    private static Context mApplication = null;

    public static void setApplication(Context context){
        mApplication = context;
    }

    public static Context getApplication(){
        return mApplication;
    }
}
