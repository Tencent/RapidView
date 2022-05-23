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


import com.tencent.rapidview.deobfuscated.IRapidNode;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.deobfuscated.IRapidXmlLuaCenter;

import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidXmlLuaCenter
 * @Desc  XML中内置的Script脚本标签
 *
 * @author arlozhang
 * @date 2016.07.27
 */
public class RapidXmlLuaCenter implements IRapidXmlLuaCenter {

    private IRapidView mRapidView = null;

    private RapidLuaEnvironment mLuaEnvironment = null;

    private Map<String, RapidXmlLuaNode> mMapLuaNode = new ConcurrentHashMap<String, RapidXmlLuaNode>();

    public RapidXmlLuaCenter(RapidLuaEnvironment environment){
        mLuaEnvironment = environment;
    }

    public void add(Element element, Map<String, String> mapEnv){
        if( element == null ){
            return;
        }

        RapidXmlLuaNode node = new RapidXmlLuaNode(element, mLuaEnvironment, mapEnv);

        mMapLuaNode.put(node.getID(), node);
    }

    public void notify(IRapidNode.HOOK_TYPE type, String value) {

        for( Map.Entry<String, RapidXmlLuaNode> entry : mMapLuaNode.entrySet() ){

            RapidXmlLuaNode node = entry.getValue();

            if (node == null) {
                continue;
            }

            node.notify(type, value);
        }

    }

    public void setRapidView(IRapidView rapidView){
        mRapidView = rapidView;

        for( Map.Entry<String, RapidXmlLuaNode> entry : mMapLuaNode.entrySet() ){
            RapidXmlLuaNode node = entry.getValue();
            if( node == null ){
                continue;
            }

            node.setRapidView(mRapidView);
        }
    }

    public void run(List<String> listKey){

        if( listKey == null ){
            return;
        }

        for( int i = 0; i < listKey.size(); i++ ){
            run(listKey.get(i));
        }
    }

    @Override
    public void run(String key){
        RapidXmlLuaNode node;

        if( key == null ){
            return;
        }

        node = mMapLuaNode.get(key);
        if( node == null ){
            return;
        }

        node.run();
    }
}
