package com.tencent.rapidview.deobfuscated.luajavainterface;

import com.tencent.rapidview.deobfuscated.IBytes;

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


    boolean isNetworkActive();
    boolean isWap();
    boolean isWifi();
    boolean is2G();
    boolean is3G();
    boolean is4G();

    String urlDecode(String url);
    String urlEncode(String url);
}
