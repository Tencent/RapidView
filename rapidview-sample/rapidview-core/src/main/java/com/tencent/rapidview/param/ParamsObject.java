/***************************************************************************************************
 Tencent is pleased to support the open source community by making RapidView available.
 Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 Licensed under the MITLicense (the "License"); you may not use this file except in compliance
 withthe License. You mayobtain a copy of the License at

 http://opensource.org/licenses/MIT

 Unless required by applicable law or agreed to in writing, software distributed under the License is
 distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied. See the License for the specific language governing permissions and limitations under the
 License.
 ***************************************************************************************************/
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

    public void fillLayoutParams(String key, Var value, Map<String, IRapidView> brotherMap){
        IFunction function = null;

        if( key == null || value == null ){
            return;
        }


        function = getAttributeFunction(key);

        if( function == null ){
            return;
        }

        try{
            function.run(this, getLayoutParams(), brotherMap, value);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    protected abstract Object getObject();

    protected IFunction getAttributeFunction(String key){
        return null;
    }
}
