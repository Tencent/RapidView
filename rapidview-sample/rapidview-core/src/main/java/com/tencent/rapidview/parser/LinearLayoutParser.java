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

import android.widget.LinearLayout;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class LinearLayoutParser
 * @Desc RapidView界面控件LinearLayout解析器
 *
 * @author arlozhang
 * @date 2015.09.24
 */
public class LinearLayoutParser extends ViewGroupParser {

    private static Map<String, IFunction> mLinearLayoutClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mLinearLayoutClassMap.put("gravity", initgravity.class.newInstance());
            mLinearLayoutClassMap.put("horizontalgravity", inithorizontalgravity.class.newInstance());
            mLinearLayoutClassMap.put("verticalgravity", initverticalgravity.class.newInstance());
            mLinearLayoutClassMap.put("baselinealigned", initbaselinealigned.class.newInstance());
            mLinearLayoutClassMap.put("weightsum", initweightsum.class.newInstance());
            mLinearLayoutClassMap.put("orientation", initorientation.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public LinearLayoutParser(){}

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mLinearLayoutClassMap.get(key);

        return clazz;
    }

    private static class initgravity implements IFunction {
        public initgravity(){}

        public void run(RapidParserObject object, Object view, Var value){
            ((LinearLayout)view).setGravity(value.getInt());
        }
    }

    private static class inithorizontalgravity implements IFunction {
        public inithorizontalgravity(){}

        public void run(RapidParserObject object, Object view, Var value){
            ((LinearLayout)view).setHorizontalGravity(value.getInt());
        }
    }

    private static class initverticalgravity implements IFunction {
        public initverticalgravity(){}

        public void run(RapidParserObject object, Object view, Var value){
            ((LinearLayout)view).setVerticalGravity(value.getInt());
        }
    }

    private static class initbaselinealigned implements IFunction {
        public initbaselinealigned(){}

        public void run(RapidParserObject object, Object view, Var value){
            ((LinearLayout)view).setBaselineAligned(value.getBoolean());
        }
    }

    private static class initweightsum implements IFunction {
        public initweightsum(){}

        public void run(RapidParserObject object, Object view, Var value){
            ((LinearLayout)view).setWeightSum(value.getFloat());
        }
    }

    private static class initorientation implements IFunction {
        public initorientation(){}

        public void run(RapidParserObject object, Object view, Var value){
            if( value.getString().compareToIgnoreCase("horizontal") == 0 ){
                ((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL);
            }
            else if( value.getString().compareToIgnoreCase("vertical") == 0 ){
                ((LinearLayout)view).setOrientation(LinearLayout.VERTICAL);
            }
        }
    }
}
