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
