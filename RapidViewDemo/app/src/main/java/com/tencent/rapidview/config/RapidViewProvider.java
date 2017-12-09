package com.tencent.rapidview.config;

import android.content.Context;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.tencent.rapidview.param.FrameLayoutParams;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.HorizontalScrollViewParser;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.view.RapidViewGroupObject;
import com.tencent.rapidview.view.RapidViewObject;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by realhe on 2017/12/9.
 */

public abstract class RapidViewProvider<V extends View,P extends RapidParser<V>> extends RapidViewGroupObject {
    public  RapidViewProvider(){}

    @Override
    protected RapidParserObject createParser(){
        return provideParser();
    }

    @Override
    protected View createView(Context context){
        return provideView(context);
    }

    public abstract P provideParser();

    public abstract V provideView(Context context);


}
