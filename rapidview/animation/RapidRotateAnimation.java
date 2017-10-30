package com.tencent.rapidview.animation;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

/**
 * @Class RapidRotateAnimation
 * @Desc RapidView RotateAnimation解析器
 *
 * @author arlozhang
 * @date 2016.08.17
 */
public class RapidRotateAnimation extends RapidAnimation {

    public RapidRotateAnimation(RapidAnimationCenter center){
        super(center);
    }

    @Override
    protected Animation createAnimation(){
        String fromDegrees = mMapAttribute.get("fromdegrees");
        String toDegrees = mMapAttribute.get("todegrees");
        String pivotXType = mMapAttribute.get("pivotxtype");
        String pivotXValue = mMapAttribute.get("pivotxvalue");
        String pivotYType = mMapAttribute.get("pivotytype");
        String pivotYValue = mMapAttribute.get("pivotyvalue");

        if( fromDegrees == null ){
            fromDegrees = "0";
        }

        if( toDegrees == null ){
            toDegrees = "0";
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

        return new RotateAnimation(Float.parseFloat(fromDegrees),
                                   Float.parseFloat(toDegrees),
                                   Integer.parseInt(pivotXType),
                                   Float.parseFloat(pivotXValue),
                                   Integer.parseInt(pivotYType),
                                   Float.parseFloat(pivotYValue));
    }
}
