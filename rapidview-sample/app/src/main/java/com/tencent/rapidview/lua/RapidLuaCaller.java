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

import com.tencent.rapidview.utils.RapidStringUtils;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * @Class RapidLuaCaller
 * @Desc 用于调用lua方法
 *
 * @author arlozhang
 * @date 2016.12.06
 */
public class RapidLuaCaller {

    private static RapidLuaCaller msInstance;

    private RapidLuaCaller(){}

    public static RapidLuaCaller getInstance(){

        if( msInstance == null ){
            msInstance = new RapidLuaCaller();
        }

        return msInstance;
    }

    public LuaValue call(Globals globals, String name, Object... args){
        LuaValue function = null;
        LuaValue ret = null;

        if( globals == null || RapidStringUtils.isEmpty(name) ){
            return ret;
        }

        function = globals.get(name);
        if( function.isnil() ){
            return ret;
        }

        try{
            if( args == null || args.length == 0 ){
                ret = function.call();
            }
            else if( args.length == 1 ){
                ret = function.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]) );
            }
            else if( args.length == 2 ){
                ret = function.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]),
                                     args[1] instanceof LuaValue ? (LuaValue) args[1] : CoerceJavaToLua.coerce(args[1]) );
            }
            else{
                ret = function.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]),
                                     args[1] instanceof LuaValue ? (LuaValue) args[1] : CoerceJavaToLua.coerce(args[1]),
                                     args[2] instanceof LuaValue ? (LuaValue) args[2] : CoerceJavaToLua.coerce(args[2]) );
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return ret;
        }

        return ret;
    }

    public boolean call(LuaFunction function, Object... args){


        if( function == null || function.isnil() ){
            return false;
        }

        try{
            if( args == null || args.length == 0 ){
                function.call();
            }
            else if( args.length == 1 ){
                function.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]) );
            }
            else if( args.length == 2 ){
                function.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]),
                               args[1] instanceof LuaValue ? (LuaValue) args[1] : CoerceJavaToLua.coerce(args[1]) );
            }
            else{
                function.call( args[0] instanceof LuaValue ? (LuaValue) args[0] : CoerceJavaToLua.coerce(args[0]),
                               args[1] instanceof LuaValue ? (LuaValue) args[1] : CoerceJavaToLua.coerce(args[1]),
                               args[2] instanceof LuaValue ? (LuaValue) args[2] : CoerceJavaToLua.coerce(args[2]) );
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
