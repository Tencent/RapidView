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

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.ViewUtils;
import com.tencent.rapidview.view.RapidShaderView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShaderViewParser extends RelativeLayoutParser {

    private static Map<String, IFunction> mShaderMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mShaderMap.put("lineargradient", initlineargradient.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ShaderViewParser(){}

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mShaderMap.get(key);

        return clazz;
    }


        private static class initlineargradient implements IFunction {
        public initlineargradient(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> listParams = RapidStringUtils.stringToList(value.getString());
            Shader lineargradient = null;
            int[] arrayColor;
            Shader.TileMode mode = Shader.TileMode.CLAMP;
            String strMode;

            if( listParams.size() < 7 || object.mRapidView == null ){
                return;
            }

            arrayColor = new int[listParams.size() - 5];

            for( int i = 0; i < arrayColor.length; i++ ){
                arrayColor[i] = Color.parseColor("#" + listParams.get(i + 4));
            }

            strMode = listParams.get(listParams.size() - 1).toLowerCase();

            if( strMode.compareTo("mirror") == 0 ){
                mode = Shader.TileMode.MIRROR;
            }
            else if( strMode.compareTo("repeat") == 0 ){
                mode = Shader.TileMode.REPEAT;
            }

            lineargradient = new LinearGradient(ViewUtils.dip2px(object.mRapidView.getView().getContext(), Float.parseFloat(listParams.get(0))),
                    ViewUtils.dip2px(object.mRapidView.getView().getContext(), Float.parseFloat(listParams.get(1))),
                    ViewUtils.dip2px(object.mRapidView.getView().getContext(), Float.parseFloat(listParams.get(2))),
                    ViewUtils.dip2px(object.mRapidView.getView().getContext(), Float.parseFloat(listParams.get(3))),
                    arrayColor, null, mode);

            ((RapidShaderView.ShaderView)view).setShader(lineargradient);

            ((RapidShaderView.ShaderView) view).invalidate();
        }
    }

}
