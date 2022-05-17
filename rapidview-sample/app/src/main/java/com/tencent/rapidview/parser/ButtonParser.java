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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.ViewUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ButtonParser
 * @Desc RapidView Button解析类
 *
 * @date 2016.02.18
 */
public final class ButtonParser extends TextViewParser {

    private static Map<String, IFunction> mButtonClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mButtonClassMap.put("gradientdrawable", initcornerradius.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ButtonParser(){}

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null){
            return null;
        }

        RapidParserObject.IFunction clazz = mButtonClassMap.get(key);

        return clazz;
    }


    private static class initcornerradius implements IFunction {
        public initcornerradius(){}

        /*像这种drawable都可以通过别的方式实现，这里实现了一下，但不推荐使用，类设为final，以免他人
        * 继承后出现参数失效的情况感到疑惑*/
        public void run(RapidParserObject object, Object view, Var value){
            Map<String,String> map = RapidStringUtils.stringToMap(value.getString());

            GradientDrawable drawable = new GradientDrawable();

            String width = map.get("strokewidth");
            String color = map.get("strokecolor");

            initGradientDrawableCornerRadius(object.getContext(), drawable, map.get("cornerradius"));
            initGradientDrawableColor(drawable, map.get("color"));
            initGradientDrawableShape(drawable, map.get("shape"));

            if( width != null && color != null ){
                initGradientStroke(drawable, width, color);
            }

            ((Button) view).setBackgroundDrawable(drawable);
        }

        private void initGradientDrawableCornerRadius(Context context, GradientDrawable drawable, String value){
            if( drawable == null || value == null ){
                return;
            }

            float radius = ViewUtils.dip2px(context, Float.parseFloat(value));

            drawable.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        }


        private void initGradientDrawableColor(GradientDrawable drawable, String value) {
            if (drawable == null || value == null) {
                return;
            }

            drawable.setColor(Color.parseColor("#" + value));
        }

        private void initGradientDrawableShape(GradientDrawable drawable, String value) {
            if (drawable == null || value == null) {
                return;
            }

            drawable.setShape(Integer.parseInt(value));
        }

        private void initGradientStroke(GradientDrawable drawable, String width, String color) {
            if (drawable == null || width == null || color == null ) {
                return;
            }

            drawable.setStroke(Integer.parseInt(width), Color.parseColor("#" + color));
        }
    }


}
