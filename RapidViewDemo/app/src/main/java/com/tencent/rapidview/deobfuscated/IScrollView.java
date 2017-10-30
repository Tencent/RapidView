package com.tencent.rapidview.deobfuscated;


import android.view.GestureDetector;
import android.view.MotionEvent;

public interface IScrollView {

    interface ISimpleOnGestureListener {

        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }

    interface IScrollViewListener {

        void onScrollChanged(int l, int t, int oldl, int oldt);

        void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY);
    }

    interface IInterruptTouchListener{

        boolean onInterceptTouchEvent(MotionEvent ev);
    }

    interface IScrollVerticalEdgeListener{

        boolean onScrollToBottom();

        boolean onScrollToTop();
    }

    void setSimpleOnGestureListener(ISimpleOnGestureListener listener);

    void setInterruptTouchEvent(IInterruptTouchListener listener);

    void setScrollListener(IScrollViewListener listener);

    void setVerticalEdgeListener(IScrollVerticalEdgeListener listener);

    GestureDetector getGestureDetector();
}
