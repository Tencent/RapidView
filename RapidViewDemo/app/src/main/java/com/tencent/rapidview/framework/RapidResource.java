package com.tencent.rapidview.framework;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidResource
 * @Desc 由于res里面的资源经过混淆后无法反射读到，因此这里将需要的资源配置成静态ID，供配置读取
 *
 * @author arlozhang
 * @date 2016.05.07
 */
public class RapidResource {

    public static final Map<String, Integer> mResourceMap = new ConcurrentHashMap<String, Integer>();

    static {
        //mResourceMap.put("pic_defaule", R.drawable.pic_defaule);
    }
}
