package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;

import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.param.RelativeLayoutParams;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.RapidRuntimeViewParser;
import com.tencent.rapidview.runtime.RuntimeView;

/**
 * @Class RapidRuntimeView
 * @Desc 光子界面RapidRuntimeView
 *
 * @author arlozhang
 * @date 2016.04.13
 */
public class RapidRuntimeView extends RapidViewGroupObject {

    public RapidRuntimeView(){}

    @Override
    protected RapidParserObject createParser(){
        return new RapidRuntimeViewParser();
    }

    @Override
    protected View createView(Context context){
        return new RuntimeView(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new RelativeLayoutParams(context);
    }
}
