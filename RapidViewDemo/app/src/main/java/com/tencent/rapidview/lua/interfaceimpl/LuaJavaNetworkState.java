package com.tencent.rapidview.lua.interfaceimpl;

import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.NetworkUtil;

/**
 * @Class LuaJavaNetworkState
 * @Desc 网络状态判断
 *
 * @author arlozhang
 * @date 2017.03.31
 */
public class LuaJavaNetworkState extends RapidLuaJavaObject {

    public LuaJavaNetworkState(String rapidID, IRapidView rapidView){
        super(rapidID, rapidView);
    }

    public boolean isNetworkActive(){
        return NetworkUtil.isNetworkActive();
    }

    public boolean isWap(){
        return NetworkUtil.isWap();
    }

    public boolean isWifi(){
        return NetworkUtil.isWifi();
    }

    public boolean is2G(){
        return NetworkUtil.is2G();
    }

    public boolean is3G(){
        return NetworkUtil.is3G();
    }

    public boolean is4G(){
        return NetworkUtil.is4G();
    }
}
