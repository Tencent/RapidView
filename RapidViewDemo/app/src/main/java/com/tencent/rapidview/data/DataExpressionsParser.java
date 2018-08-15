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
package com.tencent.rapidview.data;


import java.util.Map;

/**
 * @Class DataExpressionsParser
 * @Desc 数据表达式解析
 *
 * @author arlozhang
 * @date 2016.03.15
 */
public class DataExpressionsParser {

    public Var get(RapidDataBinder binder, Map<String, String> mapEnv, String id, String key, String value) {
        if( value == null ){
            return null;
        }

        String ret = getEnvVarReplace(mapEnv, value);

        return getBinderValue(binder, id, key, ret);
    }

    private Var getBinderValue(RapidDataBinder binder, String id, String key, String value){
        String dataKey = value;
        String dftValue = "";
        Var   ret;

        if (value.length() <= 5 || value.substring(0, 5).compareToIgnoreCase("data@") != 0) {
            return new Var(value);
        }

        if( !value.contains("data@") ){
            return new Var(value);
        }

        dataKey = value.substring(5, dataKey.length());

        if (dataKey.contains("$")) {
            String[] parts = dataKey.split("\\$");

            dataKey = parts[0];
            dftValue = parts[1];
        }


        if (binder == null) {
            return new Var(dftValue);
        }

        if( id == null || key == null ){
            ret = binder.getData(dataKey);
        }
        else{
            ret = binder.getAndBind(dataKey, id, key);
        }

        if (ret == null || ret.isNull() ) {
            if( ret == null ){
                ret = new Var();
            }

            ret.set(dftValue);
        }

        return ret;
    }

    private String getEnvVarReplace( Map<String, String> mapEnv, String value ){
        String ret = value;

        if( mapEnv == null || !value.contains("[") ){

            return value;
        }

        while ( ret.contains("[") ){
            int posLeft = ret.lastIndexOf("[");
            int posRight =  ret.lastIndexOf("]");

            String strEnvKey = ret.substring(posLeft + 1, posRight);
            String strEnvValue = mapEnv.get(strEnvKey);
            if( strEnvValue == null ){
                strEnvValue = "";
            }

            String strReplace = ret.substring(0, posLeft);
            strReplace += strEnvValue;

            if( ret.length() > posRight + 1 ){
                strReplace += ret.substring(posRight + 1, ret.length());
            }

            ret = strReplace;
        }

        return ret;
    }

    public boolean isDataExpression(String str){
        return true;
    }
}
