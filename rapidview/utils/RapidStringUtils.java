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
package com.tencent.rapidview.utils;

import com.tencent.rapidview.data.Var;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidStringUtils
 * @Desc 统一处理字符串拆分工作，保证写法一致
 *
 * @author arlozhang
 * @date 2016.03.18
 */
public class RapidStringUtils {

    public static boolean stringToBoolean(String value){
        if( value == null ){
            return false;
        }

        if( value.compareToIgnoreCase("true") == 0 ||
            value.compareToIgnoreCase("1") == 0 ){
            return true;
        }

        return false;
    }

    public static boolean isEmpty(String value){
        if( value == null ){
            return true;
        }

        if( value.compareTo("") == 0 ){
            return true;
        }

        return false;
    }

    public static boolean isEmpty(Var value){
        if( value == null || value.isNull() ){
            return true;
        }

        if( value.getString().compareTo("") == 0 ){
            return true;
        }

        return false;
    }

    public static Map<String,String> stringToMap(String value){
        Map<String,String> map = new ConcurrentHashMap<String,String>();
        String[] arrayItems;
        if( value == null ){
            value = "";
        }

        arrayItems = value.split(",");
        for( int i = 0; i < arrayItems.length; i++ ){
            String[] arrayValue = arrayItems[i].split(":");
            if( arrayValue.length < 1 ){
                continue;
            }

            map.put(rapidSymbolTranslate(arrayValue[0]), arrayValue.length > 1 ? rapidSymbolTranslate(arrayValue[1]) : "");
        }

        return map;
    }

    public static Map<String, Var> stringToVarMap(String value){
        Map<String, Var> map = new ConcurrentHashMap<String, Var>();
        String[] arrayItems;
        if( value == null ){
            value = "";
        }

        arrayItems = value.split(",");
        for( int i = 0; i < arrayItems.length; i++ ){
            String[] arrayValue = arrayItems[i].split(":");
            if( arrayValue.length < 1 ){
                continue;
            }

            map.put(rapidSymbolTranslate(arrayValue[0]), new Var(arrayValue.length > 1 ? rapidSymbolTranslate(arrayValue[1]) : ""));
        }

        return map;
    }

    public static List<String> stringToList(String value){
        List<String> list = new ArrayList<String>();
        String[] arrayItems;

        if( value == null ){
            value = "";
        }

        arrayItems = value.split(",");
        for( int i = 0; i < arrayItems.length; i++ ){
            list.add(rapidSymbolTranslate(arrayItems[i]));
        }

        return list;
    }

    public static List<Map<String, String>> stringToListMap(String value){
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String[] arrayMap;

        if( value == null ){
            value = "";
        }

        arrayMap = value.split("\\|");
        for( int i = 0; i < arrayMap.length; i++ ){
            Map<String, String> mapItem = new ConcurrentHashMap<String, String>();
            String[] arrayMapItem = arrayMap[i].split(",");

            for( int j = 0; j < arrayMapItem.length; j++ ){
                String[] arrayItem = arrayMapItem[j].split(":");
                if( arrayItem.length < 2 ){
                    continue;
                }

                mapItem.put(rapidSymbolTranslate(arrayItem[0]), rapidSymbolTranslate(arrayItem[1]));
            }

            list.add(mapItem);
        }

        return list;
    }

    public static List<List<String>> stringToTwoLayerList(String value){
        List<List<String>> listOuter = new ArrayList<List<String>>();
        String[] arrayList;

        if( value == null ){
            value = "";
        }

        arrayList = value.split("\\|");

        for( int i = 0; i < arrayList.length; i++ ){
            List<String> listInner = new ArrayList<String>();
            String[] arrayItem = arrayList[i].split(",");

            for( int j = 0; j < arrayItem.length; j++ ){
                listInner.add(rapidSymbolTranslate(arrayItem[j]));
            }

            listOuter.add(listInner);
        }

        return listOuter;
    }

    public static String rapidSymbolTranslate(String value){

        if( !value.contains("&") && !value.contains("^") ){
            return value;
        }

        value = value.replace("&comma", ",");
        value = value.replace("&colon", ":");
        value = value.replace("&vtcline", "\\|");
        value = value.replace("&leftbrace", "{");
        value = value.replace("&rightbrace", "}");

        value = value.replace("^comma", ",");
        value = value.replace("^colon", ":");
        value = value.replace("^vtcline", "\\|");
        value = value.replace("^leftbrace", "{");
        value = value.replace("^rightbrace", "}");

        return value;
    }
}
