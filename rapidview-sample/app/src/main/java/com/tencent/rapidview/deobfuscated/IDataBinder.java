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

import android.os.Handler;

import com.tencent.rapidview.data.Var;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Map;


/**
 * @Class IDataBinder
 * @Desc 对外暴露的DataBinder接口
 *
 * @author arlozhang
 * @date 2016.12.05
 */
public interface IDataBinder {

    Handler getUiHandler();

    void addView(IRapidView view);

    void removeView(IRapidView view);

    void update(String key, Object object);

    void update(String key, String value);

    void update(LuaTable table);

    LuaValue bind(String dataKey, String id, String attrKey);

    boolean unbind(String dataKey, String id, String attrKey);

    LuaValue get(String key);

    void removeData(String key);

    Map<String, Var> getDataMap();
}
