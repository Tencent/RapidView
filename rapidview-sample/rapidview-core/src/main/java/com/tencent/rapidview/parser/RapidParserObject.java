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
import android.os.Handler;
import android.provider.Settings;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.animation.RapidAnimationCenter;
import com.tencent.rapidview.data.DataExpressionsParser;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidNode;
import com.tencent.rapidview.deobfuscated.IRapidNotifyListener;
import com.tencent.rapidview.deobfuscated.IRapidParser;
import com.tencent.rapidview.deobfuscated.IRapidViewGroup;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.framework.RapidObjectImpl;
import com.tencent.rapidview.lua.RapidLuaCaller;
import com.tencent.rapidview.lua.RapidLuaEnvironment;
import com.tencent.rapidview.lua.RapidLuaJavaBridge;
import com.tencent.rapidview.lua.RapidXmlLuaCenter;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.task.RapidTaskCenter;
import com.tencent.rapidview.utils.DeviceUtils;
import com.tencent.rapidview.utils.RapidControlNameCreator;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.XLog;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidParserObject
 * @Desc RapidView界面控件解析器基本行为
 *
 * @author arlozhang
 * @date 2015.09.22
 */
public abstract class RapidParserObject implements IRapidParser {

    Handler mHandler = null;

    protected RapidObjectImpl.CONCURRENT_LOAD_STATE mConcState = null;

    protected String mRapidID = null;

    protected boolean mLimitLevel = false;

    protected boolean mIsPreload = false;

    public Map<String, IRapidView> mBrotherMap;

    public Map<String, IRapidView> mMapChild;

    public Map<String, Var> mMapOriginAttribute;

    public Map<String, String> mMapEnvironment;

    public RapidDataBinder mBinder;

    public RapidTaskCenter mTaskCenter;

    public RapidAnimationCenter mAnimationCenter;

    public ParamsObject mParams = null;

    public IRapidView mRapidView = null;

    public IRapidActionListener mActionListener = null;

    protected IRapidNotifyListener mNotifyListener = null;

    public Context mContext = null;

    public IRapidView[] mArrayChild = null;

    public IRapidViewGroup mParentView = null;

    public int mIndexInParent = -1;

    public RapidLuaEnvironment mLuaEnvironment = null;

    public String mID = null;

    public String mControlName = "";

    protected static int mScreenWidth = 0;

    protected static int mScreenHeight = 0;

    protected boolean mIsNotifyExposure = false;

    protected List<ATTRIBUTE_FUN_NODE> mInitFunNodeList = null;

    protected List<ATTRIBUTE_PARAMS_NODE> mInitParamsNodeList = null;

    private class ATTRIBUTE_PARAMS_NODE{
        public ATTRIBUTE_PARAMS_NODE(String key, Var value, boolean isExpression){
            this.key = key;
            this.value = value;
            this.isExpression = isExpression;
        }

        public String key;
        public Var value;
        public boolean isExpression;
    }

    private class ATTRIBUTE_FUN_NODE{
        public ATTRIBUTE_FUN_NODE(IFunction function, Var value, boolean isExpression){
            this.function = function;
            this.value = value;
            this.isExpression = isExpression;
        }

        public IFunction function = null;
        public Var value;
        public boolean isExpression;
    }

    RapidParserObject(){
        mMapChild = new ConcurrentHashMap<String, IRapidView>();
        mMapOriginAttribute = new ConcurrentHashMap<String, Var>();
        mInitFunNodeList = new ArrayList<ATTRIBUTE_FUN_NODE>();
        mInitParamsNodeList = new ArrayList<ATTRIBUTE_PARAMS_NODE>();
    }

    protected interface IFunction {
        void run(RapidParserObject object, Object view, Var value);
    }

    protected void loadAttribute(Map<String, Var> attrMap, IRapidView view){
        if( attrMap == null || view == null || view.getView() == null ){
            return;
        }

        for( Map.Entry<String, Var> entry : attrMap.entrySet() ){
            IFunction function = getAttributeFunction(entry.getKey().toLowerCase(), view);

            if( function == null ){
                continue;
            }

            try{
                function.run(this, view.getView(), entry.getValue());
            }
            catch (Exception e){
                if( entry.getValue() != null ){
                    XLog.d(RapidConfig.RAPID_ERROR_TAG, "解析参数异常：" + entry.getValue().getString());
                }

                e.printStackTrace();
            }
        }
    }

