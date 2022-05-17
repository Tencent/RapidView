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

import android.widget.RelativeLayout;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RelativeLayoutParser
 * @Desc RapidView界面控件RelativeLayout解析器
 *
 * @author arlozhang
 * @date 2015.09.24
 */
public class RelativeLayoutParser extends ViewGroupParser {

    private static Map<String, IFunction> mRelativeLayoutClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mRelativeLayoutClassMap.put("gravity", initgravity.class.newInstance());
            mRelativeLayoutClassMap.put("horizontalgravtiy", inithorizontalgravtiy.class.newInstance());
            mRelativeLayoutClassMap.put("verticalgravity", initverticalgravity.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public RelativeLayoutParser(){}

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mRelativeLayoutClassMap.get(key);

        return clazz;
    }

    private static class initgravity implements IFunction {
        public initgravity(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((RelativeLayout)view).setGravity(value.getInt());
        }
    }

    private static class inithorizontalgravtiy implements IFunction {
        public inithorizontalgravtiy(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((RelativeLayout)view).setHorizontalGravity(value.getInt());
        }
    }

    private static class initverticalgravity implements IFunction {
        public initverticalgravity(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((RelativeLayout)view).setVerticalGravity(value.getInt());
        }
    }
}
