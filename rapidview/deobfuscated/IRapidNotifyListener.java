package com.tencent.rapidview.deobfuscated;

import android.view.KeyEvent;
import android.view.View;

/**
 * @Class IPhotonNotifyListener
 * @Desc RapidView控件通知的消息监听
 *
 * @author arlozhang
 * @date 2017.07.03
 */
public interface IRapidNotifyListener {

    void onResume();

    void onPause();

    void onDestroy();

    void onParentScroll(View view, int l, int t, int oldl, int oldt);

    void onKeyDown(StringBuilder intercept, int keyCode, KeyEvent event);

    void onParentOverScrolled(View view, int scrollX, int scrollY, boolean clampedX, boolean clampedY);
}
