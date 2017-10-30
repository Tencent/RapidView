package com.tencent.rapidview.deobfuscated;

import android.content.Context;
import android.os.Handler;

import com.tencent.rapidview.animation.RapidAnimationCenter;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.lua.RapidLuaJavaBridge;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.task.RapidTaskCenter;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;

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

    RapidAnimationCenter getAnimationCenter();

    LuaTable getEnv();

    Globals getGlobals();

    RapidLuaJavaBridge getJavaInterface();

    boolean isLimitLevel();

    Context getContext();

    String getRapidID();

    int getScreenHeight();

    int getScreenWidth();

    void setNotifyListener(IRapidNotifyListener listener);

    IRapidNotifyListener getNotifyListener();
}
