package com.tencent.rapidview.config;

import android.content.Context;
import android.view.View;

import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.view.RapidViewGroupObject;
import com.tencent.rapidview.view.RapidViewObject;

/**
 * Created by realhe on 2017/12/9.
 */

public abstract class RapidViewProvider<V extends View,P extends RapidParserObject> extends RapidViewObject {
    public RapidViewProvider(){}

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

    public abstract int minLimitLevel();
    public abstract String provideViewName();

}
