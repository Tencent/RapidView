package com.tencent.rapidview.action;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class InvalidateAction
 * @Desc 无效化界面动作
 *
 * @author arlozhang
 * @date 2018.01.05
 */
public class InvalidateAction extends ActionObject{

    public InvalidateAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){

        mRapidView.getView().invalidate();
        mRapidView.getView().requestLayout();

        return true;
    }
}
