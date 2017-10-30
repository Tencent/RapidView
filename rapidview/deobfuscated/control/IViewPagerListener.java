package com.tencent.rapidview.deobfuscated.control;

/**
 * @Class IViewPagerListener
 * @Desc viewPager的消息通知
 *
 * @author arlozhang
 * @date 2017.07.03
 */
public interface IViewPagerListener {

    /**
     * 模拟系统的pause和resume在翻页的时候也发出pause和resume消息，同时和系统的pause和resume合在一起
     */
    void onPause(int pos, String tag);

    void onResume(int pos, String tag);

    /**
     * 提供一个首次反动到当前页面的标记，便于页面进行加载
     */
    void onPageSelected(int pos, String tag, boolean first);
}
