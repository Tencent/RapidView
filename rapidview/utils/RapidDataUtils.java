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
package com.tencent.rapidview.utils;

import com.tencent.rapidview.data.Var;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RapidDataUtils {

    public static Map<String, Var> translateData(LuaTable data){
        LuaValue key = LuaValue.NIL;
        LuaValue value = LuaValue.NIL;
        Map<String, Var> map = new ConcurrentHashMap<String, Var>();

        if( data == null || !data.istable() ){
            return map;
        }

        while(true){
            Varargs argsItem = data.next(key);
            key = argsItem.arg1();

            if( key.isnil() ){
                break;
            }

            value = argsItem.arg(2);

            if( key.isstring()  ){
                map.put(key.toString(), new Var(value));
            }
        }

        return map;
    }

    public static Map<String, Var> table2Map(LuaTable table){
        LuaValue key = LuaValue.NIL;
        LuaValue value = LuaValue.NIL;
        Map<String, Var> map = new ConcurrentHashMap<String, Var>();
        if( table == null || !table.istable() ){
            return null;
        }

        while(true){
            Varargs argsItem = table.next(key);
            key = argsItem.arg1();

            if( key.isnil() ){
                break;
            }

            value = argsItem.arg(2);

            if( key.isstring() ){
                map.put(key.toString(), new Var(value));
            }
        }

        return map;
    }
}
