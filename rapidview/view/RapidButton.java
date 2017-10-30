package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.tencent.rapidview.parser.ButtonParser;
import com.tencent.rapidview.parser.RapidParserObject;

/**
 * @Class RapidButton
 * @Desc 光子界面Button
 *
 * @author arlozhang
 * @date 2016.02.18
 */
public class RapidButton extends RapidViewObject {

    public RapidButton(){}

    @Override
    protected RapidParserObject createParser(){
        return new ButtonParser();
    }

    @Override
    protected View createView(Context context){
        return new Button(context);
    }
}
