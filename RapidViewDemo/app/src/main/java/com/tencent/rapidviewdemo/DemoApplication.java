package com.tencent.rapidviewdemo;

import android.app.Application;

import com.tencent.rapidview.framework.RapidPool;


public class DemoApplication extends Application {

    public static DemoApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        RapidPool.getInstance().initialize(this, null);
    }

    public static Application getInstance() {
        if(mInstance == null) {
            return null;
        }

        return mInstance;
    }
}
