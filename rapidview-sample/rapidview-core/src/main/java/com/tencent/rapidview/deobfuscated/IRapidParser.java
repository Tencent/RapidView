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

import android.content.Context;
import android.os.Handler;

import com.tencent.rapidview.animation.RapidAnimationCenter;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.lua.RapidLuaJavaBridge;
import com.tencent.rapidview.lua.RapidXmlLuaCenter;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.task.RapidTaskCenter;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;

import java.util.List;

/**
 * @Class IRapidParser
 * @Desc RapidParser的对外暴露的接口
 *
 * @author arlozhang
 * @date 2016.12.05
 */
public interface IRapidParser {

    enum EVENT{
        enum_resume,
        enum_pause,
        enum_destroy,
        enum_parent_scroll,
        enum_parent_over_scrolled,
        enum_key_down,
        enum_onactivityresult,
        enum_key_back,
    }

    void notify(EVENT event, StringBuilder ret, Object... args);

    void notify(IRapidNode.HOOK_TYPE type, String value);

    void setParentView(IRapidViewGroup parentView);

    IRapidViewGroup getParentView();

    void setIndexInParent(int index);

    int getIndexInParent();

    Handler getUIHandler();

    void update(String attrKey, Object attrValue);

    ParamsObject getParams();

    String getID();

    IRapidView getChildView(String id);

    RapidDataBinder getBinder();

    IRapidActionListener getActionListener();

    RapidTaskCenter getTaskCenter();

    RapidXmlLuaCenter getXmlLuaCenter();

    void run(List<String> listKey);

    void run(String key);


    RapidAnimationCenter getAnimationCenter();

    LuaTable getEnv();

    Globals getGlobals();

    RapidLuaJavaBridge getJavaInterface();

    boolean isLimitLevel();

    Context getContext();

    String getRapidID();

    int getScreenHeight();

    int getScreenWidth();

    String getControlName();

    void setNotifyListener(IRapidNotifyListener listener);

    IRapidNotifyListener getNotifyListener();
}
