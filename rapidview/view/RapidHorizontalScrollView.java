package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.tencent.rapidview.param.FrameLayoutParams;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.HorizontalScrollViewParser;
import com.tencent.rapidview.parser.RapidParserObject;

/**
 * @Class RapidHorizontalScrollView
 * @Desc 光子界面HorizontalScrollView
 *
 * @author arlozhang
 * @date 2016.04.01
 */
public class RapidHorizontalScrollView extends RapidViewGroupObject {

    public RapidHorizontalScrollView(){}

    @Override
    protected RapidParserObject createParser(){
        return new HorizontalScrollViewParser();
    }

    @Override
    protected View createView(Context context){
        return new HorizontalScrollView(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new FrameLayoutParams(context);
    }
}
