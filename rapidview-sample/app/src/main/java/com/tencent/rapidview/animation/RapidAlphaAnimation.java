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
