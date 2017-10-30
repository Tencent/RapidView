package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;

import com.tencent.rapidview.control.NormalRecyclerView;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.param.RecyclerViewLayoutParams;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.RecyclerViewParser;

/**
 * @Class RapidRecyclerView
 * @Desc 光子界面ViewStub,光子的ViewStub由自己实现，因此只创建一个View对象
 *
 * @author arlozhang
 * @date 2017.09.14
 */
public class RapidRecyclerView extends RapidViewGroupObject{

    public RapidRecyclerView(){}

    @Override
    protected RapidParserObject createParser(){
        return new RecyclerViewParser();
    }

    @Override
    protected View createView(Context context){
        return new NormalRecyclerView(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new RecyclerViewLayoutParams(context);
    }
}
