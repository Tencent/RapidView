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

import org.w3c.dom.Element;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class FilterChooser
 * @Desc 过滤条件选择器
 *
 * @author arlozhang
 * @date 2016.03.16
 */
public class FilterChooser {

    private static Map<String, IFunction> mAllClassMap = new ConcurrentHashMap<String, IFunction>();

    private static Map<String, IFunction> mLimitClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mAllClassMap.put("datafilter", DataFilterGeter.class.newInstance());
            mAllClassMap.put("networkfilter", NetWorkGeter.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try{
            mLimitClassMap.put("datafilter", DataFilterGeter.class.newInstance());
            mLimitClassMap.put("networkfilter", NetWorkGeter.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static FilterObject get(Element element,  Map<String, String> mapEnv, boolean limitLevel){
        IFunction function;

        if( element == null ){
            return null;
        }

        if( limitLevel ){
            function = mLimitClassMap.get(element.getTagName().toLowerCase());
        }
        else{
            function = mAllClassMap.get(element.getTagName().toLowerCase());
        }

        if( function == null ){
            return null;
        }

        return function.get(element, mapEnv);
    }

    private interface IFunction{
        FilterObject get(Element element,  Map<String, String> mapEnv);
    }

    private static class DataFilterGeter implements IFunction{
        public DataFilterGeter(){}
        @Override
        public FilterObject get(Element element,  Map<String, String> mapEnv){
            return new DataFilter(element, mapEnv);
        }
    }

    private static class NetWorkGeter implements IFunction{
        public NetWorkGeter(){}
        @Override
        public FilterObject get(Element element,  Map<String, String> mapEnv){
            return new NetWorkFilter(element, mapEnv);
        }
    }
}
