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
package com.tencent.rapidview.framework;

import android.content.Context;
import android.os.Handler;
import android.os.Process;

import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.animation.RapidAnimationCenter;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidNode;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.lua.RapidLuaEnvironment;
import com.tencent.rapidview.lua.RapidLuaJavaBridge;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.task.RapidTaskCenter;
import com.tencent.rapidview.utils.RapidThreadPool;
import com.tencent.rapidview.utils.XLog;

import org.luaj.vm2.Globals;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Class RapidObject
 * @Desc RapidView界面对象核心类，每一个本类实例对应一个界面实例。
 *
 * @author arlozhang
 * @date 2015.09.22
 */
public class RapidObject extends RapidObjectImpl {

    private IRapidView mRapidView = null;

    protected CONCURRENT_LOAD_STATE mConcState = new CONCURRENT_LOAD_STATE();

    public interface IInitializeListener{
        void onFinish();
    }

    public interface ILoadListener{
        /**
         * 界面加载完成的通知
         *
         * @param rapidView 加载完成的界面，添加到父容器的view需要通过调用getView方法获取真实View,
         *                   参数则需要通过调用getParser().getParams().getLayoutParams()来获取。
         */
        void onFinish(IRapidView rapidView);
    }

    /**
     * 初始化一个视图，该方法可在非界面线程调用，但load前必须调用一次。
     * 界面加载的耗时操作都放到了该方法中，因此提前调用可以用于预加载。
     * 在listener中返回的view需在load后才可以使用。
     *
     * @param context      用于读取数据的上下文。
     * @param rapidID      当使用沙箱的方式加载时，需要传入rapidID
     * @param globals      是否使用外部的globals，如果为null，则重新创建一个
     * @param limitLevel   是否是受限级运行
     * @param xmlName      视图主XML的全名
     * @param initDataMap  初始化本身需要的数据池。目前除了无上限的include视图的情况，需要在初始化阶段确定需要
     *                     include哪些文件外，暂时没有其它用处，大部分情况可以传null。
     * @param listener     用于通知初始化完成。
     * @praam initDirectly 是否初始化完成
     */
    public void initialize(final Context context,
                           final String  rapidID,
                           final Globals globals,
                           final boolean limitLevel,
                           final String  xmlName,
                           final Map<String, Var> initDataMap,
                           final IInitializeListener listener,
                           final boolean initDirectly) {
        _initialize(new RapidDataBinder(initDataMap), context, rapidID, globals, limitLevel, xmlName, listener, initDirectly);
    }


    public void initialize(final RapidDataBinder binder,
                           final Context context,
                           final String  rapidID,
                           final Globals globals,
                           final boolean limitLevel,
                           final String  xmlName,
                           final IInitializeListener listener){
        _initialize(binder, context, rapidID, globals, limitLevel, xmlName, listener, false);
    }

    public boolean isInitialized(){
        return mConcState.mInitialized;
    }

    /**
     * 加载界面，可在initialize调用后但未执行完时调用。必须在界面线程调用。
     *
     * @param uiHandler      界面线程的Handler。
     * @param parent         父视图的context
     * @param objClazz       父容器所对应的LayoutParams类型，例如父容器为RelativeLayout,
     *                       则该参数传入RelativeLayoutParams.class。
     * @param dataMap        数据池，界面要展示的数据、依赖的信息需放到map中传入，如数据尚未抵达，
     *                       可能通过获取DataBinder，调用update的方式更新数据。
     * @param actionListener 在父容器是客户端写死的情况下，需要配置的父容器交互、功能，通过该回调调用。回调参数
     *                       由各自功能解释意义。
     */
    public IRapidView load(Handler uiHandler,
                           Context parent,
                           Class objClazz,
                           Map<String, Var> dataMap,
                           IRapidActionListener actionListener){

        synchronized (mConcState){
            if( !mConcState.mCalledInitialize ){
                return null;
            }

            if( mConcState.mCalledLoad ){
                return mRapidView;
            }

            mConcState.mCalledLoad = true;
        }

        while( true ){
            synchronized (mConcState){

                for( int i = 0; i < mConcState.mPreloadList.size(); i++ ){

                    mConcState.mPreloadList.get(i).preload(parent);
                }

                mConcState.mPreloadList.clear();

                if( mConcState.mInitialized ){
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "完成初始化，准备加载");
                    break;
                }

                mConcState.mWaited = true;

                try{
                    mConcState.wait();
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

        if( uiHandler == null ){
            return null;
        }

        if( dataMap == null ){
            dataMap = new ConcurrentHashMap<String, Var>();
        }

        return _load(uiHandler, parent, objClazz, dataMap, actionListener);
    }

    private void _initialize(final RapidDataBinder binder,
                             final Context context,
                             final String  rapidID,
                             final Globals globals,
                             final boolean limitLevel,
                             final String  xmlName,
                             final IInitializeListener listener,
                             final boolean initDirectly){

        synchronized (mConcState){

            if( mConcState.mCalledInitialize ){
                return;
            }

            mConcState.mCalledInitialize = true;
        }

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "开始初始化：" + (xmlName == null ? "" : xmlName));

        if( context == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "context为空：" + (xmlName == null ? "" : xmlName));
        }

        RapidThreadPool.get().execute(new Runnable() {

            @Override
            public void run() {

                if( !RapidPool.getInstance().isInitialize() ){
                    Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
                }
                else if( initDirectly ) {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
                }
                else{
                    Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
                }

                _initialize(binder, context, rapidID, globals, limitLevel, xmlName, listener);
            }
        });
    }

