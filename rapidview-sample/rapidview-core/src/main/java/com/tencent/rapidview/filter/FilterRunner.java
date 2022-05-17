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
package com.tencent.rapidview.filter;

import com.tencent.rapidview.deobfuscated.IFilterRunner;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.XLog;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @Class FilterRunner
 * @Desc 用于外部直接执行Filter的执行器
 *
 * @author arlozhang
 * @date 2016.12.05
 */
public class FilterRunner implements IFilterRunner{

    private DocumentBuilderFactory mFactory = null;

    private DocumentBuilder mBuilder = null;

    private Document mDocument = null;

    private IRapidView mRapidView = null;

    private final boolean mLimitLevel;

    public FilterRunner(IRapidView rapidView, boolean limitLevel){

        mLimitLevel = limitLevel;
        mRapidView = rapidView;

        try{
            mFactory = DocumentBuilderFactory.newInstance();
            mBuilder = mFactory.newDocumentBuilder();
            mDocument = mBuilder.newDocument();
        }
        catch (ParserConfigurationException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean run(String filterName, LuaTable tableAttribute, LuaTable tableEnv){
        LuaValue key = LuaValue.NIL;
        LuaValue value = LuaValue.NIL;
        Map<String, String> mapAttr = new ConcurrentHashMap<String, String>();
        Map<String, String> mapEnv = new ConcurrentHashMap<String, String>();

        if( tableAttribute != null && tableAttribute.istable() ){
            while(true){
                Varargs argsItem = tableAttribute.next(key);
                key = argsItem.arg1();

                if( key.isnil() ){
                    break;
                }

                value = argsItem.arg(2);

                if( key.isstring() && value.isstring() ){
                    mapAttr.put(key.toString(), value.toString());
                }
            }
        }

        if( tableEnv != null && tableEnv.istable() ){
            while(true){
                Varargs argsItem = tableEnv.next(key);
                key = argsItem.arg1();

                if( key.isnil() ){
                    break;
                }

                value = argsItem.arg(2);

                if( key.isstring() && value.isstring() ){
                    mapEnv.put(key.toString(), value.toString());
                }
            }
        }

        return run(filterName, mapAttr, mapEnv);
    }

    public boolean run(String filterName, Map<String,String> mapAttribute, Map<String,String> mapEnv){
        FilterObject obj = null;
        Element filterElement = null;

        if( mDocument == null || RapidStringUtils.isEmpty(filterName) || mapAttribute == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "无法执行filter，参数错误");
            return false;
        }

        filterElement = mDocument.createElement(filterName);

        for( Map.Entry<String, String> entry : mapAttribute.entrySet() ){
            filterElement.setAttribute(entry.getKey(), entry.getValue());
        }

        if( mapEnv == null ){
            mapEnv = new ConcurrentHashMap<String, String>();
        }

        obj = FilterChooser.get(filterElement, mapEnv, mLimitLevel);
        if( obj == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "无法执行filter，filter未找到：" + filterName);
            return false;
        }

        obj.setRapidView(mRapidView);

        return obj.isPass();
    }
}
