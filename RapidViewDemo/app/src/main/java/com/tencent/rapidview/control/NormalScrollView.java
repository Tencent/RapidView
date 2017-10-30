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
package com.tencent.rapidview.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.tencent.rapidview.deobfuscated.IScrollView;

/**
 * @Class NormalScrollView
 * @Desc 通用scrollview
 *
 * @author arlozhang
 * @date 2016.09.28
 */
public class NormalScrollView extends ScrollView implements IScrollView {

    private IScrollViewListener mListener = null;

    private ISimpleOnGestureListener mGestureListener = null;

    private IInterruptTouchListener mInterruptListener = null;

    private IScrollVerticalEdgeListener mVerticalEdgeListener = null;

    private GestureDetector mDetector = new GestureDetector(new PhotonGestureDetector());

    private int mMaxScrolledT = 0;

    public NormalScrollView(Context context){
        super(context);
    }

    public NormalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getMaxScrolledT(){
        return  mMaxScrolledT;
    }

    @Override
    public void setInterruptTouchEvent(IInterruptTouchListener listener){
        mInterruptListener = listener;
    }

    @Override
    public GestureDetector getGestureDetector(){
        return mDetector;
    }

    @Override
    public void setSimpleOnGestureListener(ISimpleOnGestureListener listener){
        mGestureListener = listener;
    }

    @Override
    public void setScrollListener(IScrollViewListener listener){
        mListener = listener;
    }

    @Override
    public void setVerticalEdgeListener(IScrollVerticalEdgeListener listener){
        mVerticalEdgeListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);

        if( mMaxScrolledT < t ){
            mMaxScrolledT = t;
        }

        if( mVerticalEdgeListener != null ){
            if( getScrollY() == 0 ){
                mVerticalEdgeListener.onScrollToTop();
            }

            if( getScrollY() + getHeight() - getPaddingTop()-getPaddingBottom() == getChildAt(0).getHeight() ){
                mVerticalEdgeListener.onScrollToBottom();
            }
        }

        if( mListener == null ){
            return;
        }

        mListener.onScrollChanged(l,t, oldl, oldt);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

        if( mListener == null ){
            return;
        }

        mListener.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        boolean ret = false;

        if( mInterruptListener != null ){
            ret = mInterruptListener.onInterceptTouchEvent(ev);
        }

        if( ret ){
            return true;
        }

        return super.onInterceptTouchEvent(ev);
    }

    class PhotonGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if( mGestureListener == null ){
                return false;
            }

            return mGestureListener.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if( mGestureListener == null ){
                return false;
            }

            return mGestureListener.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
