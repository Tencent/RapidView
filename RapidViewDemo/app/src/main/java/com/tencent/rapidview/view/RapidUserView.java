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
package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;

import com.tencent.rapidview.animation.RapidAnimationCenter;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.framework.RapidObjectImpl;
import com.tencent.rapidview.framework.UserViewConfig;
import com.tencent.rapidview.lua.RapidLuaEnvironment;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.ViewParser;
import com.tencent.rapidview.task.RapidTaskCenter;
import com.tencent.rapidview.utils.XLog;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class RapidUserView
 * @Desc 光子界面简单用户自定义view，高级自定义view可以通过disposal实现
 *
 * @author arlozhang
 * @date 2015.10.08
 */
public class RapidUserView extends RapidViewObject {

    private String mClass;

    public RapidUserView(){}

    @Override
    protected RapidParserObject createParser(){
        return new ViewParser();
    }

    @Override
    public boolean initialize( Context context,
                               String  rapidID,
                               boolean limitLevel,
                               Element element,
                               Map<String, String> envMap,
                               RapidLuaEnvironment luaEnv,
                               Map<String, IRapidView> brotherMap,
                               RapidTaskCenter taskCenter,
                               RapidAnimationCenter animationCenter,
                               RapidDataBinder binder,
                               RapidObjectImpl.CONCURRENT_LOAD_STATE concState){
        if( element != null ){
            mClass = element.getAttribute("class");
        }

        return super.initialize(context, rapidID, limitLevel, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);
    }

    @Override
    protected View createView(Context context){
        View userView;

        if( mClass == null ){
            return null;
        }

        if( mClass.compareToIgnoreCase("") == 0 ){
            return null;
        }

        try{
            UserViewConfig.IFunction function = RapidConfig.msMapUserView.get(mClass.toLowerCase());
            if( function == null ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "找不到UserView类：" + mClass.toLowerCase());
                return null;
            }

            userView = function.get(context);
            if( userView == null ){
                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "读取UserView实体对象失败：" + mClass.toLowerCase());
                return null;
            }

            userView.setId(getID());

        }catch (Exception e){
            e.printStackTrace();
            userView = null;
        }

        return userView;
    }
}
