package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;

import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.ViewStubParser;

/**
 * @Class RapidViewStub
 * @Desc 光子界面ViewStub,光子的ViewStub由自己实现，因此只创建一个View对象
 *
 * @author arlozhang
 * @date 2016.03.22
 */
public class RapidViewStub extends RapidViewObject {

    public RapidViewStub(){}

    @Override
    protected RapidParserObject createParser(){
        return new ViewStubParser();
    }

    @Override
    protected View createView(Context context){
        return new View(context);
    }
}
