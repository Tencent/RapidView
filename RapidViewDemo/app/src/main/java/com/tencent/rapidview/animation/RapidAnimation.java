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

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.RapidStringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidAnimation
 * @Desc RapidView Animation解析器
 *
 * @author arlozhang
 * @date 2016.08.16
 */
public abstract class RapidAnimation extends AnimationObject{

    public RapidAnimation(RapidAnimationCenter center){
        super(center);
    }

    private static Map<String, IFunction> mAnimationMap = new ConcurrentHashMap<String, IFunction>();

    private String mAnimationEndTask = "";

    private String mAnimationRepeatTask = "";

    private String mAnimationStartTask = "";

    static{
        try{
            mAnimationMap.put("cancel", initcancel.class.newInstance());
            mAnimationMap.put("initialize", initinitialize.class.newInstance());
            mAnimationMap.put("reset", initreset.class.newInstance());
            mAnimationMap.put("restrictduration", initrestrictduration.class.newInstance());
            mAnimationMap.put("scalecurrentduration", initscalecurrentduration.class.newInstance());
            mAnimationMap.put("detachwallpaper", initdetachwallpaper.class.newInstance());
            mAnimationMap.put("duration", initduration.class.newInstance());
            mAnimationMap.put("fillafter", initfillafter.class.newInstance());
            mAnimationMap.put("fillbefore", initfillbefore.class.newInstance());
            mAnimationMap.put("fillenabled", initfillenabled.class.newInstance());
            mAnimationMap.put("repeatcount", initrepeatcount.class.newInstance());
            mAnimationMap.put("repeatmode", initrepeatmode.class.newInstance());
            mAnimationMap.put("startoffset", initstartoffset.class.newInstance());
            mAnimationMap.put("starttime", initstarttime.class.newInstance());
            mAnimationMap.put("zadjustment", initzadjustment.class.newInstance());
            mAnimationMap.put("start", initstart.class.newInstance());
            mAnimationMap.put("startnow", initstartnow.class.newInstance());
            mAnimationMap.put("interpolator", initinterpolator.class.newInstance());
            mAnimationMap.put("animationend", initanimationend.class.newInstance());
            mAnimationMap.put("animationrepeat", initanimationrepeat.class.newInstance());
            mAnimationMap.put("animationstart", initanimationstart.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void loadFinish(){
        if( mAnimation == null ){
            return;
        }

        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                List<String> list = null;

                if( RapidStringUtils.isEmpty(mAnimationStartTask) ){
                    return;
                }

                list = RapidStringUtils.stringToList(mAnimationStartTask);

                for( int i = 0; i < list.size(); i++ ){
                    IRapidView rapidView = null;

                    rapidView = getAnimationCenter().getTaskCenter().getRapidView();

                    if( rapidView != null ){
                        rapidView.getParser().run(list.get(i));
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                List<String> list = null;

                if( RapidStringUtils.isEmpty(mAnimationEndTask) ){
                    return;
                }

                list = RapidStringUtils.stringToList(mAnimationEndTask);

                for( int i = 0; i < list.size(); i++ ){
                    IRapidView rapidView = null;

                    rapidView = getAnimationCenter().getTaskCenter().getRapidView();

                    if( rapidView != null ){
                        rapidView.getParser().run(list.get(i));
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                List<String> list = null;

                if( RapidStringUtils.isEmpty(mAnimationRepeatTask) ){
                    return;
                }

                list = RapidStringUtils.stringToList(mAnimationRepeatTask);

                for( int i = 0; i < list.size(); i++ ){
                    IRapidView rapidView = null;

                    rapidView = getAnimationCenter().getTaskCenter().getRapidView();

                    if( rapidView != null ){
                        rapidView.getParser().run(list.get(i));
                    }
                }
            }
        });
    }

    @Override
    protected IFunction getFunction(String key){
        IFunction function;

        if( key == null ){
            return null;
        }

        function = mAnimationMap.get(key);

        return function;
    }

    private static class initcancel implements IFunction{
        public initcancel(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).cancel();
        }
    }

    private static class initinitialize implements IFunction{
        public initinitialize(){}

        public void run(AnimationObject object, Object animation, String value){
            String[] margin = value.split(",");

            if( margin.length < 4 ){
                return;
            }

            ((Animation)animation).initialize( Integer.parseInt(margin[0]),
                                               Integer.parseInt(margin[1]),
                                               Integer.parseInt(margin[2]),
                                               Integer.parseInt(margin[3]) );
        }
    }

    private static class initreset implements IFunction{
        public initreset(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).reset();
        }
    }

    private static class initrestrictduration implements IFunction{
        public initrestrictduration(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).restrictDuration(Long.parseLong(value));
        }
    }

    private static class initscalecurrentduration implements IFunction{
        public initscalecurrentduration(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).scaleCurrentDuration(Float.parseFloat(value));
        }
    }

