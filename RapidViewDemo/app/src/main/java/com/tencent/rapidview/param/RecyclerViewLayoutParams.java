package com.tencent.rapidview.param;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * @Class RecyclerViewLayoutParams
 * @Desc RecyclerViewd的Params类
 *
 * @author arlozhang
 * @date 2017.09.14
 */

public class RecyclerViewLayoutParams extends MarginParams {

    public RecyclerViewLayoutParams(Context context){
        super(context);
    }

    @Override
    protected Object getObject(){
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
