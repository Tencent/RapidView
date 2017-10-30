package com.tencent.rapidview.animation;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * @Class RapidAlphaAnimation
 * @Desc RapidAlphaAnimation解析器
 *
 * @author arlozhang
 * @date 2016.08.17
 */
public class RapidAlphaAnimation extends RapidAnimation {

    public RapidAlphaAnimation(RapidAnimationCenter center){
        super(center);
    }

    @Override
    protected Animation createAnimation(){
        String fromAlpha = mMapAttribute.get("fromalpha");
        String toAlpha = mMapAttribute.get("toalpha");

        if( fromAlpha == null ){
            fromAlpha = "0";
        }

        if( toAlpha == null ){
            toAlpha = "0";
        }

        return new AlphaAnimation(Integer.parseInt(fromAlpha), Integer.parseInt(toAlpha));
    }
}
