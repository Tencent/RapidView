package com.tencent.rapidview.deobfuscated.control;

import android.view.View;

import com.tencent.rapidview.control.RapidPagerAdapter;
import com.tencent.rapidview.deobfuscated.IRapidView;

/**
 * @Class IRapidViewPager
 * @Desc viewpager不混淆接口
 *
 * @author arlozhang
 * @date 2017.07.07
 */
public interface IRapidViewPager {

    RapidPagerAdapter getAdapter();

    View getCurrentView();

    IRapidView getCurrentPhotonView();

    void setTabTag(int index, String tag);

    String getTabTag(int index);

    void setViewPagerListener(IViewPagerListener listener);
}
