package com.tencent.rapidview.deobfuscated;

import org.luaj.vm2.LuaTable;

/**
 * @Class IFilterRunner
 * @Desc 外部执行Filter的对外暴露接口
 *
 * @author arlozhang
 * @date 2016.12.05
 */
public interface IFilterRunner {

    boolean run(String filterName, LuaTable tableAttribute, LuaTable tableEnv);
}
