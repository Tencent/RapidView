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

import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.utils.RapidStringUtils;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class DataFilter
 * @Desc 通过比较数据来过滤
 *
 * @author arlozhang
 * @date 2016.03.16
 */
public class DataFilter extends FilterObject{

    public DataFilter(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean pass(){
        Var ref = mMapAttribute.get("reference");
        Var type = mMapAttribute.get("type");
        Var key = mMapAttribute.get("key");
        Var value = mMapAttribute.get("value");
        RapidDataBinder binder = null;

        if( mRapidView == null || ref == null || key == null ){
            return false;
        }

        if( value == null ){
            value = new Var("");
        }

        if( type == null ){
            type = new Var("");
        }

        binder = mRapidView.getParser().getBinder();

        if( binder == null ){
            return false;
        }

        if( type.getString().compareToIgnoreCase("int") == 0 ||
            type.getString().compareToIgnoreCase("integer") == 0 ){

            return isIntegerPass(ref.getString(), binder.getData(key.getString()).getString(), value.getString());
        }
        else if( type.getString().compareToIgnoreCase("float") == 0 ){
            return isFloatPass(ref.getString(), binder.getData(key.getString()).getString(), value.getString());
        }

        return isStringPass(ref.getString(), binder.getData(key.getString()).getString(), value.getString());
    }

    public boolean isFloatPass(String ref, String data, String value){

        try{
            float lData = Float.parseFloat(data);
            float lRefValue = Float.parseFloat(value);

            if( ref.compareToIgnoreCase("greater") == 0 && lData > lRefValue){
                return true;
            }

            if( ref.compareToIgnoreCase("greaterequal") == 0 && lData >= lRefValue ){
                return true;
            }

            if( ref.compareToIgnoreCase("equal") == 0 && Float.compare(lData,lRefValue) == 0){
                return true;
            }

            if( ref.compareToIgnoreCase("unequal") == 0 && lData != lRefValue ){
                return true;
            }

            if( ref.compareToIgnoreCase("less") == 0 && lData < lRefValue ){
                return true;
            }

            if( ref.compareToIgnoreCase("lessequal") == 0 && lData <= lRefValue ){
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }


    public boolean isIntegerPass(String ref, String data, String value){

        try{
            long lData = 0;
            long lRefValue = 0;

            if( RapidStringUtils.isEmpty(data) || RapidStringUtils.isEmpty(value) ){
                return false;
            }

            lData = Long.parseLong(data);
            lRefValue = Long.parseLong(value);

            if( ref.compareToIgnoreCase("greater") == 0 && lData > lRefValue ){
                return true;
            }

            if( ref.compareToIgnoreCase("greaterequal") == 0 && lData >= lRefValue ){
                return true;
            }

            if( ref.compareToIgnoreCase("equal") == 0 && lData == lRefValue ){
                return true;
            }

            if( ref.compareToIgnoreCase("unequal") == 0 && lData != lRefValue ){
                return true;
            }

            if( ref.compareToIgnoreCase("less") == 0 && lData < lRefValue ){
                return true;
            }

            if( ref.compareToIgnoreCase("lessequal") == 0 && lData <= lRefValue ){
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean isStringPass(String ref, String data, String value){

        try{
            if( ref.compareToIgnoreCase("greater") == 0 && data.compareTo(value) > 0 ){
                return true;
            }

            if( ref.compareToIgnoreCase("greaterequal") == 0 && data.compareTo(value) >= 0 ){
                return true;
            }

            if( ref.compareToIgnoreCase("equal") == 0 && data.compareTo(value) == 0 ){
                return true;
            }

            if( ref.compareToIgnoreCase("unequal") == 0 && data.compareTo(value) != 0 ){
                return true;
            }

            if( ref.compareToIgnoreCase("less") == 0 && data.compareTo(value) < 0 ){
                return true;
            }

            if( ref.compareToIgnoreCase("lessequal") == 0 && data.compareTo(value) <= 0 ){
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

}
