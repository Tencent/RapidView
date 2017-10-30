package com.tencent.rapidview.deobfuscated.control;


import com.tencent.rapidview.deobfuscated.IRapidView;

import org.luaj.vm2.LuaTable;

/**
 * @Class IPhotonPagerAdapter
 * @Desc pageradapter不混淆接口
 *
 * @author arlozhang
 * @date 2017.07.07
 */
public interface IPhotonPagerAdapter {

    void addView(IRapidView view);

    void refresh(LuaTable tableView);

    IRapidView getChildView(String name);
}
