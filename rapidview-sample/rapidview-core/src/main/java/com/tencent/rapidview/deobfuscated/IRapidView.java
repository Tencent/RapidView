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
import android.view.View;

import com.tencent.rapidview.animation.RapidAnimationCenter;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.framework.RapidObjectImpl;
import com.tencent.rapidview.lua.RapidLuaEnvironment;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.task.RapidTaskCenter;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class IRapidView
 * @Desc RapidView界面View接口
 *
 * @author arlozhang
 * @date 2015.09.22
 */
public interface IRapidView {

    int getID();

    String getTag();

    void setTag(String tag);

    View getView();

    RapidParserObject getParser();

    boolean preload(Context context);

    boolean load(Context context,
                 ParamsObject param,
                 IRapidActionListener listener);

    boolean initialize(Context  context,
                       String   rapidID,
                       boolean  limitLevel,
                       Element  element,
                       Map<String, String> envMap,
                       RapidLuaEnvironment luaEnv,
                       Map<String, IRapidView> brotherMap,
                       RapidTaskCenter taskCenter,
                       RapidAnimationCenter animationCenterCenter,
                       RapidDataBinder binder,
                       RapidObjectImpl.CONCURRENT_LOAD_STATE concState);
}
