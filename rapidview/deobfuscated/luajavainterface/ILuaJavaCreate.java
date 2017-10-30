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

import org.luaj.vm2.LuaValue;

/**
 * @Class ILuaJavaCreate
 * @Desc Lua调用java的创建对象接口
 *
 * @author arlozhang
 * @date 2017.03.03
 */
public interface ILuaJavaCreate {

    /**
     * 创建受限的Java对象给lua
     *
     * @param   objName 对象名
     * @param   args0~5 对象构造参数
     *
     * @return  实际对象
     */
    LuaValue create(String objName);
    LuaValue create(String objName, Object args0);
    LuaValue create(String objName, Object args0, Object args1);
    LuaValue create(String objName, Object args0, Object args1, Object args2);
    LuaValue create(String objName, Object args0, Object args1, Object args2, Object args3);
    LuaValue create(String objName, Object args0, Object args1, Object args2, Object args3, Object args4);
    LuaValue create(String objName, Object args0, Object args1, Object args2, Object args3, Object args4, Object args5);
}

