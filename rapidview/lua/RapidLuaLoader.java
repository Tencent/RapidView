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

import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.utils.LuaResourceFinder;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.RapidThreadPool;
import com.tencent.rapidview.utils.XLog;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.lib.jse.LuajavaLib;

import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @Class RapidLuaLoader
 * @Desc RapidView Lua加载器，用于lua文件加载
 *
 * @author arlozhang
 * @date 2016.12.05
 */
public class RapidLuaLoader {

    private static RapidLuaLoader msInstance;

    private static BlockingQueue<Globals> msLimitGlobalsQueue = new ArrayBlockingQueue<Globals>(10);

    private static BlockingQueue<Globals> msGlobalsQueue = new ArrayBlockingQueue<Globals>(10);

    private RapidLuaLoader(){
        cacheLimitGlobal();
        cacheGlobal();
    }

    public static RapidLuaLoader getInstance(){

        if( msInstance == null ){
            msInstance = new RapidLuaLoader();
        }

        return msInstance;
    }

    private Globals createLimitGlobals(){
        Globals globals = new Globals();

        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new CoroutineLib());
        globals.load(new JseMathLib());
        globals.load(new JseOsLib());
        globals.load(new LuajavaLib(RapidLuaLimitPath.msLimitArray, null));

        LoadState.install(globals);
        LuaC.install(globals);

        return globals;
    }

    public Globals createGlobals(String rapidID, boolean limitLevel){

        Globals globals;

        LuaResourceFinder finder = null;

        if( limitLevel ){
            try{
                globals = msLimitGlobalsQueue.take();
            }
            catch (InterruptedException e){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "读取LimitGlobal抛出异常");
                e.printStackTrace();
                globals = createLimitGlobals();
            }
        }
        else{
            try{
                globals = msGlobalsQueue.take();
            }
            catch (InterruptedException e){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "读取Global抛出异常");
                e.printStackTrace();
                globals = JsePlatform.standardGlobals();
            }
        }

        finder = new LuaResourceFinder();

        finder.setLimitLevel(limitLevel);
        finder.setRapidID(rapidID);

        globals.finder = finder;

        return globals;
    }

    private void cacheLimitGlobal(){
        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {

                while (true){

                    try{
                        msLimitGlobalsQueue.put(createLimitGlobals());
                    }
                    catch (InterruptedException e){
                        XLog.d(RapidConfig.RAPID_ERROR_TAG, "缓存LimitGlobal抛出异常");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void cacheGlobal(){
        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {

                while (true){

                    try{
                        msGlobalsQueue.put(JsePlatform.standardGlobals());
                    }
                    catch (InterruptedException e){
                        XLog.d(RapidConfig.RAPID_ERROR_TAG, "缓存Global抛出异常");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public boolean load(RapidLuaEnvironment luaEnv, String name, Object... args){

        LuaClosure closure = null;


        if( RapidStringUtils.isEmpty(name) || luaEnv == null ){
            return false;
        }

        try{
            closure = luaEnv.getClosure(name);

            if( args.length == 0 ){
                closure.call();
            }
            else if( args.length == 1 ){
                closure.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]) );
            }
            else if( args.length == 2 ){
                closure.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]),
                              args[1] instanceof LuaValue ? (LuaValue) args[1] : CoerceJavaToLua.coerce(args[1]) );
            }
            else{
                closure.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]),
                              args[1] instanceof LuaValue ? (LuaValue) args[1] : CoerceJavaToLua.coerce(args[1]),
                              args[2] instanceof LuaValue ? (LuaValue) args[2] : CoerceJavaToLua.coerce(args[2]) );
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }
    public boolean load(Globals globals, String name, Object... args){

        LuaClosure closure = null;

        if( RapidStringUtils.isEmpty(name) || globals == null ){
            return false;
        }

        try{
            closure = getClosure(globals, name);

            if( args.length == 0 ){
                closure.call();
            }
            else if( args.length == 1 ){
                closure.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]) );
            }
            else if( args.length == 2 ){
                closure.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]),
                        args[1] instanceof LuaValue ? (LuaValue) args[1] : CoerceJavaToLua.coerce(args[1]) );
            }
            else{
                closure.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]),
                        args[1] instanceof LuaValue ? (LuaValue) args[1] : CoerceJavaToLua.coerce(args[1]),
                        args[2] instanceof LuaValue ? (LuaValue) args[2] : CoerceJavaToLua.coerce(args[2]) );
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    private LuaClosure getClosure(Globals global, String name){
        LuaClosure  closure    = null;
        InputStream streamFile = null;
        Prototype binary     = null;

        if( name == null ){
            return null;
        }

        if( global == null ){
            return null;
        }

        streamFile = global.finder.findResource(name);
        if( streamFile == null ){
            return null;
        }

        try{
            if( RapidLuaEnvironment.isCompiled(name) ){
                binary = global.loadPrototype(streamFile, name, "b");
            }
            else{
                binary = global.compilePrototype(streamFile, name);
            }

            closure = new LuaClosure(binary, global);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return closure;
    }
}