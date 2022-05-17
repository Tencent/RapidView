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

import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidNodeImpl;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.RapidThreadPool;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.Prototype;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * @Class RapidXmlLuaNode
 * @Desc XML内置Lua的节点
 *
 * @author arlozhang
 * @date 2018.05.16
 */
public class RapidXmlLuaNode extends RapidNodeImpl{

    private volatile LuaClosure mClosure = null;

    private RapidLuaEnvironment mLuaEnvironment = null;

    private Globals mGlobals = null;

    private LUA_TYPE mLuaType = LUA_TYPE.enum_function;

    private enum LUA_TYPE{
        enum_function,
        enum_full,
    }

    public RapidXmlLuaNode(Element element, RapidLuaEnvironment luaEnv, Map<String, String> mapEnv){
        mElement = element;
        mMapEnvironment = mapEnv;
        mLuaEnvironment = luaEnv;
        mGlobals = mLuaEnvironment.createGlobals();

        analyzeAttribute();

        if( Looper.myLooper() == Looper.getMainLooper() ){

            RapidThreadPool.get().execute(new Runnable() {
                @Override
                public void run() {
                    initialize();
                }
            });
        }
        else{
            initialize();
        }
    }

    public void setRapidView(IRapidView rapidView){

        mRapidView = rapidView;

        mGlobals.load(new RapidLuaLib(rapidView, mMapEnvironment));
    }

    public void notify(HOOK_TYPE type, String value){
        if( mMapHookType.get(type) == null ){
            return;
        }

        switch ( type ){
            case enum_datachange:
            case enum_view_scroll_exposure:
                notifyValue(value);
                break;
            case enum_load_finish:
            case enum_data_initialize:
            case enum_view_show:
            case enum_data_start:
            case enum_data_end:
                run();
                break;
        }
    }

    public boolean run(){

        if( mClosure == null ){
            initialize();
        }

        if( mClosure == null ){
            return false;
        }

        RapidLuaCaller.getInstance().call(mGlobals, "main");

        return true;
    }

    private void notifyValue(String value){
        if( value.compareToIgnoreCase(mValue) != 0 ){
            return;
        }

        run();
    }

    private synchronized void initialize(){
        Prototype binary = null;
        String lua;

        if( mClosure != null ){
            return;
        }

        lua = mElement.getTextContent();

        if( mLuaType == LUA_TYPE.enum_function ){
            lua = "function main()" + '\n' + lua + '\n' +"end";
        }

        try{
            binary = mGlobals.compilePrototype(new ByteArrayInputStream(lua.getBytes("UTF-8")), getID());

            mClosure = new LuaClosure(binary, mGlobals);

            mClosure.call();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void analyzeAttribute(){
        analyzeID();
        analyzeValue();
        analzyeHookType();
        analyzeLuaType();
    }

    protected void analyzeLuaType(){
        String type = null;
        Node node = mElement.getAttributes().getNamedItem("type");

        if( node == null ){
            return;
        }

        type = node.getNodeValue();
        type = getTransValue(type);

        if( RapidStringUtils.isEmpty(type) ){
            return;
        }

        if( type.compareToIgnoreCase("full") == 0 ){
            mLuaType = LUA_TYPE.enum_full;
        }
    }

}
