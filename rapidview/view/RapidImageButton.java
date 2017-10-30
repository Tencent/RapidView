package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.tencent.rapidview.parser.ImageButtonParser;
import com.tencent.rapidview.parser.RapidParserObject;

/**
 * @Class RapidImageButton
 * @Desc 光子界面ImageButton
 *
 * @author arlozhang
 * @date 2015.10.09
 */
public class RapidImageButton extends RapidViewObject {

    public RapidImageButton(){}

    @Override
    protected RapidParserObject createParser(){
        return new ImageButtonParser();
    }

    @Override
    protected View createView(Context context){
        return new ImageButton(context);
    }
}
