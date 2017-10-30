package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;

import com.tencent.rapidview.control.NormalViewPager;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.param.ViewPagerParams;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.ViewPagerParser;

/**
 * @Class RapidViewPager
 * @Desc 光子viewpager
 *
 * @author arlozhang
 * @date 2017.07.03
 */
public class RapidViewPager extends RapidViewGroupObject{

    public RapidViewPager(){}

    @Override
    protected RapidParserObject createParser() {
        return new ViewPagerParser();
    }

    @Override
    protected View createView(Context context){
        return new NormalViewPager(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new ViewPagerParams(context);
    }
}
