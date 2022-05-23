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
package com.tencent.rapidview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;
import android.widget.RelativeLayout;

import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.param.RelativeLayoutParams;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.ShaderViewParser;

/**
 * @Class RapidShaderView
 * @Desc 实现各种特效的View
 *
 * @author arlozhang
 * @date 2016.04.19
 */
public class RapidShaderView extends RapidRelativeLayout {

    public RapidShaderView(){}

    @Override
    protected RapidParserObject createParser(){
        return new ShaderViewParser();
    }

    @Override
    protected View createView(Context context){
        return new ShaderView(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new RelativeLayoutParams(context);
    }

    public class ShaderView extends RelativeLayout{

        private Shader mShader = null;

        public ShaderView(Context context) {
            super(context);
        }

        public void setShader(Shader shader){
            mShader = shader;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint;

            if(mShader == null){
                return;
            }

            paint = new Paint();

            paint.setShader(mShader);

            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }
    }

}
