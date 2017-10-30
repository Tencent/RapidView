package com.tencent.rapidview.deobfuscated;

import org.luaj.vm2.LuaValue;

/**
 * @Class IVar
 * @Desc Var的非混淆接口
 *
 * @author arlozhang
 * @date 2017.09.18
 */
public interface IVar {

    boolean getBoolean();

    int getInt();

    long getLong();

    float getFloat();

    double getDouble();

    String getString();

    Object getObject();

    Object getArrayItem(int index);

    int getArrayLenth();

    LuaValue getLuaValue();
}
