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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.param.ParamsObject;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ViewStubParser
 * @Desc RapidView界面控件ViewStub解析器
 *
 * @author arlozhang
 * @date 2016.07.14
 */
public class ViewStubParser extends ViewParser{

    private boolean mIsReplaced = false;

    private static Map<String, IFunction> mViewStubMap = new ConcurrentHashMap<String, IFunction>();

    public IRapidView mRapidReplaceView = null;

    static{
        try{
            mViewStubMap.put("visibility", initvisibility.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ViewStubParser(){}

    @Override
    protected RapidParserObject.IFunction getAttributeFunction(String key, IRapidView view){
        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction function = mViewStubMap.get(key);
        if( function != null ){
            return function;
        }

        return super.getAttributeFunction(key, view);
    }

    public void setReplaceView(IRapidView rapidView){
        mRapidReplaceView = rapidView;
    }

    public IRapidView getReplaceView(){
        return mRapidReplaceView;
    }

    private static class initvisibility implements RapidParserObject.IFunction {
        public initvisibility(){}

        public void run(RapidParserObject object, Object view, Var value) {
            int index;
            ViewGroup parent;
            String str = value.getString();
            IRapidView viewChild = ((ViewStubParser)object).mRapidReplaceView;
            Context context = null;
            Class[] clzParams = new Class[]{Context.class};
            Object[] objParams = null;
            ParamsObject paramObj = null;
            Constructor ctr;


            if( str.compareToIgnoreCase("GONE") == 0 ){
                ((View)view).setVisibility(View.GONE);
                return;
            }

            if( !(object instanceof ViewStubParser) ||
                ((ViewStubParser) object).mIsReplaced ){
                return;
            }

            if( str.compareToIgnoreCase("VISIBLE") != 0 &&
                str.compareToIgnoreCase("INVISIBLE") != 0 ){
                return;
            }

            if( ((ViewStubParser)object).mRapidReplaceView == null ){
                return;
            }

            parent = (ViewGroup) ((View)view).getParent();
            if( parent == null ){
                return;
            }

            ((ViewStubParser) object).mIsReplaced = true;

            index = parent.indexOfChild((View)view);

            parent.removeViewInLayout((View)view);

            if( object.getParentView() != null ){

                object.getParentView().getParser().mMapChild.remove(object.getID());

                object.getParentView().getParser().mArrayChild =
                        removeIndexView(object.getParentView().getParser().mArrayChild,
                                        object.getIndexInParent());
            }

            context = parent.getContext();
            if( viewChild == null ){
                return;
            }

            try{
                objParams = new Object[]{context};

                ctr = object.getParams().getClass().getConstructor(clzParams);
                paramObj = (ParamsObject) ctr.newInstance(objParams);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            if( object.getParentView() != null ){
                viewChild.getParser().mBrotherMap = object.getParentView().getParser().mMapChild;
            }

            if( !viewChild.load(context, paramObj, object.getActionListener()) ){
                return;
            }

            viewChild.getParser().onLoadFinish();

            parent.addView(viewChild.getView(),
                           index,
                           viewChild.getParser().getParams().getLayoutParams());

            if( isRootNode(object) ){
                object.getTaskCenter().setRapidView(viewChild);
                object.getXmlLuaCenter().setRapidView(viewChild);

                object.mBinder.addView(viewChild);

                try {
                    object.mBinder.removeView(object.mRapidView);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                return;
            }

            object.getParentView().getParser().mArrayChild =
                    insertArrayView(object.getParentView().getParser().mArrayChild,
                                    object.getIndexInParent(), viewChild);

            object.getParentView().getParser().mMapChild.put(viewChild.getParser().getID(), viewChild);

            viewChild.getParser().setParentView(object.getParentView());
            viewChild.getParser().setIndexInParent(object.getIndexInParent());
        }

        private boolean isRootNode(RapidParserObject object){

            if( object.getParentView() == null || object.getIndexInParent() == -1  ){
                return true;
            }

            return false;
        }

        private IRapidView[] insertArrayView(IRapidView[] arrayView, int index, IRapidView insertView){
            IRapidView[] arrayNewView = null;

            if( arrayView == null ){
                arrayView = new IRapidView[0];

            }

            arrayNewView = new IRapidView[arrayView.length + 1];

            if( index > arrayView.length ){
                index = arrayView.length;
            }

            for( int i = 0; i < index; i++ ){
                arrayNewView[i] = arrayView[i];
            }

            arrayNewView[index] = insertView;

            for( int i = index + 1; i < arrayNewView.length; i++ ){
                arrayNewView[i] = arrayView[i - 1];
            }

            return arrayNewView;
        }

        private IRapidView[] removeIndexView(IRapidView[] arrayView, int index){
            if( arrayView == null || arrayView.length <= 1 ){
                return new IRapidView[0];
            }

            IRapidView[] arrayNewView = new IRapidView[arrayView.length - 1];

            if( index < 0 || index >= arrayView.length ){
                return arrayView;
            }

            for( int i = 0; i < index; i++ ){
                arrayNewView[i] = arrayView[i];
            }

            for( int i = index; i < arrayNewView.length; i++ ){
                arrayNewView[i] = arrayView[i + 1];
            }

            return arrayNewView;
        }
    }
}
