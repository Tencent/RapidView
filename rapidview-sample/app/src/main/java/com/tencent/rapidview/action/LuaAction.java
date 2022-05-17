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
package com.tencent.rapidview.action;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.lua.RapidLuaCaller;
import com.tencent.rapidview.lua.RapidLuaEnvironment;
import com.tencent.rapidview.lua.RapidLuaLoader;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.utils.RapidStringUtils;

import org.luaj.vm2.Globals;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class LuaAction
 * @Desc 调用Lua的Actions
 *
 * @author arlozhang
 * @date 2016.12.06
 */
public class LuaAction extends ActionObject{

    public LuaAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){
        Var file     = mMapAttribute.get("load");
        Var function = mMapAttribute.get("function");
        Var param    = mMapAttribute.get("param1");
        Var param2   = mMapAttribute.get("param2");
        Var param3   = mMapAttribute.get("param3");
        Var type     = mMapAttribute.get("type");

        boolean isTemporary = false;

        if( type != null &&
            (type.getString().compareToIgnoreCase("temp") == 0 ||
             type.getString().compareToIgnoreCase("temporary") == 0) ) {

            isTemporary = true;
        }

        RapidLuaEnvironment luaEnv = getLuaEnvironment();
        RapidParserObject parser = getParser();
        Globals globals;

        if( isTemporary ){
            globals = createGlobals();
        }
        else {
            globals = getGlobals();
        }


        if( luaEnv == null || globals == null ){
            return false;
        }

        if( parser == null ){
            return false;
        }

        if( isTemporary && RapidStringUtils.isEmpty(file) ){
            return false;
        }

        if( isTemporary ){
            RapidLuaLoader.getInstance().load(globals, file.getString(), mRapidView, parser.getJavaInterface());
        }
        else if( !RapidStringUtils.isEmpty(file) ){
            RapidLuaLoader.getInstance().load(luaEnv, file.getString(), mRapidView, parser.getJavaInterface());
        }

        if( !RapidStringUtils.isEmpty(function) ){
            if( param != null && param2 != null && param3 != null ){
                com.tencent.rapidview.lua.RapidLuaCaller.getInstance().call(globals, function.getString(), param.getObject(), param2.getObject(), param3.getObject());
            }
            else if( param != null && param2 != null ){
                com.tencent.rapidview.lua.RapidLuaCaller.getInstance().call(globals, function.getString(), param.getObject(), param2.getObject());
            }
            else if( param != null ){
                com.tencent.rapidview.lua.RapidLuaCaller.getInstance().call(globals, function.getString(), param.getObject());
            }
            else{
                RapidLuaCaller.getInstance().call(globals, function.getString());
            }
        }

        return true;
    }

    protected Globals createGlobals(){
        RapidParserObject parser = getParser();
        if( parser == null ){
            return null;
        }

        return getParser().getLuaEnvironment().createGlobals();
    }
}
