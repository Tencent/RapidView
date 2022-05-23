/***************************************************************************************************
 Tencent is pleased to support the open source community by making RapidView available.
 Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 Licensed under the MITLicense (the "License"); you may not use this file except in compliance
 withthe License. You mayobtain a copy of the License at

 http://opensource.org/licenses/MIT

 Unless required by applicable law or agreed to in writing, software distributed under the License is
 distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied. See the License for the specific language governing permissions and limitations under the
 License.
 ***************************************************************************************************/
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

        void onOverScrolled(int scrollX, int scrollY, Boolean clampedX, Boolean clampedY);
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
