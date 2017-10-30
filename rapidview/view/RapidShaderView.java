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
