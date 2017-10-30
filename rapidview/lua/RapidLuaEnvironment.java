package com.tencent.rapidview.lua;

import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.utils.RapidStringUtils;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.Prototype;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidLuaEnvironment
 * @Desc  RapidView运行Lua环境需要的对象数据
 *
 * @author arlozhang
 * @date 2017.03.10
 */
public class RapidLuaEnvironment {

    private Globals mGlobals = null;

    private RapidLuaJavaBridge mJavaBridge = null;

    private Map<String, LuaClosure> mClosureCacheMap = new ConcurrentHashMap<String, LuaClosure>();

    public RapidLuaEnvironment(Globals globals, String rapidID, boolean limitLevel){
        mJavaBridge = new RapidLuaJavaBridge(rapidID);

        mGlobals = globals == null ? RapidLuaLoader.getInstance().createGlobals(rapidID, limitLevel) : globals;
    }


    public RapidLuaJavaBridge getJavaBridge(){
        return mJavaBridge;
    }

    public Globals getGlobals(){
        return mGlobals;
    }

    public LuaClosure getClosure(String name){
        LuaClosure  closure    = null;
        InputStream streamFile = null;
        Prototype   binary     = null;

        if( name == null ){
            return null;
        }

        if( !RapidConfig.DEBUG_MODE ){
            closure = mClosureCacheMap.get(name);
        }

        if( closure != null ){
            return closure;
        }

        streamFile = mGlobals.finder.findResource(name);
        if( streamFile == null ){
            return null;
        }

        try{
            if( isCompiled(name) ){
                binary = mGlobals.loadPrototype(streamFile, name, "b");
            }
            else{
                binary = mGlobals.compilePrototype(streamFile, name);
            }

            closure = new LuaClosure(binary, mGlobals);

            mClosureCacheMap.put(name, closure);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return closure;
    }

    private boolean isCompiled(String name){
        if( RapidStringUtils.isEmpty(name) ){
            return false;
        }

        if( name.length() < 4 ){
            return false;
        }

        if( name.substring(name.length() - 4, name.length() - 1).compareTo(".out") != 0 ){
            return false;
        }

        return true;
    }
}
