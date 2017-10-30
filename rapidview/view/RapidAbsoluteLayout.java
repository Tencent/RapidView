package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.tencent.rapidview.param.AbsoluteLayoutParams;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.AbsoluteLayoutParser;
import com.tencent.rapidview.parser.RapidParserObject;

/**
 * @Class RapidAbsoluteLayout
 * @Desc 光子界面AbsoluteLayout
 *
 * @author arlozhang
 * @date 2015.10.08
 */
public class RapidAbsoluteLayout extends RapidViewGroupObject {

    public RapidAbsoluteLayout(){}

    @Override
    protected RapidParserObject createParser(){
        return new AbsoluteLayoutParser();
    }

    @Override
    protected View createView(Context context){
        return new AbsoluteLayout(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new AbsoluteLayoutParams(context);
    }
}
