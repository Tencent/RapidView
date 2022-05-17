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
