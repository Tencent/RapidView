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
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class LinearLayoutParams
 * @Desc RapidView界面解析LinearLayout.LayoutParams解析器
 *
 * @author arlozhang
 * @date 2015.09.23
 */
public class LinearLayoutParams extends MarginParams {

    private static Map<String, IFunction> mLinearLayoutClassMap = new ConcurrentHashMap<String, IFunction>();

    static {
        try{
            mLinearLayoutClassMap.put("weight", weight.class.newInstance());
            mLinearLayoutClassMap.put("layoutgravity", layoutgravity.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public LinearLayoutParams(Context context){
        super(context);
    }

    @Override
    protected Object getObject(){
        return new LinearLayout.LayoutParams(0, 0);
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

        IFunction clazz = mLinearLayoutClassMap.get(key);

        return clazz;
    }

    private static class weight implements IFunction {
        public weight(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            ((LinearLayout.LayoutParams)params).weight = value.getFloat();
        }
    }

    private static class layoutgravity implements IFunction {
        public layoutgravity(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            String str = null;

            if( value == null || params == null ){
                return;
            }

            str = value.getString();

            if( str.compareToIgnoreCase("no_gravity") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.NO_GRAVITY;
            }
            else if( str.compareToIgnoreCase("top") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.TOP;
            }
            else if( str.compareToIgnoreCase("bottom") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.BOTTOM;
            }
            else if( str.compareToIgnoreCase("left") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.LEFT;
            }
            else if( str.compareToIgnoreCase("right") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.RIGHT;
            }
            else if( str.compareToIgnoreCase("center_vertical") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.CENTER_VERTICAL;
            }
            else if( str.compareToIgnoreCase("fill_vertical") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.FILL_VERTICAL;
            }
            else if( str.compareToIgnoreCase("center_horizontal") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.CENTER_HORIZONTAL;
            }
            else if( str.compareToIgnoreCase("fill_horizontal") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.FILL_HORIZONTAL;
            }
            else if( str.compareToIgnoreCase("center") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.CENTER;
            }
            else if( str.compareToIgnoreCase("fill") == 0 ){
                ((LinearLayout.LayoutParams)params).gravity = Gravity.FILL;
            }
        }
    }
}
