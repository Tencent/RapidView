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
package com.tencent.rapidview;

import android.content.Context;
import android.os.Handler;

import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.framework.RapidObject;
import com.tencent.rapidview.framework.RapidPool;
import com.tencent.rapidview.utils.RapidBenchMark;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.XLog;

import org.luaj.vm2.Globals;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidLoader
 * @Desc 加载界面
 * @author arlozhang
 * @date 2015.09.22
 */
public class RapidLoader {

    public interface IListener{
        /**
         * 加载实时View时，异步通知加载完毕，在界面线程调用，可直接处理。
         * 关于实时View：实时从后台下拉文件，并保存到RapidView沙箱中，实时加载，可选择是否准许获取受限等级RapidView权限。如果，
         * 是CP提供的文件，则一般禁止访问受限控件和能力，如果是内部的功能，则可访问受限能力。
         *
         * @param rapidView
         */
        void loadFinish(IRapidView rapidView);
    }

    /**
     * 从缓存池加载界面，必须在界面线程调用。
     *
     * @param viewName       视图的名称，将根据视图名查询主xml文件并加载
     * @param UIHandler      界面线程的Handler。
     * @param parent         父视图的context
     * @param objClazz       父容器所对应的LayoutParams类型，例如父容器为RelativeLayout,
     *                       则该参数传入RelativeLayoutParams.class。
     * @param dataMap        数据池，界面要展示的数据、依赖的信息需放到map中传入，如数据尚未抵达，
     *                       可能通过获取DataBinder，调用update的方式更新数据。
     * @param actionListener 在父容器是客户端写死的情况下，需要配置的父容器交互、功能，通过该回调调用。回调参数
     *                       由各自功能解释意义。
     *
     * @return               返回加载完成的界面对象
     */
    public static IRapidView load(String  viewName,
                                  Handler UIHandler,
                                  Context parent,
                                  Class   objClazz,
                                  Map<String, Var> dataMap,
                                  IRapidActionListener actionListener ){

        String nativeXml;
        Object callMark = new Object();
        IRapidView rapidView = null;

        nativeXml = RapidConfig.msMapViewNaitve.get(viewName);
        if( nativeXml == null ){
            nativeXml = "";
        }

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "准备加载RapidView视图，视图名：" + viewName + ",默认视图XML：" + nativeXml);
        RapidBenchMark.get().start(callMark.toString(), "开始加载" + viewName + "或" + nativeXml);

        RapidPool.getInstance().updateLock();
        RapidBenchMark.get().mark(callMark.toString(), "进入升级锁");

        try{
            RapidObject object = RapidPool.getInstance().get(viewName, nativeXml);
            if( object == null ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "RapidObject为空");
                return null;
            }

            RapidBenchMark.get().mark(callMark.toString(), "获得对象");

            rapidView = object.load(UIHandler, parent, objClazz, dataMap, actionListener);

            if( object.isEmpty() ){
                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "没有加载到视图，读取本地视图");
                object = RapidPool.getInstance().get("", nativeXml);
                rapidView = object.load(UIHandler, parent, objClazz, dataMap, actionListener);
            }

            if( rapidView != null ){
                rapidView.setTag(viewName);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            RapidPool.getInstance().updateUnlock();
        }

        RapidBenchMark.get().end(callMark.toString(), "加载完毕");

        return rapidView;
    }

    /**
     * 异步从RapidView沙箱里加载实时界面
     *
     * @param rapidID     rapidViewID，根据ID在沙盘中读取相应文件夹中的文件
     * @param xmlName     指定加载的XML名称，不指定的话默认为main.xml
     * @param limitLevel  是否是限制等级运行
     * @param UIHandler   界面线程的Handler
     * @param context     父视图的Context
     * @param objClazz    父容器所对应的LayoutParams类型，例如父容器为RelativeLayout,
     *                    则该参数传入RelativeLayoutParams.class。
     * @param globals     外部提供的lua Globals，如果外部不提供则重新创建一个
     * @param binder      当当前视图是添加到其他RapidView中时，可以将父视图的binder传入，如果是主视图则填入null
     * @param contextMap  RapidView对象上下文数据
     * @param listener    加载完成的监听器，会在界面线程完成调用
     *
     * @return            是否正常开始加载
     */
    public static boolean load(final String    rapidID,
                               final String    xmlName,
                               final boolean   limitLevel,
                               final Handler   UIHandler,
                               final Context   context,
                               final Class     objClazz,
                               final Globals   globals,
                               final RapidDataBinder binder,
                               final Map<String, Var> contextMap,
                               final IListener listener ){

        final RapidObject object = new RapidObject();
        final Object callMark = new Object();
        RapidDataBinder dataBinder = binder;

        if( RapidStringUtils.isEmpty(rapidID) ||
            UIHandler == null ||
            context   == null ||
            objClazz  == null ||
            listener  == null ){
            return false;
        }

        if( dataBinder == null ){
            dataBinder = new RapidDataBinder(new ConcurrentHashMap<String, Var>());
        }

        if( contextMap != null ){
            dataBinder.setContext(contextMap);
        }

        RapidBenchMark.get().start(callMark.toString(), "开始加载" + rapidID + (limitLevel ? "，限制权限执行" : "，root权限执行"));

        object.initialize(dataBinder,
                          context,
                          rapidID,
                          globals,
                          limitLevel,
                          RapidStringUtils.isEmpty(xmlName) ? "main.xml" : xmlName,
                          null);

                UIHandler.post(new Runnable() {
            @Override
            public void run() {
                IRapidView rapidView = object.load( UIHandler,
                        context,
                        objClazz,
                        new ConcurrentHashMap<String, Var>(),
                        null);

                RapidBenchMark.get().end(callMark.toString(), "加载完毕");

                listener.loadFinish(rapidView);
            }
        });

        return true;
    }
}
