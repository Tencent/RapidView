package com.tencent.rapidview.lua.interfaceimpl;

import android.view.ViewGroup;

import com.tencent.rapidview.RapidLoader;
import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;

import com.tencent.rapidview.deobfuscated.IRapidTask;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.deobfuscated.IRapidViewGroup;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.framework.RapidObject;
import com.tencent.rapidview.param.ParamsChooser;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.XLog;


import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
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

    public LuaJavaViewWrapper(String photonID, IRapidView rapidView){
        super(photonID, rapidView);
    }

    public LuaValue loadView(String viewName, String params, LuaTable data, IRapidActionListener listener){
        IRapidView view = null;
        LuaValue ret = null;

        if(RapidConfig.TEST_MODE){
            try{
                view = _loadView(viewName, params, data, listener);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            view = _loadView(viewName, params, data, listener);
        }

        if( view != null ){
            ret = CoerceJavaToLua.coerce(view);
        }

        return ret;
    }

    public LuaValue addView(String viewName, String parentID, String above, RapidDataBinder binder, LuaTable data, IRapidActionListener listener){
        LuaValue value = null;

        if(RapidConfig.TEST_MODE){
            try{
                value = _addView(viewName, parentID, above, binder, data, listener);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            value = _addView(viewName, parentID, above, binder, data, listener);
        }

        return value;
    }

    private IRapidView _loadView(String view, String params, LuaTable data, IRapidActionListener listener){
        IRapidView         loadView = null;
        Map<String, Var>   map      = null;
        RapidParserObject parser   = getParser();

        if( parser == null || RapidStringUtils.isEmpty(view) ){
            return null;
        }

        map = translateData(data);

        if( view.length() > 4 && view.substring(view.length() - 4, view.length()).compareToIgnoreCase(".xml") == 0 ){
            RapidObject object = new RapidObject();

            object.initialize(null, parser.getContext(), mRapidID, null, parser.isLimitLevel(), view, null);

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

    private LuaValue _addView(String viewName, String parentID, String above, RapidDataBinder binder, LuaTable data, IRapidActionListener listener){
        IRapidView         addView      = null;
        int                indexOfAbove = -1;
        IRapidViewGroup    parentView   = null;
        IRapidView         aboveView    = null;
        ViewGroup          parent       = null;
        RapidParserObject  parser       = getParser();
        Map<String, Var> map          = null;

        if( parser == null || RapidStringUtils.isEmpty(viewName) || RapidStringUtils.isEmpty(parentID) ){
            return null;
        }

        map = translateData(data);

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

        if( viewName.length() > 4 && viewName.substring(viewName.length() - 4, viewName.length()).compareToIgnoreCase(".xml") == 0 ){
            RapidObject object = new RapidObject();

            object.initialize(binder, parser.getContext(), mRapidID, null, parser.isLimitLevel(), viewName, null);

            addView = object.load( parser.getUIHandler(),
                                   parser.getContext(),
                                   parentView == null ? parser.getParams().getClass() :
                                   parentView.createParams(getParser().getContext()).getClass(),
                                   map,
                                   listener == null ? parser.getActionListener() : listener);

        }
        else{
            addView = RapidLoader.load(viewName, parser.getUIHandler(), parser.getContext(), parentView == null ? parser.getParams().getClass() :
                    parentView.createParams(getParser().getContext()).getClass(), map, listener == null ? parser.getActionListener() : listener);
        }

        if( addView == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddView接口需要添加的视图加载失败：" + viewName + "(photonID:" + mRapidID + ")");
            return null;
        }


        addView.getParser().getTaskCenter().notify(IRapidTask.HOOK_TYPE.enum_data_start, "");

        addView.getParser().getBinder().update(map);

        addView.getParser().getTaskCenter().notify(IRapidTask.HOOK_TYPE.enum_data_end, "");


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
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "AddViewAction获取RapidView父视图视图失败");
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

    private Map<String, Var> translateData(LuaTable data){
        LuaValue key = LuaValue.NIL;
        LuaValue value = LuaValue.NIL;
        Map<String, Var> map = new ConcurrentHashMap<String, Var>();

        if( data == null || !data.istable() ){
            return map;
        }

        while(true){
            Varargs argsItem = data.next(key);
            key = argsItem.arg1();

            if( key.isnil() ){
                break;
            }

            value = argsItem.arg(2);

            if( key.isstring()  ){
                map.put(key.toString(), new Var(value));
            }
        }

        return map;
    }
}
