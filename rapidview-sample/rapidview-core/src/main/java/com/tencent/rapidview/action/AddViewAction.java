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
package com.tencent.rapidview.action;

import android.view.ViewGroup;

import com.tencent.rapidview.RapidLoader;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidNode;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.deobfuscated.IRapidViewGroup;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.XLog;

import org.w3c.dom.Element;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class AddViewAction
 * @Desc 动态添加View的动作，动态添加的View和父View构成父子View的关系，可通过父View的子控件查找找到，但数据、
 *       binder以及task模块互相不通。
 *
 * @author arlozhang
 * @date 2016.07.27
 */
public class AddViewAction extends ActionObject{

    private Var mViewString;

    private Var mParent;

    private Var mDataString;

    private Var mInitString;

    private Var mAbove;

    public AddViewAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){
        Map<String, Var> mapData;
        Map<String, Var> mapInit;
        int              indexOfAbove = -1;
        IRapidViewGroup parentView   = null;
        IRapidView aboveView    = null;
        IRapidView addView      = null;
        ViewGroup        parent       = null;
        RapidDataBinder binder = getBinder();
        RapidParserObject parser = getParser();

        if( binder == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction获取Binder失败");
            return false;
        }

        if( parser == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction获取Parser失败");
            return false;
        }

        initAttribute();

        if( RapidStringUtils.isEmpty(mViewString) ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction视图名为空");
            return false;
        }

        mapData = getData(binder);
        mapInit = getInitMap(binder);

        if( mRapidView == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction原有视图获取失败");
            return false;
        }

        if( !RapidStringUtils.isEmpty(mParent) ){
            IRapidView view = null;

            view = mRapidView.getParser().getChildView(mParent.getString());

            if( view instanceof IRapidViewGroup){
                parentView = (IRapidViewGroup) view;
            }
        }

        addView = RapidLoader.load( mViewString.getString(),
                                     binder.getUiHandler(),
                                     getContext(),
                                     parentView == null ? parser.getParams().getClass() :
                                     parentView.createParams(getContext()).getClass(),
                                     mapInit == null ? new ConcurrentHashMap<String,Var>() : mapInit,
                                     parser.getActionListener() );

        if( addView == null || addView.getView() == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction需要添加的视图加载失败View：" + mViewString);
            return false;
        }

        addView.getParser().getTaskCenter().notify(IRapidNode.HOOK_TYPE.enum_data_start, "");

        addView.getParser().getBinder().update(mapData);

        addView.getParser().getTaskCenter().notify(IRapidNode.HOOK_TYPE.enum_data_end, "");

        if( RapidStringUtils.isEmpty(mParent) &&
            RapidStringUtils.isEmpty(mAbove)  ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "未指定父视图（parent）或底视图(above)");
            return false;
        }

        if( !RapidStringUtils.isEmpty(mAbove) ){
            IRapidViewGroup aboveParentView = null;

            aboveView = mRapidView.getParser().getChildView(mAbove.getString());

            if( aboveView != null ){
                aboveParentView = aboveView.getParser().getParentView();
                if( aboveParentView == null ){
                    XLog.d(RapidConfig.RAPID_ERROR_TAG, "above视图处于根节点，无法向根节点添加View");
                    return false;
                }

                if( parentView == null ){
                    parentView = aboveParentView;
                }
                else if( parentView.getParser().getID().compareTo(aboveParentView.getParser().getID()) != 0 ){
                    XLog.d(RapidConfig.RAPID_ERROR_TAG, "指定的parentView和aboveView的父视图不相同");
                    return false;
                }
            }
            else{
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "找不到aboveView：" + mAbove);
            }
        }

        if ( parentView == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction获取RapidView父视图视图失败");
            return false;
        }

        parent = (ViewGroup) parentView.getView();

        if( parent == null ||
            !(parentView instanceof IRapidViewGroup) ||
            !(parentView.getView() instanceof ViewGroup) ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction父视图为空");
            return false;
        }

        if( aboveView != null ){
            if( aboveView.getView() == null ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "AboveView为空");
                return false;
            }

            indexOfAbove = parent.indexOfChild(aboveView.getView());
        }

        if( indexOfAbove == -1 || indexOfAbove >= parent.getChildCount() ){
            parent.addView(addView.getView(), addView.getParser().getParams().getLayoutParams());
        }
        else{
            parent.addView(addView.getView(), indexOfAbove, addView.getParser().getParams().getLayoutParams());
        }

        addView.getParser().mBrotherMap = parentView.getParser().mMapChild;

        parentView.getParser().mArrayChild = addArrayView(parentView.getParser().mArrayChild, addView);

        if( parentView.getParser().mMapChild.get(addView.getParser().getID()) != null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction父视图ID冲突：" + addView.getParser().getID());
        }

        parentView.getParser().mMapChild.put(addView.getParser().getID(), addView);

        addView.getParser().setParentView(parentView);
        addView.getParser().setIndexInParent(parent.indexOfChild(addView.getView()));

        retObj = addView;

        return true;
    }

    private IRapidView[] addArrayView(IRapidView[] arrayView, IRapidView addView){
        IRapidView[] arrayNewView;
        int originArrayLenth = 0;

        if( addView == null ){
            return arrayView;
        }

        if( arrayView == null ){
            arrayNewView = new IRapidView[1];
            originArrayLenth = 0;
        }
        else{
            arrayNewView = new IRapidView[arrayView.length + 1];
            originArrayLenth = arrayView.length;
        }

        for( int i = 0; i < originArrayLenth; i++ ){
            arrayNewView[i] = arrayView[i];
        }

        arrayNewView[originArrayLenth] = addView;

        return arrayNewView;
    }


    private Map<String, Var> getData(RapidDataBinder binder){
        Map<String, Var> mapData = null;
        Object obj = null;

        if( binder == null ) {
            return new ConcurrentHashMap<String, Var>();
        }

        if( mDataString.getObject() instanceof Map ){
            obj = mDataString.getObject();
        }

        try{
            if( obj instanceof Map){
                mapData = new ConcurrentHashMap<String, Var>();

                for( Map.Entry<String, Var> map : ((Map<String, Var>) obj).entrySet() ){
                    mapData.put(map.getKey(), map.getValue());
                }
            }
        }
        catch (Exception e){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction获取数据池异常：" + mDataString);
            e.printStackTrace();
            mapData = null;
        }

        if( mapData == null ){
            if (binder != null) {
                mapData = binder.getDataMap();
            }
        }

        return mapData;
    }

    private Map<String, Var> getInitMap(RapidDataBinder binder){
        Map<String, Var> mapInit = new ConcurrentHashMap<String, Var>();
        Object obj = null;

        if( binder == null ) {
            return mapInit;
        }

        if( mInitString.getObject() instanceof Map ){
            obj = mInitString.getObject();
        }

        try{
            if( obj instanceof Map){
                mapInit = new ConcurrentHashMap<String, Var>();

                for( Map.Entry<String, Var> map : ((Map<String, Var>) obj).entrySet() ){
                    mapInit.put(map.getKey(), map.getValue());
                }
            }
        }
        catch (Exception e){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction获取初始化数据池异常：" + mInitString);
            e.printStackTrace();
        }

        return mapInit;
    }

    private void initAttribute(){
        mViewString   = mMapAttribute.get("view");
        mParent       = mMapAttribute.get("parent");
        mDataString   = mMapAttribute.get("data");
        mInitString   = mMapAttribute.get("init");
        mAbove        = mMapAttribute.get("above");

        if( mViewString == null ){
            mViewString = new Var("");
        }

        if( mParent == null ){
            mParent = new Var("");
        }

        if( mDataString == null ){
            mDataString = new Var("");
        }

        if( mInitString == null ){
            mInitString = new Var("");
        }

        if( mAbove == null ){
            mAbove = new Var("");
        }
    }
}
