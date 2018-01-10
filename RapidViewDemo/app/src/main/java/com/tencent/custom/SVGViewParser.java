package com.tencent.custom;

import android.util.Log;

import com.tencent.rapidview.config.RapidAttribute;
import com.tencent.rapidview.config.RapidViewParserRouter;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.debug.RapidLog;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.ViewParser;

/**
 * Created by realhe on 2017/12/9.
 */

public class SVGViewParser extends ViewParser{
    private static SVGViewParserRouter mSVGViewParserRouter = new SVGViewParserRouter();
    public SVGViewParser() {
        super();
    }

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view) {
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mSVGViewParserRouter.getParserFunction(key);
        return clazz;
    }

    private static class SVGViewParserRouter extends RapidViewParserRouter<SVGView>{
        @RapidAttribute("svg")
        public class svgRooter extends AttributeProcessor<SVGView>{
            @Override
            public void process(RapidParserObject object, SVGView view, String key, Var value) {
                RapidLog.d("realhe","touch" + key);
            }
        }
    }
}
