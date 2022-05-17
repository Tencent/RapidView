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

import com.tencent.rapidview.deobfuscated.IRapidView;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.Map;

/**
 * @Class RapidLuaLib
 * @Desc  用于提供光子内置参数能力
 *
 * @author arlozhang
 * @date 2018.05.18
 */
public class RapidLuaLib extends TwoArgFunction {

    private IRapidView mRapidView = null;

    private Map<String, String> mMapEnvironment = null;

    public RapidLuaLib(IRapidView photonView, Map<String, String> mapEnv){
        mRapidView = photonView;
        mMapEnvironment = mapEnv;
    }


    @Override
    public LuaValue call(LuaValue var1, LuaValue var2) {
        LuaTable functions = new LuaTable();

        functions.set("getRapidView", new GetPhotonView());
        functions.set("getJavaBridge", new GetJavaBridge());
        functions.set("getEnvironment", new GetEnvironment());

        var2.set("rapid", functions);
        var2.get("package").get("loaded").set("rapid", functions);

        return functions;
    }


    private final class GetPhotonView extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            return CoerceJavaToLua.coerce(mRapidView);
        }
    }

    private final class GetJavaBridge extends ZeroArgFunction{

        @Override
        public LuaValue call() {
            return CoerceJavaToLua.coerce(mRapidView.getParser().getJavaInterface());
        }
    }

    private final class GetEnvironment extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue luaValue) {
            String key = luaValue.tojstring();
            String value;

            if( mMapEnvironment == null ){
                return LuaString.valueOf("");
            }

            if( key == null ){
                return LuaString.valueOf("");
            }

            value = mMapEnvironment.get(key);
            if( value == null ){
                return LuaString.valueOf("");
            }

            return LuaString.valueOf(value);
        }
    }
}
