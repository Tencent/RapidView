package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.tencent.rapidview.param.FrameLayoutParams;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.FrameLayoutParser;
import com.tencent.rapidview.parser.RapidParserObject;

/**
 * @Class RapidFrameLayout
 * @Desc 光子界面FrameLayoutView
 *
 * @author arlozhang
 * @date 2016.03.22
 */
public class RapidFrameLayout extends RapidViewGroupObject {

    public RapidFrameLayout(){}

    @Override
    protected RapidParserObject createParser(){
        return new FrameLayoutParser();
    }

    @Override
    protected View createView(Context context){
        return new FrameLayout(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new FrameLayoutParams(context);
    }
}
