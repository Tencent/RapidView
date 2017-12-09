package com.tencent.custom;

import android.content.Context;
import android.widget.TextView;

import com.tencent.rapidview.config.RapidViewProvider;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.TextViewParser;

/**
 * Created by realhe on 2017/12/9.
 */

public class SVGViewProvider extends RapidViewProvider<SVGView,SVGParser> {
    @Override
    public SVGParser provideParser() {
        return null;
    }

    @Override
    public SVGView provideView(Context context) {
        return null;
    }

    @Override
    public ParamsObject createParams(Context context) {
        return null;
    }
}
