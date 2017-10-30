package com.tencent.rapidview.param;

import android.content.Context;
import android.widget.AbsoluteLayout;

/**
 * @Class AbsoluteLayoutParams
 * @Desc RapidView界面AbsoluteLayout.LayoutParams解析器
 *
 * @author arlozhang
 * @date 2015.10.08
 */
public class AbsoluteLayoutParams extends ViewGroupParams {

    public AbsoluteLayoutParams(Context context){
        super(context);
    }

    @Override
    protected Object getObject(){
        return new AbsoluteLayout.LayoutParams(0, 0, 0, 0);
    }
}
