package com.tencent.rapidview.filter;

import org.w3c.dom.Element;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class FilterChooser
 * @Desc 过滤条件选择器
 *
 * @author arlozhang
 * @date 2016.03.16
 */
public class FilterChooser {

    private static Map<String, IFunction> mAllClassMap = new ConcurrentHashMap<String, IFunction>();

    private static Map<String, IFunction> mLimitClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mAllClassMap.put("datafilter", DataFilterGeter.class.newInstance());
            mAllClassMap.put("networkfilter", NetWorkGeter.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try{
            mLimitClassMap.put("datafilter", DataFilterGeter.class.newInstance());
            mLimitClassMap.put("networkfilter", NetWorkGeter.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static FilterObject get(Element element,  Map<String, String> mapEnv, boolean limitLevel){
        IFunction function;

        if( element == null ){
            return null;
        }

        if( limitLevel ){
            function = mLimitClassMap.get(element.getTagName().toLowerCase());
        }
        else{
            function = mAllClassMap.get(element.getTagName().toLowerCase());
        }

        if( function == null ){
            return null;
        }

        return function.get(element, mapEnv);
    }

    private interface IFunction{
        FilterObject get(Element element,  Map<String, String> mapEnv);
    }

    private static class DataFilterGeter implements IFunction{
        public DataFilterGeter(){}
        @Override
        public FilterObject get(Element element,  Map<String, String> mapEnv){
            return new DataFilter(element, mapEnv);
        }
    }

    private static class NetWorkGeter implements IFunction{
        public NetWorkGeter(){}
        @Override
        public FilterObject get(Element element,  Map<String, String> mapEnv){
            return new NetWorkFilter(element, mapEnv);
        }
    }
}
