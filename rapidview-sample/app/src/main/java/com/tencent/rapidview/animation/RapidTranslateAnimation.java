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

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.ViewUtils;

/**
 * @Class RapidTranslateAnimation
 * @Desc RapidView TranslateAnimation解析器
 *
 * @author arlozhang
 * @date 2016.08.17
 */
public class RapidTranslateAnimation extends RapidAnimation {

    protected long mScreenWidth = 0;
    protected long mScreenHeight = 0;

    public RapidTranslateAnimation(RapidAnimationCenter center){
        super(center);
        initScreenParams(center.getContext());
    }

    private void initScreenParams(Context context){
        if( context == null ){
            return;
        }

        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        mScreenWidth = display.getWidth();
        mScreenHeight = display.getHeight();
    }

    private int getReallyLength(String strLenth){
        if( RapidStringUtils.isEmpty(strLenth) ){
            return 0;
        }

        if( strLenth.length() >= 2 && strLenth.substring(strLenth.length() - 2).compareToIgnoreCase("%x") == 0 ){
            float percent = Float.parseFloat(strLenth.substring(0, strLenth.length() - 2)) / 100;
            return (int)(percent * mScreenWidth);
        }
        else if( strLenth.length() >= 2 && strLenth.substring(strLenth.length() - 2).compareToIgnoreCase("%y") == 0 ){
            float percent = Float.parseFloat(strLenth.substring(0, strLenth.length() - 2)) / 100;
            return (int)(percent * mScreenHeight);
        }

        return ViewUtils.dip2px(getAnimationCenter().getContext(), Float.parseFloat(strLenth));
    }

    @Override
    protected Animation createAnimation(){
        String fromXType = mMapAttribute.get("fromxtype");
        String fromXValue = mMapAttribute.get("fromxvalue");
        String toXType = mMapAttribute.get("toxtype");
        String toXValue = mMapAttribute.get("toxvalue");
        String fromYType = mMapAttribute.get("fromytype");
        String fromYValue = mMapAttribute.get("fromyvalue");
        String toYType = mMapAttribute.get("toytype");
        String toYValue = mMapAttribute.get("toyvalue");


        if( fromXType == null ){
            fromXType = "0";
        }

        if( fromXValue == null ){
            fromXValue = "0";
        }

        if( toXType == null ){
            toXType = "0";
        }

        if( toXValue == null ){
            toXValue = "0";
        }

        if( fromYType == null ){
            fromYType = "0";
        }

        if( fromYValue == null ){
            fromYValue = "0";
        }

        if( toYType == null ){
            toYType = "0";
        }

        if( toYValue == null ){
            toYValue = "0";
        }

        return new TranslateAnimation(Integer.parseInt(fromXType),
                                      getReallyLength(fromXValue),
                                      Integer.parseInt(toXType),
                                      getReallyLength(toXValue),
                                      Integer.parseInt(fromYType),
                                      getReallyLength(fromYValue),
                                      Integer.parseInt(toYType),
                                      getReallyLength(toYValue));
    }
}
