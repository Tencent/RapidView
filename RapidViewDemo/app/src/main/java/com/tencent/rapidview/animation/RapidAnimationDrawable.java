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
package com.tencent.rapidview.animation;

import android.graphics.drawable.AnimationDrawable;

/**
 * @Class RapidAnimationDrawable
 * @Desc 扩展AnimationDrawable能力
 *
 * @author arlozhang
 * @date 2016.08.19
 */
public class RapidAnimationDrawable extends AnimationDrawable{

    private RapidAnimationCenter mCenter = null;

    private Listener mListener = null;

    private long mStartOffset = 0;

    public interface Listener{

        void onAnimationStart();

        void onAnimationEnd();
    }

    public RapidAnimationDrawable(RapidAnimationCenter center){
        mCenter = center;
    }

    public void setListener(Listener listener){
        mListener = listener;
    }

    public void setStartOffset(long offset){
        mStartOffset = offset;
    }

    @Override
    public void start(){
        if( mCenter == null || mCenter.getUIHandler() == null ){
            super.start();
            return;
        }

        mCenter.getUIHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RapidAnimationDrawable.super.start();

                if( mListener != null ){
                    mListener.onAnimationStart();
                }

                mCenter.getUIHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if( mListener != null ){
                            mListener.onAnimationEnd();
                        }
                    }
                }, getTotalDuration());
            }
        }, mStartOffset);
    }

    @Override
    public boolean setVisible(final boolean visible, final boolean restart) {
        if( mCenter == null || mCenter.getUIHandler() == null || !restart ){
            return super.setVisible(visible, restart);
        }

        mCenter.getUIHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RapidAnimationDrawable.super.setVisible(visible, restart);

                if( mListener != null ){
                    mListener.onAnimationStart();
                }

                mCenter.getUIHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if( mListener != null ){
                            mListener.onAnimationEnd();
                        }
                    }
                }, getTotalDuration());
            }
        }, mStartOffset);

        return true;
    }

    private long getTotalDuration(){
        long total = 0;

        for( int i = 0; i < getNumberOfFrames(); i++ ){
            total += getDuration(i);
        }

        return total;
    }
}