    private static class initdetachwallpaper implements IFunction{
        public initdetachwallpaper(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).setDetachWallpaper(RapidStringUtils.stringToBoolean(value));
        }
    }

    private static class initduration implements IFunction{
        public initduration(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).setDuration(Long.parseLong(value));
        }
    }

    private static class initfillafter implements IFunction{
        public initfillafter(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).setFillAfter(RapidStringUtils.stringToBoolean(value));
        }
    }

    private static class initfillbefore implements IFunction{
        public initfillbefore(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).setFillBefore(RapidStringUtils.stringToBoolean(value));
        }
    }

    private static class initfillenabled implements IFunction{
        public initfillenabled(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).setFillEnabled(RapidStringUtils.stringToBoolean(value));
        }
    }

    private static class initrepeatcount implements IFunction{
        public initrepeatcount(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).setRepeatCount(Integer.parseInt(value));
        }
    }

    private static class initrepeatmode implements IFunction{
        public initrepeatmode(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).setRepeatMode(Integer.parseInt(value));
        }
    }

    private static class initstartoffset implements IFunction{
        public initstartoffset(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).setStartOffset(Long.parseLong(value));
        }
    }

    private static class initstarttime implements IFunction{
        public initstarttime(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).setStartTime(Long.parseLong(value));
        }
    }

    private static class initzadjustment implements IFunction{
        public initzadjustment(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).setZAdjustment(Integer.parseInt(value));
        }
    }

    private static class initstart implements IFunction{
        public initstart(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).start();
        }
    }

    private static class initstartnow implements IFunction{
        public initstartnow(){}

        public void run(AnimationObject object, Object animation, String value){
            ((Animation)animation).startNow();
        }
    }

    private static class initinterpolator implements IFunction{
        public initinterpolator(){}

        public void run(AnimationObject object, Object animation, String value){
            Interpolator interpolator = null;

            if( value.compareToIgnoreCase("linear") == 0 ){
                interpolator = new LinearInterpolator();
            }
            else if( value.compareToIgnoreCase("acceleratedecelerate") == 0 ){
                interpolator = new AccelerateDecelerateInterpolator();
            }
            else if( value.compareToIgnoreCase("accelerate") == 0 ){
                interpolator = new AccelerateInterpolator();
            }
            else if( value.compareToIgnoreCase("anticipate") == 0 ){
                interpolator = new AnticipateInterpolator();
            }
            else if( value.compareToIgnoreCase("anticipateovershoot") == 0 ){
                interpolator = new AnticipateOvershootInterpolator();
            }
            else if( value.compareToIgnoreCase("bounce") == 0 ){
                interpolator = new BounceInterpolator();
            }
//            else if( value.compareToIgnoreCase("cycle") == 0 ){
//                interpolator = new CycleInterpolator();
//            }
            else if( value.compareToIgnoreCase("decelerate") == 0 ){
                interpolator = new DecelerateInterpolator();
            }
            else if( value.compareToIgnoreCase("overshoot") == 0 ){
                interpolator = new OvershootInterpolator();
            }

            if( interpolator == null ){
                return;
            }

            ((Animation)animation).setInterpolator(interpolator);
        }
    }

    private static class initanimationend implements IFunction{
        public initanimationend(){}

        public void run(AnimationObject object, Object animation, String value){
            ((RapidAnimation)object).mAnimationEndTask = value;
        }
    }

    private static class initanimationrepeat implements IFunction{
        public initanimationrepeat(){}

        public void run(AnimationObject object, Object animation, String value){
            ((RapidAnimation)object).mAnimationRepeatTask = value;
        }
    }

    private static class initanimationstart implements IFunction{
        public initanimationstart(){}

        public void run(AnimationObject object, Object animation, String value){
            ((RapidAnimation)object).mAnimationStartTask = value;
        }
    }
}
