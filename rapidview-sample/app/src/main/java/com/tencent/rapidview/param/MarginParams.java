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

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.ViewUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class MarginParams
 * @Desc RapidView界面解析ViewGroup.MarginLayoutParams解析器
 *
 * @author arlozhang
 * @date 2015.09.23
 */
public class MarginParams extends ViewGroupParams {

    private static Map<String, IFunction> mMarginClassMap = new ConcurrentHashMap<String, IFunction>();

    static {
        try{
            mMarginClassMap.put("margin", margin.class.newInstance());
            mMarginClassMap.put("marginleft", marginLeft.class.newInstance());
            mMarginClassMap.put("margintop", marginTop.class.newInstance());
            mMarginClassMap.put("marginright", marginRight.class.newInstance());
            mMarginClassMap.put("marginbottom", marginBottom.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public MarginParams(Context context){
        super(context);
    }

    @Override
    protected Object getObject(){
        return new ViewGroup.MarginLayoutParams(0, 0);
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

        IFunction clazz = mMarginClassMap.get(key);

        return clazz;
    }

    private static class margin implements IFunction {
        public margin(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            String[] margin = value.getString().split(",");

            if( margin.length < 4 ){
                return;
            }

            ((ViewGroup.MarginLayoutParams)params).setMargins(ViewUtils.dip2px(object.mContext, Float.parseFloat(margin[0])),
                    ViewUtils.dip2px(object.mContext, Float.parseFloat(margin[1])),
                    ViewUtils.dip2px(object.mContext, Float.parseFloat(margin[2])),
                    ViewUtils.dip2px(object.mContext, Float.parseFloat(margin[3])) );
        }
    }

    private static class marginLeft implements IFunction {
        public marginLeft(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int left = 0;

            try{
                left = ViewUtils.dip2px(object.mContext, value.getFloat());
            }
            catch (Exception e){
                e.printStackTrace();
            }

            ((ViewGroup.MarginLayoutParams)params).leftMargin = left;
        }
    }

    private static class marginTop implements IFunction {
        public marginTop(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int top = 0;

            try{
                top = ViewUtils.dip2px(object.mContext, value.getFloat());
            }
            catch (Exception e){
                e.printStackTrace();
            }

            ((ViewGroup.MarginLayoutParams)params).topMargin = top;
        }
    }

    private static class marginRight implements IFunction {
        public marginRight(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int right = 0;

            try{
                right = ViewUtils.dip2px(object.mContext, value.getFloat());
            }
            catch (Exception e){
                e.printStackTrace();
            }

            ((ViewGroup.MarginLayoutParams)params).rightMargin = right;
        }
    }

    private static class marginBottom implements IFunction {
        public marginBottom(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            if( value == null || params == null ){
                return;
            }

            int bottom = 0;

            try{
                bottom = ViewUtils.dip2px(object.mContext, value.getFloat());
            }
            catch (Exception e){
                e.printStackTrace();
            }

            ((ViewGroup.MarginLayoutParams)params).bottomMargin = bottom;
        }
    }

}
