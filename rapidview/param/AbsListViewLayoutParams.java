package com.tencent.rapidview.param;

import android.content.Context;
import android.widget.AbsListView;

/**
 * @Class AbsListViewLayoutParams
 * @Desc RapidView界面解析AbsListView.LayoutParams解析器
 *
 * @author arlozhang
 * @date 2015.10.08
 */
public class AbsListViewLayoutParams extends ViewGroupParams {

    public AbsListViewLayoutParams(Context context){
        super(context);
    }

    @Override
    protected Object getObject(){
        return new AbsListView.LayoutParams(0, 0);
    }
}
