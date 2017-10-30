package com.tencent.rapidview.deobfuscated;

import android.content.Context;
import android.view.View;

import com.tencent.rapidview.animation.RapidAnimationCenter;
import com.tencent.rapidview.data.RapidDataBinder;
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
                       RapidDataBinder binder);
}
