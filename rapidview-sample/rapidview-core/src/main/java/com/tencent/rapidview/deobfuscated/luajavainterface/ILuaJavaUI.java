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
package com.tencent.rapidview.deobfuscated.luajavainterface;

import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.data.RapidDataBinder;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * @Class ILuaJavaUI
 * @Desc Lua调用java的UI操作接口
 *
 * @author arlozhang
 * @date 2017.03.03
 */
public interface ILuaJavaUI {

    /**
     * 添加一个视图到已有的XML中
     *
     * @param   xml       xml名
     * @param   parentID  父容器ID
     * @param   above     在父容器中的哪个控件上面，如果空，则默认插到最下面
     * @param   binder    如果需要继承父视图或某个视图的数据binder，需要把该binder传入，否则填空
     * @param   data      table形式的数据或Map<String, Var>
     * @param   listener  事件回调
     *
     * @return  返回新生成的界面
     */
    LuaValue addView(String xml, String parentID, String above, RapidDataBinder binder, Object data, IRapidActionListener listener);
    LuaValue addView(String xml, String parentID, String above, RapidDataBinder binder, Object data);



    /**
     * 加载一个视图[受限]
     *
     * @param name     view的名字或XML的名字，当为view的名字时，为受限接口
     * @param params   params类型(abslistvviewlayoutparams/absolutelayoutparams/framelayoutparams/linearlayoutparams/marginparams/relativelayoutparams/viewgroupparams/viewpagerparams)
     * @param data     table形式的数据或Map<String, Var>
     * @param listener 事件回调
     * @return
     */
    LuaValue loadView(String name, String params, Object data, IRapidActionListener listener);
    LuaValue loadView(String name, String params, Object data);

    /**
     * 删除一个子控件
     * @param id 子控件ID
     * @return
     */
    LuaValue removeView(String id);

    /**
     * 销毁当前activity
     */
    void finish();

    /**
     * 启动一个全新的activity的逻辑
     *
     * @param xml 加载的XML名字
     * @param params 创建activity的参数
     */
    void startActivity(String xml, LuaTable params);

    /**
     * delay在界面线程执行一段逻辑
     *
     * @param milliSec 要延迟的毫秒数
     * @param function 要执行的函数
     */
    void delayRun(long milliSec, LuaFunction function);

    /**
     * 界面线程执行一段逻辑
     *
     * @param function 要执行的函数
     */
    void postRun(LuaFunction function);

    /**
     * dip转px
     *
     * @param dip dip的值
     * @return
     */
    int dip2px(int dip);

    /**
     * px转dip
     *
     * @param px px的值
     * @return
     */
    int px2dip(int px);
}
