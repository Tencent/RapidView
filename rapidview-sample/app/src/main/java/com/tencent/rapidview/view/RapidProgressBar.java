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
import android.widget.ProgressBar;

import com.tencent.rapidview.animation.RapidAnimationCenter;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidObjectImpl;
import com.tencent.rapidview.lua.RapidLuaEnvironment;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.parser.ProgressBarParser;
import com.tencent.rapidview.task.RapidTaskCenter;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class RapidProgressBar
 * @Desc 光子界面ProgressBar
 *
 * @author arlozhang
 * @date 2015.10.08
 */
public class RapidProgressBar extends RapidViewObject {

    private String mStyle;

    public RapidProgressBar(){}

    @Override
    protected RapidParserObject createParser(){
        return new ProgressBarParser();
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
            mStyle = element.getAttribute("style");
        }

        return super.initialize(context, rapidID, limitLevel, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);
    }

    @Override
    protected View createView(Context context){
        View viewProgressBar;

        if( mStyle == null || mStyle.compareToIgnoreCase("") == 0 ){
            viewProgressBar = new ProgressBar(context);
        }else if( mStyle.compareToIgnoreCase("horizontal") == 0 ){
            viewProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        }else if( mStyle.compareToIgnoreCase("small") == 0 ){
            viewProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
        }else if( mStyle.compareToIgnoreCase("large") == 0 ){
            viewProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        }else if( mStyle.compareToIgnoreCase("inverse") == 0 ){
            viewProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleInverse);
        }else if( mStyle.compareToIgnoreCase("smallinverse") == 0 ){
            viewProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmallInverse);
        }else if( mStyle.compareToIgnoreCase("largeinverse") == 0 ){
            viewProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLargeInverse);
        }else{
            viewProgressBar = new ProgressBar(context);
        }

        return viewProgressBar;
    }
}
