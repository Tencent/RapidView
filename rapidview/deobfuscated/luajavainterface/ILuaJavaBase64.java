package com.tencent.rapidview.deobfuscated.luajavainterface;

import com.tencent.rapidview.deobfuscated.IBytes;

/**
 * @Class ILuaJavaBase64
 * @Desc Lua调用java的base64接口
 *
 * @author arlozhang
 * @date 2017.03.03
 */
public interface ILuaJavaBase64 {

    /**
     * base64解压缩
     *
     * @param str   准备解压的数据
     * @param flags 参数，默认为default, default/url_safe
     * @return
     */
    IBytes decode(String str, String flags);

    /**
     * base64压缩
     *
     * @param bytes 需要压缩的数据
     * @param flags 参数，默认为default, default/no_padding/no_wrap/crlf/url_safe
     * @return
     */
    String encode(IBytes bytes, String flags);
}
