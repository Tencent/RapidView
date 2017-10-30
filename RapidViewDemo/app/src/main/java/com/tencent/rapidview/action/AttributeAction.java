package com.tencent.rapidview.action;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class AttributeAction
 * @Desc 调整参数的action
 *
 * @author arlozhang
 * @date 2017.10.09
 */
public class AttributeAction extends ActionObject{

    public AttributeAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){
        IRapidView view;
        Var id = mMapAttribute.get("cid");
        Var key = mMapAttribute.get("key");
        Var value = mMapAttribute.get("value");

        if( mRapidView == null ){
            return false;
        }


        if( mRapidView.getParser().getID().compareToIgnoreCase(id.getString()) == 0 ) {

            mRapidView.getParser().update(key.getString(), value);
            return true;
        }

        view = mRapidView.getParser().getChildView(id.getString());
        if (view == null) {
            return false;
        }

        view.getParser().update(key.getString(), value);

        return true;
    }
}
