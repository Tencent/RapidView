package com.tencent.rapidview.param;

import android.content.Context;
import android.view.ViewGroup;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidParams;
import com.tencent.rapidview.deobfuscated.IRapidView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Class ParamsObject
 * @Desc RapidView界面解析param基类
 *
 * @author arlozhang
 * @date 2015.09.23
 */
public abstract class ParamsObject implements IRapidParams {
    protected Context mContext;
    protected Object mParam;

    protected List<ATTRIBUTE_FUN_NODE> mInitFunNodeList = null;

    private class ATTRIBUTE_FUN_NODE{
        public IFunction function = null;
        public String value;
    }

    ParamsObject(Context context){
        this.mContext = context;
        mInitFunNodeList = new ArrayList<ATTRIBUTE_FUN_NODE>();
    }

    protected interface IFunction {
        void run(ParamsObject object, ViewGroup.LayoutParams params, Map<String, IRapidView> brotherMap, Var value);
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams(){

        if( mParam == null ){
            mParam = getObject();
        }

        if( mParam == null ){
            return null;
        }

        if( !(mParam instanceof ViewGroup.LayoutParams) ){
            return null;
        }

        return (ViewGroup.LayoutParams)mParam;
    }

    public void fillLayoutParams(Map<String, Var> attrMap, Map<String, IRapidView> brotherMap){
        if( attrMap == null ){
            return;
        }

        for( Map.Entry<String, Var> entry : attrMap.entrySet() ){
            IFunction function = getAttributeFunction(entry.getKey().toLowerCase());

            if( function == null ){
                continue;
            }

            try{
                function.run(this, getLayoutParams(), brotherMap, entry.getValue());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    protected abstract Object getObject();

    protected IFunction getAttributeFunction(String key){
        return null;
    }
}
