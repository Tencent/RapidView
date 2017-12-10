package com.tencent.rapidview.config;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.debug.RapidLog;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.ViewParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by realhe on 2017/12/9.
 */

public abstract class RapidViewParserRouter<V> {
    private Map<String, AttributeProcessor<V>> mAttrProcessorMap = new ConcurrentHashMap<>();
    public RapidViewParserRouter(){
        initRouter();
    }

    private void initRouter(){
        RapidLog.d("realhe","init");
        try {
            Class routerClass = this.getClass();
            for (Class processorClass : routerClass.getClasses()){
                // If the class have no attribute annotaion, continue
                if (!processorClass.isAnnotationPresent(RapidAttribute.class)){
                    continue;
                }
                RapidAttribute rapidAttribute =(RapidAttribute)processorClass.getAnnotation(RapidAttribute.class);
                String key = rapidAttribute.value();

                // Instantiate the processor
                Constructor constructor = processorClass.getDeclaredConstructor(this.getClass());
                AttributeProcessor<V> attributeProcessor = (AttributeProcessor) constructor.newInstance(this);
                mAttrProcessorMap.put(key,attributeProcessor);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public RapidParserObject.IFunction getParserFunction(String key){
        return new AttributeParserFunction(key);
    }

    private class AttributeParserFunction implements RapidParserObject.IFunction{
        private String mKey = "";
        public AttributeParserFunction(String key) {
            super();
            this.mKey = key;
        }
        @Override
        public void run(RapidParserObject object, Object view, Var value) {
            try{
                V realView = (V)view;
                AttributeProcessor<V> processor = getProcessorByKey(this.mKey);
                if (processor != null){
                    processor.process(object,realView,this.mKey,value);
                }
            }catch (ClassCastException castException){
                castException.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private AttributeProcessor<V> getProcessorByKey(String key){
        try{
            return mAttrProcessorMap.get(key);
        }catch (Exception e){
            return null;
        }
    }


    protected abstract class AttributeProcessor<V>{
        public AttributeProcessor() {
        }
        public abstract void process(RapidParserObject object, V view, String key, Var value);
    }


}
