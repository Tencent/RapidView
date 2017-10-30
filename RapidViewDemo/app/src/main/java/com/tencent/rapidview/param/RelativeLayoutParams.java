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
package com.tencent.rapidview.param;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RelativeLayoutParams
 * @Desc RapidView界面解析RelativeLayout.LayoutParams解析器
 *
 * @author arlozhang
 * @date 2015.09.23
 */
public class RelativeLayoutParams extends MarginParams {

    private static Map<String, IFunction> mRelativeLayoutClassMap = new ConcurrentHashMap<String, IFunction>();

    static {
        try{
            mRelativeLayoutClassMap.put("alignleft", alignLeft.class.newInstance());
            mRelativeLayoutClassMap.put("aligntop", alignTop.class.newInstance());
            mRelativeLayoutClassMap.put("alignright", alignRight.class.newInstance());
            mRelativeLayoutClassMap.put("alignbottom", alignBottom.class.newInstance());
            mRelativeLayoutClassMap.put("leftof", leftOf.class.newInstance());
            mRelativeLayoutClassMap.put("above", above.class.newInstance());
            mRelativeLayoutClassMap.put("rightof", rightOf.class.newInstance());
            mRelativeLayoutClassMap.put("below", below.class.newInstance());
            mRelativeLayoutClassMap.put("centervertical", centerVertical.class.newInstance());
            mRelativeLayoutClassMap.put("centerhorizontal", centerHorizontal.class.newInstance());
            mRelativeLayoutClassMap.put("centerinparent", centerInParent.class.newInstance());
            mRelativeLayoutClassMap.put("alignparenttop", alignParentTop.class.newInstance());
            mRelativeLayoutClassMap.put("alignparentright", alignParentRight.class.newInstance());
            mRelativeLayoutClassMap.put("alignparentleft", alignParentLeft.class.newInstance());
            mRelativeLayoutClassMap.put("alignparentbottom", alignParentBottom.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public RelativeLayoutParams(Context context){
        super(context);
    }

    @Override
    protected Object getObject(){
        return new RelativeLayout.LayoutParams(0, 0);
    }


    @Override
    protected IFunction getAttributeFunction(String key){
        IFunction function = super.getAttributeFunction(key);
        if( function != null ){
            return function;
        }

        if( key == null ){
            return null;
        }

        IFunction clazz = mRelativeLayoutClassMap.get(key);

        return clazz;
    }

    private static class alignLeft implements IFunction {
        public alignLeft(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( brotherMap == null || params == null || value == null ){
                return;
            }

            int id = 0;

            if( brotherMap.get(value.getString()) != null ){
                id = brotherMap.get(value.getString()).getID();
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.ALIGN_LEFT, id);
        }
    }

    private static class alignTop implements IFunction {
        public alignTop(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( brotherMap == null || params == null || value == null ){
                return;
            }

            int id = 0;

            if( brotherMap.get(value.getString()) != null ){
                id = brotherMap.get(value.getString()).getID();
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.ALIGN_TOP, id);
        }
    }

    private static class alignRight implements IFunction {
        public alignRight(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( brotherMap == null || params == null || value == null ){
                return;
            }

            int id = 0;

            if( brotherMap.get(value.getString()) != null ){
                id = brotherMap.get(value.getString()).getID();
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.ALIGN_RIGHT, id);
        }
    }

    private static class alignBottom implements IFunction {
        public alignBottom(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( brotherMap == null || params == null || value == null ){
                return;
            }

            int id = 0;

            if( brotherMap.get(value.getString()) != null ){
                id = brotherMap.get(value.getString()).getID();
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.ALIGN_BOTTOM, id);
        }
    }

    private static class leftOf implements IFunction {
        public leftOf(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( brotherMap == null || params == null || value == null ){
                return;
            }

            int id = 0;

            if( brotherMap.get(value.getString()) != null ){
                id = brotherMap.get(value.getString()).getID();
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.LEFT_OF, id);
        }
    }

    private static class above implements IFunction {
        public above(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( brotherMap == null || params == null || value == null ){
                return;
            }

            int id = 0;

            if( brotherMap.get(value.getString()) != null ){
                id = brotherMap.get(value.getString()).getID();
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.ABOVE, id);
        }
    }

    private static class rightOf implements IFunction {
        public rightOf(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( brotherMap == null || params == null || value == null ){
                return;
            }

            int id = 0;

            if( brotherMap.get(value.getString()) != null ){
                id = brotherMap.get(value.getString()).getID();
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.RIGHT_OF, id);
        }
    }

    private static class below implements IFunction {
        public below(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( brotherMap == null || params == null || value == null ){
                return;
            }

            int id = 0;

            if( brotherMap.get(value.getString()) != null ){
                id = brotherMap.get(value.getString()).getID();
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.BELOW, id);
        }
    }

    private static class alignParentBottom implements IFunction {
        public alignParentBottom(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int nValue = 0;

            if( value.getString().compareToIgnoreCase("true") == 0 ){
                nValue = RelativeLayout.TRUE;
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, nValue);
        }
    }

    private static class alignParentLeft implements IFunction {
        public alignParentLeft(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int nValue = 0;

            if( value.getString().compareToIgnoreCase("true") == 0 ){
                nValue = RelativeLayout.TRUE;
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.ALIGN_PARENT_LEFT, nValue);
        }
    }

    private static class alignParentRight implements IFunction {
        public alignParentRight(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int nValue = 0;

            if( value.getString().compareToIgnoreCase("true") == 0 ){
                nValue = RelativeLayout.TRUE;
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.ALIGN_PARENT_RIGHT, nValue);
        }
    }

    private static class alignParentTop implements IFunction {
        public alignParentTop(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int nValue = 0;

            if( value.getString().compareToIgnoreCase("true") == 0 ){
                nValue = RelativeLayout.TRUE;
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.ALIGN_PARENT_TOP, nValue);
        }
    }

    private static class centerHorizontal implements IFunction {
        public centerHorizontal(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int nValue = 0;

            if( value.getString().compareToIgnoreCase("true") == 0 ){
                nValue = RelativeLayout.TRUE;
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.CENTER_HORIZONTAL, nValue);
        }
    }

    private static class centerVertical implements IFunction {
        public centerVertical(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int nValue = 0;

            if( value.getString().compareToIgnoreCase("true") == 0 ){
                nValue = RelativeLayout.TRUE;
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.CENTER_VERTICAL, nValue);
        }
    }


    private static class centerInParent implements IFunction {
        public centerInParent(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int nValue = 0;

            if( value.getString().compareToIgnoreCase("true") == 0 ){
                nValue = RelativeLayout.TRUE;
            }

            ((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.CENTER_IN_PARENT, nValue);
        }
    }

}
