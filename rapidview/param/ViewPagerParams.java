package com.tencent.rapidview.param;

import android.content.Context;
import android.support.v4.view.ViewPager;

/**
 * @Class ViewPagerParams
 * @Desc RapidView界面ViewPager.LayoutParams解析器
 *
 * @author arlozhang
 * @date 2015.09.23
 */
public class ViewPagerParams extends ViewGroupParams {

    public ViewPagerParams(Context context){
        super(context);
    }

    @Override
    protected Object getObject(){
        return new ViewPager.MarginLayoutParams(0, 0);
    }

}
