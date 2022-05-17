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
package com.tencent.rapidview.lua;

import android.os.Looper;

import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.RapidThreadPool;

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

    private String mRapidID;

    private boolean mLimitLevel = false;

    private RapidLuaJavaBridge mJavaBridge = null;

    private RapidXmlLuaCenter mXmlLuaCenter = new RapidXmlLuaCenter(this);

    private Map<String, LuaClosure> mClosureCacheMap = new ConcurrentHashMap<String, LuaClosure>();

    public RapidLuaEnvironment(Globals globals, String rapidID, boolean limitLevel){
        mJavaBridge = new RapidLuaJavaBridge(rapidID);
        mRapidID = rapidID;
        mLimitLevel = limitLevel;

        if( globals != null ){
            mGlobals = globals;
        }
        else{
            RapidThreadPool.get().execute(new Runnable() {
                @Override
                public void run() {
                    initGlobals();
                }
            });
        }
    }

    public RapidXmlLuaCenter getXmlLuaCenter(){
        return mXmlLuaCenter;
    }


    public RapidLuaJavaBridge getJavaBridge(){
        return mJavaBridge;
    }

    public Globals getGlobals(){
        if( mGlobals == null ){
            initGlobals();
        }

        return mGlobals;
    }

    public Globals createGlobals(){
        return RapidLuaLoader.getInstance().createGlobals(mRapidID, mLimitLevel);
    }

    public void initClosure(final String name){

        if( Looper.myLooper() != Looper.getMainLooper() ){
            getClosure(name);
        }
        else{
            RapidThreadPool.get().execute(new Runnable() {
                @Override
                public void run() {
                    getClosure(name);
                }
            });
        }
    }

    public LuaClosure getClosure(String name){
        LuaClosure  closure    = null;
        InputStream streamFile = null;
        Prototype   binary     = null;

        if( name == null ){
            return null;
        }

        if( mGlobals == null ){
            initGlobals();
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

    public static boolean isCompiled(String name){
        if( RapidStringUtils.isEmpty(name) ){
            return false;
        }

        if( name.length() < 4 ){
            return false;
        }

        if( name.substring(name.length() - 4, name.length()).compareTo(".out") != 0 ){
            return false;
        }

        return true;
    }

    private synchronized void initGlobals(){
        if( mGlobals != null ){
            return;
        }

        Globals globals = createGlobals();
        mGlobals = globals;
    }
}
