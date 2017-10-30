package com.tencent.rapidview.view;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.TextViewParser;


/**
 * @Class RapidTextView
 * @Desc 光子界面TextView
 *
 * @author arlozhang
 * @date 2015.09.24
 */
public class RapidTextView extends RapidViewObject {

    public RapidTextView(){}

    @Override
    protected RapidParserObject createParser(){
        return new TextViewParser();
    }

    @Override
    protected View createView(Context context){
        return new TextView(context);
    }
}
