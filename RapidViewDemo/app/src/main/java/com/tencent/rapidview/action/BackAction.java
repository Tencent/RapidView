package com.tencent.rapidview.action;

import android.view.KeyEvent;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class BackAction
 * @Desc 返回动作
 *
 * @author arlozhang
 * @date 2016.08.04
 */
public class BackAction extends ActionObject{

    public BackAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){
        try{
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }
}
