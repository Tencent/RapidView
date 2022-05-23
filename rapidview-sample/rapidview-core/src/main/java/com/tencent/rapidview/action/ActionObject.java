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

import android.content.Context;

import com.tencent.rapidview.data.DataExpressionsParser;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.lua.RapidLuaEnvironment;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.XLog;

import org.luaj.vm2.Globals;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ActionObject
 * @Desc 一个动作的执行节点
 *
 * @author arlozhang
 * @date 2016.03.16
 */
public abstract class ActionObject {

    protected Map<String, String> mMapOriginAttribute = new ConcurrentHashMap<String, String>();

    protected Map<String, Var> mMapAttribute = new ConcurrentHashMap<String, Var>();

    protected Map<String, String> mMapEnvironment;

    protected IRapidView mRapidView = null;

    protected String mActionTag;

    protected Object retObj = null;

    public ActionObject(Element element, Map<String, String> mapEnv){
        mMapEnvironment = mapEnv;
        initAttribute(element);
    }

    public boolean callRun(){
        boolean bRet;
        RapidParserObject obj;

        translateAttribute();

        bRet = run();

        obj = getParser();
        if( retObj != null && obj != null ){
            try {
                obj.getBinder().update(getActionName() + "_run_ret", new Var(retObj));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return bRet;
    }

    public void setRapidView(IRapidView rapidView){
        mRapidView = rapidView;
    }

    public String getActionName(){
        return mActionTag.toLowerCase();
    }

    private void initAttribute(Element element){
        if( element == null ){
            return;
        }

        NamedNodeMap mapAttrs = element.getAttributes();

        mMapOriginAttribute.clear();
        for( int i = 0; i < mapAttrs.getLength(); i++ ){
            mMapOriginAttribute.put(mapAttrs.item(i).getNodeName().toLowerCase(),
                    mapAttrs.item(i).getNodeValue());
        }

        mActionTag = element.getTagName();
    }

    protected Context getContext(){
        if( mRapidView == null ){
            return null;
        }

        return mRapidView.getParser().getContext();
    }

    protected RapidParserObject getParser(){
        if( mRapidView == null ){
            return null;
        }

        return mRapidView.getParser();
    }

    protected RapidDataBinder getBinder(){
        RapidParserObject parser = getParser();
        if( parser == null ){
            return null;
        }

        return getParser().getBinder();
    }

    protected RapidLuaEnvironment getLuaEnvironment(){
        RapidParserObject parser = getParser();
        if( parser == null ){
            return null;
        }

        return getParser().getLuaEnvironment();
    }

    protected Globals getGlobals(){
        RapidParserObject parser = getParser();
        if( parser == null ){
            return null;
        }

        return getParser().getGlobals();
    }

    protected IRapidView getRapidView(){
        return mRapidView;
    }

    protected void translateAttribute(){
        RapidDataBinder binder = getBinder();
        DataExpressionsParser parser = new DataExpressionsParser();
        Map<String, Var> map = new ConcurrentHashMap<String, Var>();

        if( binder == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "获取Binder失败：" + getActionName());

            for( Map.Entry<String, String> entry : mMapOriginAttribute.entrySet() ){
                mMapAttribute.put(entry.getKey(), new Var(entry.getValue()));
            }

            return;
        }

        for( Map.Entry<String, String> entry : mMapOriginAttribute.entrySet() ){
            Var var = new Var(entry.getValue());

            if( entry.getValue() == null ){
                continue;
            }

            if( parser.isDataExpression(entry.getValue()) ){
                var = parser.get(binder, mMapEnvironment, null, null, entry.getValue());
            }

            map.put(entry.getKey(), var);
        }

        mMapAttribute = map;
    }

    public abstract boolean run();
}
