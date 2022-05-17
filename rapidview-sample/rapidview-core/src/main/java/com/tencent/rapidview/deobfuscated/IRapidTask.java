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


import org.luaj.vm2.LuaTable;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * @Class IRapidTask
 * @Desc TaskCenter对外暴露的接口
 *
 * @author arlozhang
 * @date 2016.12.05
 */
public interface IRapidTask {

    void setRapidView(IRapidView arrayView);

    void setEnvironment(Map<String, String> mapEnv);

    LuaTable getEnv();

    void add(Element element);

    void run(List<String> listKey);

    void run(String key);

    void notify(IRapidNode.HOOK_TYPE type, String value);

    IRapidView getRapidView();

    IActionRunner getActionRunner();

    IFilterRunner getFilterRunner();
}
