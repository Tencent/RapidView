package com.tencent.rapidview.control;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tencent.rapidview.deobfuscated.IRapidView;

/**
 * @Class NormalRecyclerViewHolder
 * @Desc RapidView recyclerView的Holder
 *
 * @author arlozhang
 * @date 2017.09.15
 */
public class NormalRecyclerViewHolder extends RecyclerView.ViewHolder{

    private IRapidView mView = null;

    private View mEmptyView = null;

    public NormalRecyclerViewHolder(Context context, IRapidView view){
        super(view.getView());

        mView = view;
    }

    public NormalRecyclerViewHolder(Context context, View emptyView){
        super(emptyView);

        emptyView.setTag("NONE");
        mEmptyView = emptyView;
    }


    public IRapidView getView(){
        return mView;
    }
}
