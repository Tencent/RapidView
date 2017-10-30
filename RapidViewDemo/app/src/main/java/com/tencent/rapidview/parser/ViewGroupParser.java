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
package com.tencent.rapidview.parser;

import android.view.ViewGroup;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ViewGroupParser
 * @Desc RapidView界面控件ViewGroup解析器
 *
 * @author arlozhang
 * @date 2015.09.24
 */
public class ViewGroupParser extends ViewParser {

    private static Map<String, IFunction> mViewGroupClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mViewGroupClassMap.put("descendantfocusability", initdescendantFocusability.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ViewGroupParser(){}

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mViewGroupClassMap.get(key);

        return clazz;
    }

    private static class initdescendantFocusability implements IFunction {
        public initdescendantFocusability(){}

        public void run(RapidParserObject object, Object view, Var value) {
            if( value.getString().compareToIgnoreCase("beforedescendants") == 0 ){
                ((ViewGroup)view).setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            }

            if( value.getString().compareToIgnoreCase("afterdescendants") == 0 ){
                ((ViewGroup)view).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            }

            if( value.getString().compareToIgnoreCase("blocksdescendants") == 0 ){
                ((ViewGroup)view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }
        }
    }

}
