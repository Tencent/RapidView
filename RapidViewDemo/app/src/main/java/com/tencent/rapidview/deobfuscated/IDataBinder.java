package com.tencent.rapidview.deobfuscated;

import android.os.Handler;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;


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
}
