package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;

import com.tencent.rapidview.control.NormalScrollView;
import com.tencent.rapidview.param.FrameLayoutParams;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.ScrollViewParser;

/**
 * @Class RapidScrollView
 * @Desc 光子界面ScrollView
 *
 * @author arlozhang
 * @date 2016.03.31
 */
public class RapidScrollView extends RapidViewGroupObject {

    public RapidScrollView(){}

    @Override
    protected RapidParserObject createParser(){
        return new ScrollViewParser();
    }

    @Override
    protected View createView(Context context){
        return new NormalScrollView(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new FrameLayoutParams(context);
    }
}
