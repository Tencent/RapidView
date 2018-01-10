package com.tencent.rapidview.action;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.framework.RapidRuntimeCachePool;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class CacheViewAction
 * @Desc 主动发起XML缓存的ACTION
 *
 * @author arlozhang
 * @date 2016.07.27
 */
public class CacheViewAction extends ActionObject {

    public CacheViewAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){
        Var xml = mMapAttribute.get("xml");

        if( xml == null ){
            return false;
        }

        return RapidRuntimeCachePool.getInstance().set(getRapidView().getParser().getRapidID(), xml.getString(), getRapidView().getParser().isLimitLevel());
    }
}
