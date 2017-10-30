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
import com.tencent.rapidview.deobfuscated.IRapidView;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class AttributeAction
 * @Desc 调整参数的action
 *
 * @author arlozhang
 * @date 2017.10.09
 */
public class AttributeAction extends ActionObject{

    public AttributeAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){
        IRapidView view;
        Var id = mMapAttribute.get("cid");
        Var key = mMapAttribute.get("key");
        Var value = mMapAttribute.get("value");

        if( mRapidView == null ){
            return false;
        }


        if( mRapidView.getParser().getID().compareToIgnoreCase(id.getString()) == 0 ) {

            mRapidView.getParser().update(key.getString(), value);
            return true;
        }

        view = mRapidView.getParser().getChildView(id.getString());
        if (view == null) {
            return false;
        }

        view.getParser().update(key.getString(), value);

        return true;
    }
}
