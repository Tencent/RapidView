package com.tencent;

import com.tencent.rapidview.config.RapidViewProvider;
import com.tencent.rapidview.framework.RapidConfig;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by realhe on 2017/12/9.
 */

public class RapidView {
    private static ArrayList<RapidViewProvider> mRapidViewProviders = new ArrayList<>();
    private static HashMap<String,String> mRapidViewMap = new HashMap<>();
    public static void registerProvider(RapidViewProvider provider){
        mRapidViewProviders.remove(provider);
        mRapidViewProviders.add(provider);
    }

    public static boolean unregisterProvider(RapidViewProvider provider){
        return mRapidViewProviders.remove(provider);
    }

    public static void registerView(String viewName,String xmlName){
        RapidConfig.msMapViewNaitve.put(viewName,xmlName);
    }

    public static void unregisterView(String viewName){
        RapidConfig.msMapViewNaitve.remove(viewName);
    }

    public static ArrayList<RapidViewProvider> getProviders(){
        return mRapidViewProviders;
    }
}
