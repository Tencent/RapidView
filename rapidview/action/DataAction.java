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

import com.tencent.rapidview.data.DataExpressionsParser;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class DataAction
 * @Desc 向DataBinder里更新数据的action
 *
 * @author arlozhang
 * @date 2016.03.18
 */
public class DataAction extends ActionObject{

    public DataAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }


    @Override
    public boolean run(){
        Var attrkey = mMapAttribute.get("key");
        Var attrValue = mMapAttribute.get("value");

        DataExpressionsParser parser = new DataExpressionsParser();
        RapidDataBinder binder = null;

        if( mRapidView == null ) {
            return false;
        }

        binder = mRapidView.getParser().getBinder();

        if( parser.isDataExpression(attrkey.getString()) ){
            Var var = parser.get(binder, mMapEnvironment, null, null, attrkey.getString());
            if( var != null ){
                attrkey = var;
            }
        }

        if( parser.isDataExpression(attrValue.getString()) ){
            Var var = parser.get(binder, mMapEnvironment, null, null, attrValue.getString());
            if( var != null ){
                attrValue = var;
            }
        }

        mRapidView.getParser().getBinder().update(attrkey.getString(), attrValue);

        return true;
    }
}