    protected void preloadAttributeNode(IRapidView view){

        for( int i = 0; i < mInitFunNodeList.size(); i++ ){
            ATTRIBUTE_FUN_NODE node = mInitFunNodeList.get(i);
            if( node == null || node.function == null || node.value == null || view.getView() == null ){
                continue;
            }

            try{
                if( node.value.isNull() || node.isExpression ){
                    continue;
                }

                node.function.run(this, view.getView(), node.value);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    protected void loadAttributeNode(IRapidView view){

        for( int i = 0; i < mInitFunNodeList.size(); i++ ){
            ATTRIBUTE_FUN_NODE node = mInitFunNodeList.get(i);
            if( node == null || node.function == null || node.value == null || view.getView() == null ){
                continue;
            }

            try{
                if( node.value.isNull() || (mIsPreload && !node.isExpression) ){
                    continue;
                }

                node.function.run(this, view.getView(), node.value);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    protected IFunction getAttributeFunction(String key, IRapidView view){
        return null;
    }

    public void setContext(Context context){
        mContext = context;
    }

    public void onLoadFinish(){
        loadFinish();

        if( mArrayChild == null ){
            return;
        }

        for( int i = 0 ; i < mArrayChild.length; i++ ){
            if( mArrayChild[i] == null ){
                continue;
            }

            mArrayChild[i].getParser().onLoadFinish();
        }
    }

    public void onUpdateFinish(){
        updateFinish();

        if( mArrayChild == null ){
            return;
        }

        for( int i = 0 ; i < mArrayChild.length; i++ ){
            if( mArrayChild[i] == null ){
                continue;
            }

            mArrayChild[i].getParser().onUpdateFinish();
        }
    }

    @Override
    public String getRapidID(){
        return mRapidID;
    }

    @Override
    public void notify(EVENT event, StringBuilder ret, Object... args){
        LuaValue luaRet = null;

        if( ret == null ){
            ret = new StringBuilder("");
        }

        if( mLuaEnvironment.getJavaBridge() != null ){
            mLuaEnvironment.getJavaBridge().notify(event, ret, args);
        }

        if( mTaskCenter != null ){
            switch (event){
                case enum_resume:
                    luaRet = com.tencent.rapidview.lua.RapidLuaCaller.getInstance().call(mLuaEnvironment.getGlobals(), "onResume");
                    break;
                case enum_pause:
                    luaRet = com.tencent.rapidview.lua.RapidLuaCaller.getInstance().call(mLuaEnvironment.getGlobals(), "onPause");
                    break;
                case enum_destroy:
                    luaRet = RapidLuaCaller.getInstance().call(mLuaEnvironment.getGlobals(), "onDestroy");
                    break;
                case enum_key_back:
                    luaRet = com.tencent.rapidview.lua.RapidLuaCaller.getInstance().call(mLuaEnvironment.getGlobals(), "onKeyBack");
                    break;
            }
        }

        if( luaRet != null ){
            if( luaRet.isstring() ){
                ret.append(luaRet.tojstring());
            }
            else if( luaRet.isboolean() ){
                ret.append(luaRet.toboolean() ? "true" : "false");
            }
        }

        onNotify(event, ret, args);
    }

    @Override
    public void setNotifyListener(IRapidNotifyListener listener){
        mNotifyListener = listener;
    }

    @Override
    public IRapidNotifyListener getNotifyListener(){
        return mNotifyListener;
    }

    @Override
    public void notify(IRapidNode.HOOK_TYPE type, String value){

        getTaskCenter().notify(type, value);
        getXmlLuaCenter().notify(type, value);
    }

    private void onNotify(EVENT event, StringBuilder ret, Object... args){
        switch (event){
            case enum_resume:
                if( mNotifyListener != null ){
                    mNotifyListener.onResume();
                }
                onResume();
                break;
            case enum_pause:
                if( mNotifyListener != null ){
                    mNotifyListener.onPause();
                }
                onPause();
                break;
            case enum_destroy:
                if( mNotifyListener != null ){
                    mNotifyListener.onDestroy();
                }
                onDestroy();
                destroy();
                break;
            case enum_parent_scroll:
                if( args.length >= 5 ){
                    if( mNotifyListener != null ){
                        mNotifyListener.onParentScroll((View)args[0], (int)args[1], (int)args[2], (int)args[3], (int)args[4]);
                    }
                    onParentScroll((View)args[0], (int)args[1], (int)args[2], (int)args[3], (int)args[4]);
                }
                break;

            case enum_parent_over_scrolled:
                if( args.length >= 5 ){
                    if( mNotifyListener != null ){
                        mNotifyListener.onParentOverScrolled((View)args[0], (int)args[1], (int)args[2], (boolean)args[3], (boolean)args[4]);
                    }

                    onParentOverScrolled((View)args[0], (int)args[1], (int)args[2], (boolean)args[3], (boolean)args[4]);
                }
                break;
            case enum_key_down:
                if( args.length >= 2 ){
                    if( mNotifyListener != null ){
                        mNotifyListener.onKeyDown(ret, (int)args[0], (KeyEvent)args[1]);
                    }
                    onKeyDown(ret, (int)args[0], (KeyEvent)args[1]);
                }
                break;
            default:
                break;
        }

        for( IRapidView view : mMapChild.values() ){
            if( view == null ){
                continue;
            }

            view.getParser().onNotify(event, ret, args);
        }
    }

    @Override
    public void setParentView(IRapidViewGroup parentView){
        mParentView = parentView;
    }

    @Override
    public IRapidViewGroup getParentView(){
        return mParentView;
    }

    @Override
    public void setIndexInParent(int index){
        mIndexInParent = index;
    }

    @Override
    public int getIndexInParent(){
        return mIndexInParent;
    }

    @Override
    public Context getContext(){
        return mContext;
    }

    @Override
    public String getControlName(){
        return mControlName;
    }

    @Override
    public Handler getUIHandler(){
        if( mHandler != null ){
            return mHandler;
        }

        mHandler = mBinder.getUiHandler();
        if( mHandler != null ){
            return mHandler;
        }

        mHandler = mRapidView.getView().getHandler();

        return mHandler;
    }

    @Override
    public void update(String attrKey, Object attrValue){
        if( attrValue instanceof String ){
            update(attrKey, new Var((String)attrValue));
            return;
        }

        if( attrValue instanceof Integer ){
            update(attrKey, new Var((int)attrValue));
            return;
        }

        if( attrValue instanceof Double ){
            update(attrKey, new Var((double)attrValue));
            return;
        }

        if( attrValue instanceof Boolean ){
            update(attrKey, new Var((boolean)attrValue));
            return;
        }

        update(attrKey, new Var(attrValue));
    }

    public void update(String attrKey, Var attrValue){
        if( mRapidView == null || attrKey == null || attrValue == null){
            return;
        }

        Map<String, Var> map = new ConcurrentHashMap<String, Var>();

        map.put(attrKey, attrValue);

        loadAttribute(map, mRapidView);
        fillLayoutParams(attrKey.toLowerCase(), attrValue, mBrotherMap);
    }

    @Override
    public ParamsObject getParams(){
        return mParams;
    }

    @Override
    public String getID(){
        if( mID == null ){
            mID = RapidControlNameCreator.get();
        }

        return mID;
    }

    public boolean initialize( Context context,
                               String rapidID,
                               boolean limitLevel,
                               IRapidView view,
                               Element element,
                               Map<String, String> envMap,
                               RapidLuaEnvironment luaEnv,
                               Map<String, IRapidView> brotherMap,
                               RapidTaskCenter taskCenter,
                               RapidAnimationCenter animationCenter,
                               RapidDataBinder binder,
                               RapidObjectImpl.CONCURRENT_LOAD_STATE concState){

        if( view == null || element == null ){
            return false;
        }

        mRapidID = rapidID;
        mLimitLevel = limitLevel;
        mBrotherMap = brotherMap;
        mContext = context;
        mRapidView = view;
        mMapEnvironment = envMap;
        mTaskCenter = taskCenter;
        mBinder = binder;
        mAnimationCenter = animationCenter;
        mLuaEnvironment = luaEnv;
        mConcState = concState;

        initAttribute(element);
        initScreenParams(context);

        synchronized (mConcState){
            mConcState.mPreloadList.add(mRapidView);
            mConcState.notifyAll();
        }


        if( view instanceof IRapidViewGroup){
            initChild((IRapidViewGroup)view, element);
        }

        return true;
    }

    public boolean preloadView(IRapidView view){
        if( view == null ){
            return false;
        }

        preloadAttributeNode(view);

        mIsPreload = true;

        return true;
    }

    public boolean loadView(IRapidView view,
                            ParamsObject param,
                            IRapidActionListener listener){
        if( view == null || param == null ){
            return false;
        }

        mActionListener = listener;
        mParams = param;

        loadAttributeNode(view);
        loadLayoutParams();

        if( view instanceof IRapidViewGroup){
            loadChild((IRapidViewGroup) view);
        }

        return true;
    }

    private void loadLayoutParams(){

        for( int i = 0; i < mInitParamsNodeList.size(); i++ ){
            ATTRIBUTE_PARAMS_NODE node = mInitParamsNodeList.get(i);

            fillLayoutParams(node.key, node.value, mBrotherMap);
        }
    }

    @Override
    public Globals getGlobals(){
        return mLuaEnvironment.getGlobals();
    }

    public RapidLuaEnvironment getLuaEnvironment(){
        return mLuaEnvironment;
    }

    @Override
    public RapidLuaJavaBridge getJavaInterface(){
        return mLuaEnvironment.getJavaBridge();
    }

    @Override
    public IRapidView getChildView(String id){
        IRapidView retView;

        if( id == null ){
            return null;
        }

        if( id.compareToIgnoreCase(getID()) == 0 ){
            return mRapidView;
        }

        retView = mMapChild.get(id);

        if( retView != null ){
            return retView;
        }

        for( IRapidView view : mMapChild.values() ){
            if( view == null ){
                continue;
            }

            retView = view.getParser().getChildView(id);

            if( retView != null ){
                return retView;
            }
        }

        return null;
    }

    @Override
    public RapidDataBinder getBinder(){
        return mBinder;
    }

    @Override
    public IRapidActionListener getActionListener(){
        return mActionListener;
    }

    @Override
    public RapidTaskCenter getTaskCenter(){
        return mTaskCenter;
    }

    @Override
    public RapidXmlLuaCenter getXmlLuaCenter(){
        return mLuaEnvironment.getXmlLuaCenter();
    }

    @Override
    public void run(List<String> listKey){
        getTaskCenter().run(listKey);
        getXmlLuaCenter().run(listKey);
    }

    @Override
    public void run(String key){
        getTaskCenter().run(key);
        getXmlLuaCenter().run(key);
    }

    @Override
    public RapidAnimationCenter getAnimationCenter(){
        return mAnimationCenter;
    }

    @Override
    public LuaTable getEnv(){
        LuaTable table = new LuaTable();

        for( Map.Entry<String, String> entry : mMapEnvironment.entrySet() ){
            table.set(LuaString.valueOf(entry.getKey()), LuaString.valueOf(entry.getValue()));
        }

        return table;
    }

    public Map<String, String> getMapEnv(){
        return mMapEnvironment;
    }

    @Override
    public boolean isLimitLevel(){
        return mLimitLevel;
    }

    @Override
    public int getScreenHeight(){
        return mScreenHeight;
    }

    @Override
    public int getScreenWidth(){
        return mScreenWidth;
    }

    protected void fillLayoutParams(String key, Var value, Map<String, IRapidView> brotherMap){
        if( mParams == null ){
            return;
        }

        try{
            mParams.fillLayoutParams(key, value, brotherMap);
        }
        catch ( Exception e){
            e.printStackTrace();
        }
    }

    private void initAttribute(Element element){
        if( element == null ){
            return;
        }

        DataExpressionsParser parser = new DataExpressionsParser();

        NamedNodeMap mapAttrs = element.getAttributes();

        mMapOriginAttribute.clear();
        mInitFunNodeList.clear();
        mInitParamsNodeList.clear();

        Node nodeID = mapAttrs.getNamedItem("id");
        if( nodeID != null ){
            mID = nodeID.getNodeValue();
            mID = parser.get(mBinder, mMapEnvironment, null, null, mID).getString();
        }

        for( int i = 0; i < mapAttrs.getLength(); i++){
            String key = mapAttrs.item(i).getNodeName().toLowerCase();
            String value = mapAttrs.item(i).getNodeValue();
            IFunction function = null;
            boolean isExpression = false;

            mMapOriginAttribute.put(key, new Var(value));

            if( parser.isDataExpression(value) ){
                value = parser.get(mBinder, mMapEnvironment, getID(), key, value).getString();
                if( RapidStringUtils.isEmpty(value) ){
                    value = null;
                }

                isExpression = true;
            }

            function = getAttributeFunction(key, mRapidView);
            if( function == null ){
                mInitParamsNodeList.add(new ATTRIBUTE_PARAMS_NODE(key, value == null ? new Var() : new Var(value), isExpression));
                continue;
            }

            mInitFunNodeList.add(new ATTRIBUTE_FUN_NODE(function, value == null ? new Var() : new Var(value), isExpression));
        }
    }

    protected synchronized void initChild(IRapidViewGroup viewGroup, Element element){
        InnerRapidObject center;
        NodeList listChild;

        if( element == null || viewGroup == null ){
            return;
        }

        center = new InnerRapidObject();

        try{
            listChild = element.getChildNodes();
            List<IRapidView> listChildView = new ArrayList<IRapidView>();

            for( int i = 0; i < listChild.getLength(); i++ ){
                Element childElement;
                IRapidView[] arrayChildView;
                String controlID;

                Node node = listChild.item(i);

                if( node.getNodeType() != Node.ELEMENT_NODE ){
                    continue;
                }

                childElement = (Element)node;

                arrayChildView = center.initialize(mContext,
                                                   childElement,
                                                   mMapEnvironment,
                                                   mLuaEnvironment,
                                                   mMapChild,
                                                   mTaskCenter,
                                                   mAnimationCenter,
                                                   mBinder,
                                                   mConcState);

                if( arrayChildView == null || arrayChildView.length == 0 ){
                    continue;
                }

                for( int j = 0; j < arrayChildView.length; j++ ) {
                    if( arrayChildView[j] == null ){
                        continue;
                    }

                    controlID = arrayChildView[j].getParser().getID();

                    mMapChild.put(controlID, arrayChildView[j]);
                    listChildView.add(arrayChildView[j]);

                    arrayChildView[j].getParser().setParentView(viewGroup);
                    arrayChildView[j].getParser().setIndexInParent(listChildView.size() - 1);
                }
            }

            mArrayChild = listViewToArray(listChildView);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private IRapidView[] listViewToArray(List<IRapidView> list){
        IRapidView[] array = new IRapidView[list.size()];

        for( int i = 0; i < list.size(); i++ ){
            array[i] = list.get(i);
        }

        return array;
    }

    protected synchronized void loadChild(IRapidViewGroup viewGroup){
        InnerRapidObject center;
        ViewGroup parent;
        ParamsObject[] arrayObj;

        parent = (ViewGroup)viewGroup.getView();

        center = new InnerRapidObject();

        if( mArrayChild == null ){
            return;
        }

        arrayObj = new ParamsObject[mArrayChild.length];
        for( int i = 0; i < arrayObj.length; i++ ){
            arrayObj[i] = viewGroup.createParams(mContext);
        }

        for( int i = 0; i < mArrayChild.length; i++ ){
            center.load(mContext, mArrayChild[i], arrayObj[i], mActionListener);
        }

        for( int j = 0; j < mArrayChild.length; j++ ) {
            if( mArrayChild[j].getView() == null ||
                mArrayChild[j].getParser().getParams().getLayoutParams() == null ){
                continue;
            }

            parent.addView(mArrayChild[j].getView(),
                    mArrayChild[j].getParser().getParams().getLayoutParams());
        }
    }

    protected void initScreenParams(Context context){
        if( mScreenHeight != 0 && mScreenWidth != 0 ) {
            return;
        }

        if( context == null ){
            return;
        }

        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        mScreenWidth = display.getWidth();
        mScreenHeight = display.getHeight() - getMeizuSmartBarHeight(context);
    }

    public static int getMeizuSmartBarHeight(Context context) {
        final boolean autoHideSmartBar = Settings.System.getInt(context.getContentResolver(), "mz_smartbar_auto_hide", 0) == 1;
        int height = 0;

        if (DeviceUtils.hasSmartBar()) {
            if (autoHideSmartBar) {
                return 0;
            }
            else {
                try {
                    Class c = Class.forName("com.android.internal.R$dimen");
                    Object obj = c.newInstance();

                    Field field = c.getField("mz_action_button_min_height");
                    height = Integer.parseInt(field.get(obj).toString());
                    return context.getResources().getDimensionPixelSize(height);
                } catch (Throwable e) {
                    try{
                        Class c = Class.forName("com.android.internal.R$dimen");
                        Object obj = c.newInstance();

                        Field field = c.getField("navigation_bar_height");
                        height = Integer.parseInt(field.get(obj).toString());
                        return context.getResources().getDimensionPixelSize(height);
                    }
                    catch (Throwable ex){
                    }
                }
            }
        } else {
            return 0;
        }

        return 0;
    }

    protected void onResume(){}

    protected void onPause(){}

    protected void onDestroy(){}

    protected void loadFinish(){}

    protected void updateFinish(){}

    protected void onParentOverScrolled(View view, int scrollX, int scrollY, boolean clampedX, boolean clampedY){}

    protected void onParentScroll(View view, int l, int t, int oldl, int oldt){
        int[] locationScroll = null;
        int[] locationVideo  = null;
        View  currentView    = null;

        if( !mIsNotifyExposure ){
            return;
        }

        if( mRapidView == null || mRapidView.getView() == null ){
            return;
        }

        currentView = mRapidView.getView();

        locationScroll = new int[2];
        locationVideo  = new int[2];

        view.getLocationInWindow(locationScroll);
        currentView.getLocationInWindow(locationVideo);

        if( locationVideo[1] + currentView.getHeight() > locationScroll[1] &&
            locationVideo[1]  < locationScroll[1] + view.getHeight() ){

            mIsNotifyExposure = false;
            notify(IRapidNode.HOOK_TYPE.enum_view_scroll_exposure, getID());
        }
    }

    /**
    * @param intercept 是否拦截消息，如果拦截则将该string填"true"，否则不处理
    *
    */
    protected void onKeyDown(StringBuilder intercept, int keyCode, KeyEvent event){}

    private void destroy(){
        mBinder.onDestroy();

        mBinder = null;
        mTaskCenter = null;
        mParams = null;
        mRapidView = null;

        for( Map.Entry<String, IRapidView> entry : mMapChild.entrySet() ){
            if( entry.getValue() != null ){
                entry.getValue().getParser().onDestroy();
            }
        }

        mMapChild.clear();
    }

    private class InnerRapidObject extends RapidObjectImpl {

        public IRapidView load(Context parent,
                               IRapidView rapidView,
                               ParamsObject paramsObject,
                               IRapidActionListener listener ){

            return loadView(parent, rapidView, paramsObject, listener);
        }

        public IRapidView[] initialize(Context context,
                                       Element element,
                                       Map<String, String> envMap,
                                       RapidLuaEnvironment luaEnv,
                                       Map<String, IRapidView> brotherMap,
                                       RapidTaskCenter taskCenter,
                                       RapidAnimationCenter animationCenter,
                                       RapidDataBinder binder,
                                       CONCURRENT_LOAD_STATE concState){

            return initElement(context, mRapidID, mLimitLevel, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);
        }
    }
}
