package com.tencent.rapidview.deobfuscated;

/**
 * @Class IRapidActionListener
 * @Desc 界面索取方如果需要执行交互动作需要实现该接口，并根据传入Key，Value决定执行怎么样的动作
 *
 * @author arlozhang
 * @date 2016.03.23
 */
public interface IRapidActionListener {

    void notify(String key, String value);

}
