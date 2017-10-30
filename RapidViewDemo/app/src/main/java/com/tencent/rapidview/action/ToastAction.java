package com.tencent.rapidview.action;

import android.widget.Toast;

import com.tencent.rapidview.data.Var;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class ToastAction
 * @Desc 弹出toast的Action
 *
 * @author arlozhang
 * @date 2016.03.24
 */
public class ToastAction extends ActionObject{

    public ToastAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }


    @Override
    public boolean run() {
        Var value = mMapAttribute.get("value");

        if( value == null ){
            value = new Var("");
        }

        if( mRapidView == null ){
            return false;
        }

        Toast.makeText(mRapidView.getView().getContext(), value.getString(), Toast.LENGTH_SHORT).show();

        return true;
    }
}
