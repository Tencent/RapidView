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
package com.tencent.rapidview.framework;

import com.tencent.rapidview.data.DataExpressionsParser;
import com.tencent.rapidview.deobfuscated.IRapidNode;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.RapidControlNameCreator;
import com.tencent.rapidview.utils.RapidStringUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidNodeImpl
 * @Desc 对通知类XML节点的通知结构抽象类
 *
 * @author arlozhang
 * @date 2018.05.16
 */
public abstract class RapidNodeImpl implements IRapidNode {

    protected Element mElement = null;

    protected String mID = null;

    protected String mValue = null;

    protected IRapidView mRapidView = null;

    protected Map<String, String> mMapEnvironment = null;

    protected Map<HOOK_TYPE, Boolean> mMapHookType = new ConcurrentHashMap<HOOK_TYPE, Boolean>();

    public String getID(){
        if( RapidStringUtils.isEmpty(mID) ){
            mID = RapidControlNameCreator.get();
        }

        return mID;
    }

    protected void analyzeValue(){
        Node node = mElement.getAttributes().getNamedItem("value");

        if( node == null ){
            mValue = "";
            return;
        }

        mValue = node.getNodeValue();
        mValue = getTransValue(mValue);
    }

    protected void analyzeID(){
        Node node = mElement.getAttributes().getNamedItem("id");

        if( node == null ){
            mID = "";
            return;
        }

        mID = node.getNodeValue();
        mID = getTransValue(mID);
    }

    protected void analzyeHookType(){
        String hookType;

        Node node = mElement.getAttributes().getNamedItem("hook");
        List<String> listType = null;

        if( node == null ){
            return;
        }

        hookType = node.getNodeValue();
        hookType = getTransValue(hookType);

        listType = RapidStringUtils.stringToList(hookType);

        for( int i = 0; i < listType.size(); i++ ){
            String type = listType.get(i);

            if( type.compareToIgnoreCase("datachange") == 0 ||
                    type.compareToIgnoreCase("data_change") == 0 ){
                mMapHookType.put(IRapidNode.HOOK_TYPE.enum_datachange, true);
            }
            else if( hookType.compareToIgnoreCase("loadfinish") == 0 ||
                    hookType.compareToIgnoreCase("load_finish") == 0 ){
                mMapHookType.put(IRapidNode.HOOK_TYPE.enum_load_finish, true);
            }
            else if( hookType.compareToIgnoreCase("datainitialize") == 0 ||
                    hookType.compareToIgnoreCase("data_initialize") == 0){
                mMapHookType.put(IRapidNode.HOOK_TYPE.enum_data_initialize, true);
            }
            else if( hookType.compareToIgnoreCase("viewshow") == 0 ||
                    hookType.compareToIgnoreCase("view_show") == 0 ){
                mMapHookType.put(IRapidNode.HOOK_TYPE.enum_view_show, true);
            }
            else if( hookType.compareToIgnoreCase("viewscrollexposure") == 0 ||
                    hookType.compareToIgnoreCase("view_scroll_exposure") == 0 ){
                mMapHookType.put(IRapidNode.HOOK_TYPE.enum_view_scroll_exposure, true);
            }
            else if( hookType.compareToIgnoreCase("data_start") == 0 ||
                    hookType.compareToIgnoreCase("datastart") == 0 ){
                mMapHookType.put(IRapidNode.HOOK_TYPE.enum_data_start, true);
            }
            else if( hookType.compareToIgnoreCase("data_end") == 0 ||
                    hookType.compareToIgnoreCase("dataend") == 0 ){
                mMapHookType.put(IRapidNode.HOOK_TYPE.enum_data_end, true);
            }
        }

    }

    protected String getTransValue(String value){
        DataExpressionsParser parser = new DataExpressionsParser();

        if( parser.isDataExpression(value) ){
            value = parser.get(null, mMapEnvironment, null, null, value).getString();
        }

        return value;
    }
}