    private void _initialize(RapidDataBinder binder,
                             Context context,
                             String  rapidID,
                             Globals globals,
                             boolean limitLevel,
                             String  xmlName,
                             IInitializeListener listener){
        try{
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "获得初始化线程：" + xmlName);

            IRapidView rapidView;
            RapidDataBinder innerBinder = binder == null ? new RapidDataBinder(new ConcurrentHashMap<String, Var>()) : binder;
            RapidTaskCenter taskCenter = new RapidTaskCenter(null, limitLevel);
            RapidLuaEnvironment luaEnv = new RapidLuaEnvironment(globals, rapidID, limitLevel);

            rapidView = initXml(context,
                                rapidID,
                                limitLevel,
                                xmlName,
                                new ConcurrentHashMap<String, String>(),
                                luaEnv,
                                taskCenter,
                                new RapidAnimationCenter(context, taskCenter),
                                innerBinder,
                                mConcState);

            if( rapidView == null ){
                return;
            }

            innerBinder.addView(rapidView);

            mRapidView = rapidView;

            if( listener != null ){
                listener.onFinish();
            }
        }
        finally {
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "初始化完毕：" + xmlName);

            synchronized (mConcState){
                mConcState.mInitialized = true;

                if (mConcState.mWaited) {
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "通知等待初始化完毕");

                    mConcState.mWaited = false;

                    mConcState.notifyAll();
                }
            }
        }
    }


    private IRapidView _load(Handler uiHandler, Context parent, Class objClazz,
                             Map<String, Var> dataMap, IRapidActionListener listener){
        RapidDataBinder      binder;
        RapidLuaJavaBridge   javaInterface;
        ParamsObject         paramObject = null;
        Class[]              clzParams = new Class[]{Context.class};
        Object[]             objParams = new Object[]{parent};
        Constructor          ctr;
        RapidAnimationCenter animationCenter;

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "开始加载视图");

        try{
            if( mRapidView == null ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "初始化的视图为空，无法加载");
                return null;
            }

            if( mRapidView != null ){
                animationCenter = mRapidView.getParser().getAnimationCenter();

                if( animationCenter != null ){
                    animationCenter.setUiHandler(uiHandler);
                }
            }

            ctr = objClazz.getConstructor(clzParams);
            paramObject = (ParamsObject) ctr.newInstance(objParams);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        mRapidView = loadView(parent, mRapidView, paramObject, listener);
        if( mRapidView == null ){
            return null;
        }

        if( mRapidView == null ){
            return null;
        }

        binder = mRapidView.getParser().getBinder();
        javaInterface = mRapidView.getParser().getJavaInterface();

        javaInterface.setRapidView(mRapidView);

        if( binder != null ){
            for( Map.Entry<String, Var> entry : dataMap.entrySet() ){
                binder.update(entry.getKey(), entry.getValue());
            }

            binder.setUiHandler(uiHandler);

            mRapidView.getParser().notify(IRapidNode.HOOK_TYPE.enum_data_initialize, "");

            binder.setLoaded();
        }


        mRapidView.getParser().notify(IRapidNode.HOOK_TYPE.enum_load_finish, "");


        mRapidView.getParser().onLoadFinish();

        return mRapidView;
    }

    public boolean isEmpty(){
        return mRapidView == null;
    }
}
