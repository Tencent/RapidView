package com.tencent.rapidview.deobfuscated;

import org.luaj.vm2.LuaTable;

/**
 * @Class IActionRunner
 * @Desc 外部执行Action的对外暴露接口
 *
 * @author arlozhang
 * @date 2016.12.05
 */
public interface IActionRunner {

    void run(String actionName, LuaTable tableAttribute, LuaTable tableEnv);
}
