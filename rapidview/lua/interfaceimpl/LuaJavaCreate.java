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
package com.tencent.rapidview.lua.interfaceimpl;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.rapidview.deobfuscated.IBytes;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.RapidStringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class LuaJavaCreate
 * @Desc 创建指定对象
 *
 * @author arlozhang
 * @date 2017.02.20
 */
public class LuaJavaCreate extends RapidLuaJavaObject {

    public LuaJavaCreate(String rapidID, IRapidView rapidView){
        super(rapidID, rapidView);
    }

    private interface IFunction{
        Object get(Object... args);
    }

    private static Map<String, IFunction> mFunctionMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mFunctionMap.put("jsonobject", JSONObjectGeter.class.newInstance());
            mFunctionMap.put("jsonarray", JSONArrayGeter.class.newInstance());
            mFunctionMap.put("jsonstringer", JSONStringerGeter.class.newInstance());
            mFunctionMap.put("jsontokener", JSONTokenerGeter.class.newInstance());
            mFunctionMap.put("bitmap", BitmapGeter.class.newInstance());
            mFunctionMap.put("bytes", BytesGeter.class.newInstance() );
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public Object create(String objName, Object... args){
        IFunction function = null;

        if( RapidStringUtils.isEmpty(objName) ){
            return null;
        }

        function = mFunctionMap.get(objName.toLowerCase());

        return function.get(args);
    }

    private static class JSONArrayGeter implements IFunction {
        public JSONArrayGeter() {
        }

        @Override
        public Object get(Object... args) {
            if( args.length == 0 ){
                return new JSONArray();
            }

            if(args[0] instanceof JSONTokener){
                try{
                    return new JSONArray((JSONTokener) args[0]);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            if( args[0] instanceof String ){
                try{
                    return new JSONArray((String)args[0]);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            return new JSONArray();
        }
    }

    private static class JSONObjectGeter implements IFunction {
        public JSONObjectGeter() {
        }

        @Override
        public Object get(Object... args) {
            if( args.length == 0 ) {
                return new JSONObject();
            }

            if(args[0] instanceof JSONTokener){
                try{
                    return new JSONObject((JSONTokener) args[0]);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            if( args[0] instanceof String ){
                try{
                    return new JSONObject((String)args[0]);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            if( args.length >= 2 &&
                args[0] instanceof JSONObject &&
                args[1] != null &&
                args[1] instanceof LuaTable &&
                ((LuaTable)args[1]).istable() ){

                LuaValue value    = LuaValue.NIL;
                LuaTable table    = (LuaTable) args[1];
                String[] strArray = new String[table.length()];
                int      index    = -1;

                while(true){
                    Varargs argsItem = table.next(value);
                    value = argsItem.arg(2);

                    index++;

                    if( value.isnil() ){
                        break;
                    }

                    if( !value.isstring() ){
                        continue;
                    }

                    strArray[index] = value.toString();
                }

                try{
                    return new JSONObject((JSONObject) args[0], strArray);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            return new JSONObject();
        }
    }

    private static class JSONStringerGeter implements IFunction {
        public JSONStringerGeter() {
        }

        @Override
        public Object get(Object... args) {
            return new JSONStringer();
        }
    }

    private static class JSONTokenerGeter implements IFunction {
        public JSONTokenerGeter() {
        }

        @Override
        public Object get(Object... args) {
            if( args.length == 0 || !(args[0] instanceof String) ){
                return null;
            }

            return new JSONTokener((String)args[0]);
        }
    }

    private static class BitmapGeter implements IFunction {
        public BitmapGeter() {
        }

        @Override
        public Object get(Object... args) {
            Bitmap bmp = null;

            if( args.length == 0 || !(args[0] instanceof IBytes) ){
                return null;
            }

            try{
                bmp = BitmapFactory.decodeByteArray(((IBytes)args[0]).getArrayByte(), 0, (int)((IBytes)args[0]).getLength());
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return bmp;
        }
    }

    private static class BytesGeter implements IFunction {
        public BytesGeter() {
        }

        @Override
        public Object get(Object... args) {

            if( args.length == 0 || !(args[0] instanceof byte[]) ){
                return null;
            }

            return new Bytes((byte[])args[0]);
        }
    }
}
