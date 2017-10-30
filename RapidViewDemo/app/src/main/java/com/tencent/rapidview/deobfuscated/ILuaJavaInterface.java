package com.tencent.rapidview.deobfuscated;

import com.tencent.rapidview.deobfuscated.luajavainterface.ILuaJavaBase64;
import com.tencent.rapidview.deobfuscated.luajavainterface.ILuaJavaCreate;
import com.tencent.rapidview.deobfuscated.luajavainterface.ILuaJavaDebug;
import com.tencent.rapidview.deobfuscated.luajavainterface.ILuaJavaInitialize;
import com.tencent.rapidview.deobfuscated.luajavainterface.ILuaJavaMd5;
import com.tencent.rapidview.deobfuscated.luajavainterface.ILuaJavaMedia;
import com.tencent.rapidview.deobfuscated.luajavainterface.ILuaJavaNetwork;
import com.tencent.rapidview.deobfuscated.luajavainterface.ILuaJavaShare;
import com.tencent.rapidview.deobfuscated.luajavainterface.ILuaJavaSystem;
import com.tencent.rapidview.deobfuscated.luajavainterface.ILuaJavaUI;

/**
 * @Class ILuaJavaInterface
 * @Desc Lua调用java的受限接口
 *
 * @author arlozhang
 * @date 2016.12.05
 */
public interface ILuaJavaInterface extends ILuaJavaInitialize,
                                           ILuaJavaCreate,
                                           ILuaJavaNetwork,
                                           ILuaJavaDebug,
                                           ILuaJavaBase64,
                                           ILuaJavaUI,
                                           ILuaJavaMedia,
                                           ILuaJavaShare,
                                           ILuaJavaMd5,
                                           ILuaJavaSystem{
}
