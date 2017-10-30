package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.tencent.rapidview.param.LinearLayoutParams;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.LinearLayoutParser;
import com.tencent.rapidview.parser.RapidParserObject;

/**
 * @Class RapidLinearLayout
 * @Desc 光子界面LinearLayout
 *
 * @author arlozhang
 * @date 2015.10.08
 */
public class RapidLinearLayout extends RapidViewGroupObject {

    public RapidLinearLayout(){}

    @Override
    protected RapidParserObject createParser(){
        return new LinearLayoutParser();
    }

    @Override
    protected View createView(Context context){
        return new LinearLayout(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new LinearLayoutParams(context);
    }
}
