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
