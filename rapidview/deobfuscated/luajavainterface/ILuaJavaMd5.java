package com.tencent.rapidview.deobfuscated.luajavainterface;

import com.tencent.rapidview.deobfuscated.IBytes;

/**
 * @Class ILuaJavaMd5
 * @Desc Lua调用java的MD5接口
 *
 * @author arlozhang
 * @date 2017.03.03
 */
public interface ILuaJavaMd5 {

    /**
     * string 转md5 bytes
     * @param source 数据
     * @return md5 bytes
     */
    IBytes toMD5Bytes(String source);

    /**
     * bytes 转md5 bytes
     * @param source 数据
     * @return md5 bytes
     */
    IBytes toMD5Bytes(IBytes source);

    /**
     * string 转md5
     * @param source 数据
     * @return md5 string
     */
    String toMD5(String source);
}
