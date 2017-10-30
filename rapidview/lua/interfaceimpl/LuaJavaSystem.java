package com.tencent.rapidview.lua.interfaceimpl;

import com.tencent.rapidview.deobfuscated.IRapidView;

/**
 * @Class LuaJavaSystem
 * @Desc 提供系统数据
 *
 * @author arlozhang
 * @date 2017.02.22
 */

public class LuaJavaSystem extends RapidLuaJavaObject {

    public LuaJavaSystem(String rapidID, IRapidView rapidView) {
        super(rapidID, rapidView);
    }

    public String getServerTime(){
        return "";
    }
}
