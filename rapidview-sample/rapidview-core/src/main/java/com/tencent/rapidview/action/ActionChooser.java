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

import org.w3c.dom.Element;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ActionChooser
 * @Desc 选择动作的类
 *
 * @author arlozhang
 * @date 2016.03.16
 */
public class ActionChooser {

    private static Map<String, IFunction> mAllClassMap = new ConcurrentHashMap<String, IFunction>();

    private static Map<String, IFunction> mLimitClassMap = new ConcurrentHashMap<String, IFunction>();
    static{
        try{
            mAllClassMap.put("dataaction", DataActionGeter.class.newInstance());
            mAllClassMap.put("outeraction", OuterActionGeter.class.newInstance());
            mAllClassMap.put("toastaction", ToastActionGeter.class.newInstance());
            mAllClassMap.put("addviewaction", AddViewActionGeter.class.newInstance());
            mAllClassMap.put("backaction", BackActionGeter.class.newInstance());
            mAllClassMap.put("taskaction", TaskActionGeter.class.newInstance());
            mAllClassMap.put("luaaction", LuaActionGeter.class.newInstance());
            mAllClassMap.put("integeroperationaction", IntegerOperationActionGeter.class.newInstance());
            mAllClassMap.put("attributeaction", AttributeActionGeter.class.newInstance());
            mAllClassMap.put("cacheviewaction", CacheViewActionGeter.class.newInstance());
            mAllClassMap.put("invalidateaction", InvalidateActionGeter.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try{
            mLimitClassMap.put("dataaction", DataActionGeter.class.newInstance());
            mLimitClassMap.put("toastaction", ToastActionGeter.class.newInstance());
            mLimitClassMap.put("backaction", BackActionGeter.class.newInstance());
            mLimitClassMap.put("taskaction", TaskActionGeter.class.newInstance());
            mLimitClassMap.put("luaaction", LuaActionGeter.class.newInstance());
            mLimitClassMap.put("integeroperationaction", IntegerOperationActionGeter.class.newInstance());
            mLimitClassMap.put("attributeaction", AttributeActionGeter.class.newInstance());
            mLimitClassMap.put("cacheviewaction", CacheViewActionGeter.class.newInstance());
            mLimitClassMap.put("invalidateaction", InvalidateActionGeter.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ActionObject get(Element element, Map<String, String> mapEnv, boolean limitLevel){
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
        ActionObject get(Element element, Map<String, String> mapEnv);
    }

    private static class DataActionGeter implements IFunction{
        public DataActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv){
            return new DataAction(element, mapEnv);
        }
    }

    private static class OuterActionGeter implements IFunction{
        public OuterActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv){
            return new OuterAction(element, mapEnv);
        }
    }

    private static class ToastActionGeter implements IFunction{
        public ToastActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv){
            return new ToastAction(element, mapEnv);
        }
    }

    private static class AddViewActionGeter implements IFunction{
        public AddViewActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv){
            return new AddViewAction(element, mapEnv);
        }
    }

    private static class BackActionGeter implements IFunction{
        public BackActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv){
            return new BackAction(element, mapEnv);
        }
    }

    private static class TaskActionGeter implements IFunction{
        public TaskActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv){
            return new TaskAction(element, mapEnv);
        }
    }

    private static class LuaActionGeter implements IFunction{
        public LuaActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv){
            return new LuaAction(element, mapEnv);
        }
    }

    private static class IntegerOperationActionGeter implements IFunction{
        public IntegerOperationActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv){
            return new IntegerOperationAction(element, mapEnv);
        }
    }

    private static class AttributeActionGeter implements IFunction{
        public AttributeActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv){
            return new AttributeAction(element, mapEnv);
        }
    }

    private static class CacheViewActionGeter implements IFunction {
        public CacheViewActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv) {
            return new CacheViewAction(element,mapEnv);
        }
    }

    private static class InvalidateActionGeter implements IFunction {
        public InvalidateActionGeter(){}
        @Override
        public ActionObject get(Element element, Map<String, String> mapEnv) {
            return new InvalidateAction(element,mapEnv);
        }
    }
}
