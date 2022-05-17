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

import com.tencent.rapidview.deobfuscated.IBytes;
import com.tencent.rapidview.deobfuscated.utils.IRapidFeedsCacheQueue;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;

/**
 * @Class ILuaJavaNetwork
 * @Desc Lua调用java的网络接口
 *
 * @author arlozhang
 * @date 2017.03.03
 */
public interface ILuaJavaNetwork {

    /**
     * 发起https请求
     *
     * @param   url                请求的服务器地址
     * @param   data               请求的数据
     * @param   header             请求头信息，以key-value形式保存在Table中
     * @param   method             请求的方法：字符串填入OPTIONS/GET/HEAD/POST/PUT/DELETE/TRACE/CONNECT
     * @param   succeedListener    成功的回调，在界面线程，回调参数：string resopnse
     * @param   failedListener     失败的回调，在界面线程，回调参数：int    errorCode
     *
     * @return  是否成功发起请求
     */
    boolean request(String url, String data, LuaTable header, String method, LuaFunction succeedListener, LuaFunction failedListener);
    boolean request(String url, IBytes data, LuaTable header, String method, LuaFunction succeedListener, LuaFunction failedListener);
    boolean request(String url, LuaTable data, LuaTable header, String method, LuaFunction succeedListener, LuaFunction failedListener);

    /**
     * 发起一个测试的网络请求
     *
     * @param cmdID    请求id
     * @param data     请求数据
     * @param params   请求用到的参数：ui_thr
     * @param listener 回调函数(boolean succeed, List<String> viewList, List<Map<String, Var>>, dataList)
     * @return
     */
    boolean request(int cmdID, LuaTable data, LuaTable params, LuaFunction listener);

    boolean isNetworkActive();
    boolean isWap();
    boolean isWifi();
    boolean is2G();
    boolean is3G();
    boolean is4G();

    String urlDecode(String url);
    String urlEncode(String url);


    /**
     * 创建一个无感滑动队列
     *
     * @param cacheCount 缓存数量
     * @param reqStub    上次请求存根
     * @return
     */
    IRapidFeedsCacheQueue createFeedsCacheQueue(int cacheCount, Object reqStub);
}
