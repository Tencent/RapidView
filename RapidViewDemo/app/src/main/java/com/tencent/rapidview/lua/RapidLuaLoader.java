/***************************************************************************************************
 Tencent is pleased to support the open source community by making RapidView available.
 Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 Licensed under the MITLicense (the "License"); you may not use this file except in compliance
 withthe License. You mayobtain a copy of the License at

 http://opensource.org/licenses/MIT

 Unlessrequired by applicable law or agreed to in writing, software distributed under the License is
 distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied. See the License for the specific language governing permissions and limitations under the
 License.
 ***************************************************************************************************/
package com.tencent.rapidview.lua;

import com.tencent.rapidview.utils.LuaResourceFinder;
import com.tencent.rapidview.utils.RapidStringUtils;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;
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

/**
 * @Class RapidLuaLoader
 * @Desc RapidView Lua加载器，用于lua文件加载
 *
 * @author arlozhang
 * @date 2016.12.05
 */
public class RapidLuaLoader {

    private static RapidLuaLoader msInstance;

    private RapidLuaLoader(){}

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
            globals = createLimitGlobals();
        }
        else{
            globals = JsePlatform.standardGlobals();
        }

        finder = new LuaResourceFinder();

        finder.setLimitLevel(limitLevel);
        finder.setRapidID(rapidID);

        globals.finder = finder;

        return globals;
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
}
