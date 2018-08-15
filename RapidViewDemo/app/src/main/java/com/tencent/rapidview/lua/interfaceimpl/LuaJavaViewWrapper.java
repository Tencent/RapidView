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
package com.tencent.rapidview.lua.interfaceimpl;

import android.view.ViewGroup;

import com.tencent.rapidview.RapidLoader;
import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;

import com.tencent.rapidview.deobfuscated.IRapidNode;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.deobfuscated.IRapidViewGroup;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.framework.RapidObject;
import com.tencent.rapidview.framework.RapidRuntimeCachePool;
import com.tencent.rapidview.param.ParamsChooser;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.utils.RapidDataUtils;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.XLog;


import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class LuaJavaViewWrapper
 * @Desc 添加View到指定的位置
 *
 * @author arlozhang
 * @date 2017.02.20
 */
public class LuaJavaViewWrapper extends RapidLuaJavaObject{

    public LuaJavaViewWrapper(String rapidID, IRapidView rapidView){
        super(rapidID, rapidView);
    }

    public LuaValue loadView(String viewName, String params, Object data, IRapidActionListener listener){
        IRapidView view = null;
        LuaValue ret = null;
        Map<String, Var> map = null;

        if( data instanceof LuaTable ){
            map = RapidDataUtils.translateData((LuaTable) data);
        }

        if( data instanceof Map ){
            map = (Map<String, Var>)data;
        }

        if( map == null ){
            map = new ConcurrentHashMap<String, Var>();
        }


        try{
            view = _loadView(viewName, params, map, listener);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if( view != null ){
            ret = CoerceJavaToLua.coerce(view);
        }

        return ret;
    }


    public LuaValue loadView(String viewName, String params, Map<String, Var> data, IRapidActionListener listener){
        IRapidView view = null;
        LuaValue    ret  = null;

        try{
            view = _loadView(viewName, params, data, listener);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        if( view != null ){
            ret = CoerceJavaToLua.coerce(view);
        }

        return ret;
    }

    public LuaValue addView(String xmlName, String parentID, String above, RapidDataBinder binder, Object data, IRapidActionListener listener){
        LuaValue value = null;
        Map<String, Var> map = null;

        if( data instanceof LuaTable ){
            map = RapidDataUtils.translateData((LuaTable) data);
        }

        if( data instanceof Map ){
            map = (Map<String, Var>)data;
        }

        if( map == null ){
            map = new ConcurrentHashMap<String, Var>();
        }

        try{
            value = _addView(xmlName, parentID, above, binder, map, listener);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return value;
    }


    public LuaValue removeView(String id){
        IRapidView rmView = mRapidView.getParser().getChildView(id);
        IRapidView parent = null;

        if( rmView == null ){
            return null;
        }

        parent = rmView.getParser().getParentView();
        if( parent == null ){
            return null;
        }

        parent.getParser().mMapChild.remove(rmView.getParser().getID());
        parent.getParser().mArrayChild = removeArrayView(parent.getParser().mArrayChild, rmView);
        ((ViewGroup)parent.getView()).removeView(rmView.getView());

        return CoerceJavaToLua.coerce(rmView);
    }

    private IRapidView[] removeArrayView(IRapidView[] arrayView, IRapidView rmView){
        IRapidView[] arrayNewView;
        int index = 0;

        if( rmView == null ){
            return arrayView;
        }

        if( arrayView == null ){
            return new IRapidView[0];
        }
        else{
            arrayNewView = new IRapidView[arrayView.length - 1];
        }

        for( int i = 0; i < arrayNewView.length; i++ ){

            if( arrayView[i] == rmView ){
                index++;
            }

            arrayNewView[i] = arrayView[index];
            index++;
        }

        return arrayNewView;
    }


    private IRapidView _loadView(String view, String params, Map<String, Var> map, IRapidActionListener listener){
        IRapidView        loadView = null;
        RapidParserObject parser   = getParser();

        if( parser == null || RapidStringUtils.isEmpty(view) ){
            return null;
        }

        if( view.contains(".")){
            RapidObject object = RapidRuntimeCachePool.getInstance().get(mRapidID, view, parser.isLimitLevel());

            loadView = object.load(parser.getUIHandler(),
                    parser.getContext(),
                    ParamsChooser.getParamsClass(params),
                    map,
                    listener == null ? parser.getActionListener() : listener);

        }
        else{
            loadView = RapidLoader.load(view, parser.getUIHandler(), parser.getContext(), ParamsChooser.getParamsClass(params), map, listener == null ? parser.getActionListener() : listener);
        }

        return loadView;
    }

    private LuaValue _addView(String xmlName, String parentID, String above, RapidDataBinder binder, Map<String, Var> map, IRapidActionListener listener){
        IRapidView        addView      = null;
        int               indexOfAbove = -1;
        IRapidViewGroup   parentView   = null;
        IRapidView        aboveView    = null;
        ViewGroup         parent       = null;
        RapidObject       object       = null;
        RapidParserObject parser       = getParser();

        if( parser == null || RapidStringUtils.isEmpty(xmlName) || RapidStringUtils.isEmpty(parentID) ){
            return null;
        }

        if( mRapidView == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddView接口原有视图为空");
            return null;
        }

        if( !RapidStringUtils.isEmpty(parentID) ){
            IRapidView view = null;

            view = mRapidView.getParser().getChildView(parentID);
            if( view instanceof IRapidViewGroup ){
                parentView = (IRapidViewGroup) view;
            }
        }



        object = RapidRuntimeCachePool.getInstance().get(mRapidID, xmlName, parser.isLimitLevel());

        addView = object.load( parser.getUIHandler(),
                               parser.getContext(),
                               parentView == null ? parser.getParams().getClass() :
                               parentView.createParams(getParser().getContext()).getClass(),
                               new ConcurrentHashMap<String, Var>(),
                               listener == null ? parser.getActionListener() : listener );



        if( addView == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddView接口需要添加的视图加载失败：" + xmlName + "(photonID:" + mRapidID + ")");
            return null;
        }


        addView.getParser().getTaskCenter().notify(IRapidNode.HOOK_TYPE.enum_data_start, "");

        addView.getParser().getBinder().update(map);

        addView.getParser().getTaskCenter().notify(IRapidNode.HOOK_TYPE.enum_data_end, "");

        if( RapidStringUtils.isEmpty(parentID) && RapidStringUtils.isEmpty(above)  ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "未指定父视图（parent）或底视图(above)");
            return null;
        }

        if( !RapidStringUtils.isEmpty(above) ){
            IRapidViewGroup aboveParentView = null;


            aboveView = mRapidView.getParser().getChildView(above);

            if( aboveView != null ){
                aboveParentView = aboveView.getParser().getParentView();
                if( aboveParentView == null ){
                    XLog.d(RapidConfig.RAPID_ERROR_TAG, "above视图处于根节点，无法向根节点添加View");
                    return null;
                }

                if( parentView == null ){
                    parentView = aboveParentView;
                }
                else if( parentView.getParser().getID().compareTo(aboveParentView.getParser().getID()) != 0 ){
                    XLog.d(RapidConfig.RAPID_ERROR_TAG, "指定的parentView和aboveView的父视图不相同");
                    return null;
                }
            }
            else{
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "找不到aboveView：" + above);
            }
        }

        if ( parentView == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction获取光子父视图视图失败");
            return null;
        }

        parent = (ViewGroup) parentView.getView();

        if( parent == null ||
                !(parentView instanceof IRapidViewGroup) ||
                !(parentView.getView() instanceof ViewGroup) ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction父视图为空");
            return null;
        }

        if( aboveView != null ){
            if( aboveView.getView() == null ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "AboveView为空");
                return null;
            }

            indexOfAbove = parent.indexOfChild(aboveView.getView());
        }


        if( indexOfAbove == -1 || indexOfAbove >= parent.getChildCount() ){
            parent.addView(addView.getView(),
                    addView.getParser().getParams().getLayoutParams());
        }
        else{
            parent.addView(addView.getView(),
                    indexOfAbove,
                    addView.getParser().getParams().getLayoutParams());
        }

        addView.getParser().mBrotherMap = parentView.getParser().mMapChild;

        parentView.getParser().mArrayChild = addArrayView(parentView.getParser().mArrayChild, addView);

        if( parentView.getParser().mMapChild.get(addView.getParser().getID()) != null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction父视图ID冲突：" + addView.getParser().getID());
        }

        parentView.getParser().mMapChild.put(addView.getParser().getID(), addView);

        addView.getParser().setParentView(parentView);
        addView.getParser().setIndexInParent(parent.indexOfChild(addView.getView()));

        return CoerceJavaToLua.coerce(addView);
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
}
