package com.tencent.rapidview.animation;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * @Class RapidScaleAnimation
 * @Desc RapidView ScaleAnimation解析器
 *
 * @author arlozhang
 * @date 2016.08.17
 */
public class RapidScaleAnimation extends RapidAnimation {

    public RapidScaleAnimation(RapidAnimationCenter center){
        super(center);
    }

    @Override
    protected Animation createAnimation(){
        String fromX = mMapAttribute.get("fromx");
        String toX = mMapAttribute.get("tox");
        String fromY = mMapAttribute.get("fromy");
        String toY = mMapAttribute.get("toy");
        String pivotXType = mMapAttribute.get("pivotxtype");
        String pivotXValue = mMapAttribute.get("pivotxvalue");
        String pivotYType = mMapAttribute.get("pivotytype");
        String pivotYValue = mMapAttribute.get("pivotyvalue");

        if( fromX == null ){
            fromX = "0";
        }

        if( toX == null ){
            toX = "0";
        }

        if( fromY == null ){
            fromY = "0";
        }

        if( toY == null ){
            toY = "0";
        }

        if( pivotXType == null ){
            pivotXType = "0";
        }

        if( pivotXValue == null ){
            pivotXValue = "0";
        }

        if( pivotYType == null ){
            pivotYType = "0";
        }

        if( pivotYValue == null ){
            pivotYValue = "0";
        }

        return new ScaleAnimation(Float.parseFloat(fromX),
                                  Float.parseFloat(toX),
                                  Float.parseFloat(fromY),
                                  Float.parseFloat(toY),
                                  Integer.parseInt(pivotXType),
                                  Float.parseFloat(pivotXValue),
                                  Integer.parseInt(pivotYType),
                                  Float.parseFloat(pivotYValue));
    }
}
