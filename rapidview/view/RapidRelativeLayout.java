package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.param.RelativeLayoutParams;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.RelativeLayoutParser;

/**
 * @Class RapidRelativeLayout
 * @Desc 光子界面RelativeLayoutView
 *
 * @author arlozhang
 * @date 2015.09.24
 */
public class RapidRelativeLayout extends RapidViewGroupObject {

    public RapidRelativeLayout(){}

    @Override
    protected RapidParserObject createParser(){
        return new RelativeLayoutParser();
    }

    @Override
    protected View createView(Context context){
        return new RelativeLayout(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new RelativeLayoutParams(context);
    }
}
