package com.tencent.rapidview.deobfuscated.luajavainterface;

/**
 * @Class ILuaJavaDebug
 * @Desc Lua调用java的调试接口
 *
 * @author arlozhang
 * @date 2017.03.03
 */
public interface ILuaJavaDebug {

    /**
     * 测试环境下打印Android Log的方法
     *
     * @param tag    log标签
     * @param value  log内容
     */
    void Log(String tag, String value);
}
