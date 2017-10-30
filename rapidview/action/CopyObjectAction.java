package com.tencent.rapidview.action;

import com.tencent.rapidview.data.RapidDataBinder;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class CopyObjectAction
 * @Desc 拷贝目标资源池中的对象
 *
 * @author arlozhang
 * @date 2016.08.16
 */
public class CopyObjectAction extends ActionObject{

    public CopyObjectAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){
        RapidDataBinder binder = getBinder();
        String origin = mMapAttribute.get("origin").getString();
        String target = mMapAttribute.get("target").getString();
        Object obj;

        if( binder == null ){
            return false;
        }

        obj = binder.getObject(origin);
        if( obj == null ){
            return false;
        }

        binder.setObject(target, obj);

        return true;
    }
}
