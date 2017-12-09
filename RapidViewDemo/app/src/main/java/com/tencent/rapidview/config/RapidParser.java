package com.tencent.rapidview.config;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.ViewParser;

import java.util.HashMap;

/**
 * Created by realhe on 2017/12/9.
 */

public abstract class RapidParser<V> extends ViewParser{
    private static HashMap<String,AttributeProcessor> attrsMap = new HashMap<>();

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null){
            return null;
        }

        RapidParserObject.IFunction clazz = new AttributeParserFunction(key);

        return clazz;
    }

    public class AttributeParserFunction implements IFunction{
        private String mKey = "";
        public AttributeParserFunction(String key) {
            super();
            this.mKey = key;
        }
        @Override
        public void run(RapidParserObject object, Object view, Var value) {
            try{
                V realView = (V)view;
                process(object,realView,mKey,value);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private interface AttributeProcessor<V>{
        void process(RapidParserObject object, V view, String key, Var value);
    }
    public abstract void process(RapidParserObject object, V view, String key, Var value);
}
