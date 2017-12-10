package com.tencent.custom;

import android.content.Context;

import com.tencent.rapidview.config.RapidViewProvider;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.ViewParser;

/**
 * Created by realhe on 2017/12/9.
 */

public class SVGViewProvider extends RapidViewProvider<SVGView,ViewParser> {
    private static SVGViewParser mSVGViewParser = new SVGViewParser();
    @Override
    public String provideViewName() {
        return "svgview";
    }

    @Override
    public ViewParser provideParser() {
        return mSVGViewParser;
    }

    @Override
    public SVGView provideView(Context context) {
        return new SVGView(context);
    }

    @Override
    public int minLimitLevel() {
        return -1;
    }
}
