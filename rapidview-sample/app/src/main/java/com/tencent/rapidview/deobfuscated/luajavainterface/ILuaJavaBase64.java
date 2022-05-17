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
