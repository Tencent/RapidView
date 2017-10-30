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