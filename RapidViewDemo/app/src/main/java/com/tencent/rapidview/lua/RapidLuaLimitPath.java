package com.tencent.rapidview.lua;


/**
 * @Class RapidLuaLimitPath
 * @Desc RapidView Lua调用反射、代理内容过滤
 *
 * @author arlozhang
 * @date 2015.09.22
 */
public class RapidLuaLimitPath {

    public static String[] msLimitArray = new String[]{
            "android.view.",
            "android.widget.",
            "android.graphics.",
    };
}
