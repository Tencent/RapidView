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
package com.tencent.rapidview.parser;

import com.tencent.rapidview.data.DataExpressionsParser;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.runtime.RuntimeView;
import com.tencent.rapidview.utils.FileUtil;
import com.tencent.rapidview.utils.RapidStringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidRuntimeViewParser
 * @Desc RapidRuntimeViewParser解析类
 *
 * @author arlozhang
 * @date 2016.04.13
 */
public class RapidRuntimeViewParser extends ViewGroupParser{

    private static Map<String, IFunction> mRuntimeViewClassMap = new ConcurrentHashMap<String, IFunction>();

    private Var mRapidID = null;

    private Var mLimitLevel = null;

    private Var mUrl = null;

    private Var mMd5 = null;

    private Map<String, String> mParamsMap = null;

    private IRapidView mView = null;

    private String mSucceedTaskID = "";

    private String mFailedTaskID = "";

    static{
        try{
            mRuntimeViewClassMap.put("rapidid", initrapidid.class.newInstance());
            mRuntimeViewClassMap.put("limitlevel", initlimitlevel.class.newInstance());
            mRuntimeViewClassMap.put("url", initurl.class.newInstance());
            mRuntimeViewClassMap.put("md5", initmd5.class.newInstance());
            mRuntimeViewClassMap.put("params", initparams.class.newInstance());
            mRuntimeViewClassMap.put("succeed", initsucceed.class.newInstance());
            mRuntimeViewClassMap.put("failed", initfailed.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mRuntimeViewClassMap.get(key);

        return clazz;
    }

    protected void finish(){
        if( mRapidID == null || mLimitLevel == null || mUrl == null || mMd5 == null || mContext == null || mParamsMap == null ){
            return;
        }

        if( mRapidView == null ){

            return;
        }

        if( mRapidID.getString().equals("") ||
            mMd5.getString().equals("")      ||
            mUrl.getString().equals("")      ||
            mLimitLevel.getString().equals("") ){

            return;
        }

        RuntimeView view = (RuntimeView) mRapidView.getView();

        if( RapidConfig.DEBUG_MODE && FileUtil.isFileExists(FileUtil.getRapidDebugDir() + "main.xml") ){
            view.loadDirect(mRapidID.getString(), "main.xml", mLimitLevel.getInt(), null, new RuntimeView.IListener() {
                @Override
                public void onFailed() {
                    if( RapidStringUtils.isEmpty(mFailedTaskID) ){
                        return;
                    }

                    run(mFailedTaskID);
                }

                @Override
                public void onSucceed(IRapidView rapidView) {
                    mView = rapidView;

                    if( !RapidStringUtils.isEmpty(mSucceedTaskID) ){
                        run(mSucceedTaskID);
                    }
                }
            });

            return;
        }

        view.load(mRapidID.getString(), mMd5.getString(), mUrl.getString(), mLimitLevel.getInt(), null, new RuntimeView.IListener() {
            @Override
            public void onFailed() {

            }

            @Override
            public void onSucceed(IRapidView rapidView) {
                mView = rapidView;
            }
        });
    }

    private static class initrapidid implements IFunction {
        public initrapidid(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((RapidRuntimeViewParser)object).mRapidID = value;
            ((RapidRuntimeViewParser)object).finish();
        }
    }

    private static class initlimitlevel implements IFunction {
        public initlimitlevel(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((RapidRuntimeViewParser)object).mLimitLevel = value;
            ((RapidRuntimeViewParser)object).finish();
        }
    }

    private static class initurl implements IFunction {
        public initurl(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((RapidRuntimeViewParser)object).mUrl = value;
            ((RapidRuntimeViewParser)object).finish();
        }
    }

    private static class initmd5 implements IFunction {
        public initmd5(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((RapidRuntimeViewParser)object).mMd5 = value;
            ((RapidRuntimeViewParser)object).finish();
        }
    }

    private static class initsucceed implements IFunction {
        public initsucceed(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((RapidRuntimeViewParser)object).mSucceedTaskID = value.getString();
        }
    }

    private static class initfailed implements IFunction {
        public initfailed(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((RapidRuntimeViewParser)object).mFailedTaskID = value.getString();
        }
    }

    private static class initparams implements IFunction {
        public initparams(){}

        public void run(RapidParserObject object, Object view, Var value) {

            if( value.getString().compareTo("") == 0 ){
                return;
            }

            if( value.getString().compareToIgnoreCase("null") == 0 ){
                ((RapidRuntimeViewParser)object).mParamsMap = new ConcurrentHashMap<>();
                ((RapidRuntimeViewParser)object).finish();

                return;
            }

            ((RapidRuntimeViewParser)object).mParamsMap = RapidStringUtils.stringToMap(value.getString());

            translateMapData(object.getBinder(), object.mMapEnvironment, ((RapidRuntimeViewParser)object).mParamsMap);

            for( Map.Entry<String, String> entry : ((RapidRuntimeViewParser)object).mParamsMap.entrySet() ){
                ((RuntimeView)view).setParam(entry.getKey(), new Var(entry.getValue()));
            }

            ((RapidRuntimeViewParser)object).finish();
        }

        private void translateMapData(RapidDataBinder binder, Map<String, String> mapEnv, Map<String, String> map){
            DataExpressionsParser parser = new DataExpressionsParser();

            for( Map.Entry<String, String> entry : map.entrySet() ){
                String key = entry.getKey();
                String value = entry.getValue();

                if( parser.isDataExpression(value) ){
                    value = parser.get(binder, mapEnv, null, null, value).getString();
                }

                if( value == null ){
                    continue;
                }

                map.put(key, value);
            }
        }
    }
}
