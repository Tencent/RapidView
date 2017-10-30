package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.tencent.rapidview.parser.ImageViewParser;
import com.tencent.rapidview.parser.RapidParserObject;

/**
 * @Class RapidImageView
 * @Desc 光子界面ImageView
 *
 * @author arlozhang
 * @date 2015.10.08
 */
public class RapidImageView extends RapidViewObject {

    public RapidImageView(){}

    @Override
    protected RapidParserObject createParser(){
        return new ImageViewParser();
    }

    @Override
    protected View createView(Context context){
        return new ImageView(context);
    }
}
