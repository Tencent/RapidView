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
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.ViewUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ViewGroupParam
 * @Desc RapidView界面ViewGroup.LayoutParams解析器
 *
 * @author arlozhang
 * @date 2015.09.23
 */
public class ViewGroupParams extends ParamsObject {

    protected static long mScreenWidth = 0;

    protected static long mScreenHeight = 0;

    private static Map<String, IFunction> mViewGroupClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mViewGroupClassMap.put("height", height.class.newInstance());
            mViewGroupClassMap.put("width", width.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ViewGroupParams(Context context){
        super(context);
        initScreenParams(context);
    }

    private void initScreenParams(Context context){
        if( mScreenHeight != 0 && mScreenWidth != 0 ) {
            return;
        }

        if( context == null ){
            return;
        }

        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        mScreenWidth = display.getWidth();
        mScreenHeight = display.getHeight();
    }

    @Override
    protected Object getObject(){
        return new ViewGroup.LayoutParams(0, 0);
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

        IFunction clazz = mViewGroupClassMap.get(key);

        return clazz;
    }

    private static class height implements IFunction {
        public height(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            int height = 0;
            String str = null;

            if( value == null || params == null ){
                return;
            }

            str = value.getString();

            if( str.compareToIgnoreCase("fill_parent") == 0 ){
                height = ViewGroup.LayoutParams.FILL_PARENT;
            }
            else if( str.compareToIgnoreCase("match_parent") == 0){
                height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            else if( str.compareToIgnoreCase("wrap_content") == 0 ){
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            else{
                if( str.length() >= 1 && str.substring(str.length() - 1).compareToIgnoreCase("%") == 0 ){
                    float percent = Float.parseFloat(str.substring(0, str.length() - 1)) / 100;
                    height = (int)(percent * ((ViewGroupParams)object).mScreenHeight);
                }
                else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%x") == 0 ){
                    float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                    height = (int)(percent * ((ViewGroupParams)object).mScreenWidth);
                }
                else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%y") == 0 ){
                    float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                    height = (int)(percent * ((ViewGroupParams)object).mScreenHeight);
                }
                else{
                    height = ViewUtils.dip2px(object.mContext, value.getFloat());
                }
            }

            params.height = height;
        }
    }

    private static class width implements IFunction {
        public width(){}

        public void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value){
            int width = 0;
            String str = null;

            if( value == null || params == null ){
                return;
            }

            str = value.getString();

            if( str.compareToIgnoreCase("fill_parent") == 0 ){
                width = ViewGroup.LayoutParams.FILL_PARENT;
            }
            else if( str.compareToIgnoreCase("match_parent") == 0){
                width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            else if( str.compareToIgnoreCase("wrap_content") == 0 ){
                width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            else{
                if( str.length() >= 1 && str.substring(str.length() - 1).compareToIgnoreCase("%") == 0 ){
                    float percent = Float.parseFloat(str.substring(0, str.length() - 1)) / 100;
                    width = (int)(percent * ((ViewGroupParams)object).mScreenWidth);
                }
                else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%x") == 0 ){
                    float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                    width = (int)(percent * ((ViewGroupParams)object).mScreenWidth);
                }
                else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%y") == 0 ){
                    float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                    width = (int)(percent * ((ViewGroupParams)object).mScreenHeight);
                }
                else{
                    width = ViewUtils.dip2px(((ViewGroupParams)object).mContext, value.getFloat());
                }
            }

            params.width = width;
        }
    }
}
