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

import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.animation.RapidAnimationCenter;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidObjectImpl;
import com.tencent.rapidview.lua.RapidLuaEnvironment;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.task.RapidTaskCenter;
import com.tencent.rapidview.utils.RapidControlNameCreator;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class RapidViewObject
 * @Desc 控件基类
 *
 * @author arlozhang
 * @date 2016.04.12
 */
public abstract class RapidViewObject implements IRapidView {

    protected int mControlID = 0;

    protected RapidParserObject mParser = null;

    protected View mView = null;

    protected String mTag = "";

    public RapidViewObject(){}

    @Override
    public String getTag(){
        return mTag;
    }

    @Override
    public void setTag(String tag){
        mTag = tag;
    }


    @Override
    public int getID(){
        if( mView == null ){
            return 0;
        }

        if( mView.getId() != View.NO_ID ){
            return mView.getId();
        }

        if( mControlID == 0 ){
            mControlID = Integer.parseInt(RapidControlNameCreator.get());
            mView.setId(mControlID);
        }

        return mControlID;
    }


    @Override
    public View getView(){
        return mView;
    }

    @Override
    public boolean preload(Context context){
        mView = createView(context);

        getParser().setContext(context);

        return getParser().preloadView(this);
    }

    @Override
    public boolean load(Context context, ParamsObject param, IRapidActionListener listener){

        if( mView == null ){
            mView = createView(context);
        }


        mView = createView(context);

        getParser().setContext(context);

        return getParser().loadView(this, param, listener);
    }

    @Override
    public RapidParserObject getParser(){
        if( mParser == null ){
            mParser = createParser();
        }

        return mParser;
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
        return getParser().initialize(context, rapidID, limitLevel, this, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);
    }

    protected abstract RapidParserObject createParser();

    protected abstract View createView(Context context);
}
