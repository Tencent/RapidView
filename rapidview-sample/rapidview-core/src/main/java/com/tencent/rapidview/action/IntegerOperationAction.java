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

import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class IntegerOperationAction
 * @Desc 对Map中数据进行数字运算的action
 *
 * @author arlozhang
 * @date 2016.08.31
 */
public class IntegerOperationAction extends ActionObject{

    public IntegerOperationAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){
        RapidDataBinder binder = getBinder();
        Var data = mMapAttribute.get("data");
        Var value = mMapAttribute.get("value");
        Var operation = mMapAttribute.get("operation");
        Var dataValue;
        long nData = 0;
        long nValue = 0;

        if( binder == null || data == null || value == null || operation == null ){
            return false;
        }

        dataValue = binder.getData(data.getString());
        if( dataValue == null ){
            return false;
        }

        nData = dataValue.getLong();
        nValue = value.getLong();

        if( operation.getString().compareToIgnoreCase("add") == 0 ){
            nData += nValue;
        }
        else if( operation.getString().compareToIgnoreCase("subtract") == 0 ||
                operation.getString().compareToIgnoreCase("sub") == 0 ){
            nData -= nValue;
        }
        else if( operation.getString().compareToIgnoreCase("multiply") == 0 ){
            nData *= nValue;
        }
        else if( operation.getString().compareToIgnoreCase("divide") == 0 ||
                operation.getString().compareToIgnoreCase("div") == 0 ){
            if( nValue != 0 ){
                nData /= nValue;
            }
        }
        else{
            nData += nValue;
        }

        binder.update(data.getString(), new Var(nData));

        return true;
    }
}
