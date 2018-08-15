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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.Animation;

import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.RapidImageLoader;
import com.tencent.rapidview.utils.RapidStringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidFrameAnimation
 * @Desc RapidView FrameAnimation解析器
 *
 * @author arlozhang
 * @date 2016.08.18
 */
public class RapidFrameAnimation extends AnimationObject{

    public RapidFrameAnimation(RapidAnimationCenter center){
        super(center);
    }

    private String mStartTask = "";

    private String mEndTask = "";

    private static Map<String, IFunction> mFunctionMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mFunctionMap.put("addframe", initaddframe.class.newInstance());
            mFunctionMap.put("start", initstart.class.newInstance());
            mFunctionMap.put("stop", initstop.class.newInstance());
            mFunctionMap.put("oneshot", initoneshot.class.newInstance());
            mFunctionMap.put("visible", initvisible.class.newInstance());
            mFunctionMap.put("startoffset", initstartoffset.class.newInstance());
            mFunctionMap.put("animationstart", initanimationstart.class.newInstance());
            mFunctionMap.put("animationend", initanimationend.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected IFunction getFunction(String key){
        IFunction function;

        if( key == null ){
            return null;
        }

        function = mFunctionMap.get(key);

        return function;
    }

    @Override
    protected void loadFinish(){
        getFrame().setListener(new RapidAnimationDrawable.Listener() {
            @Override
            public void onAnimationStart() {
                List<String> list = null;

                if( RapidStringUtils.isEmpty(mStartTask) ){
                    return;
                }

                list = RapidStringUtils.stringToList(mStartTask);

                for( int i = 0; i < list.size(); i++ ){
                    IRapidView rapidView = null;

                    rapidView = getAnimationCenter().getTaskCenter().getRapidView();

                    if( rapidView != null ){
                        rapidView.getParser().run(list.get(i));
                    }
                }
            }

            @Override
            public void onAnimationEnd() {
                List<String> list = null;

                if( RapidStringUtils.isEmpty(mEndTask) ){
                    return;
                }

                list = RapidStringUtils.stringToList(mEndTask);

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

    private static class initaddframe implements IFunction{
        public initaddframe(){}

        public void run(AnimationObject object, Object animation, String value){
            List<List<String>> listOuter = null;

            if( RapidStringUtils.isEmpty(value) ){
                return;
            }

            listOuter = RapidStringUtils.stringToTwoLayerList(value);
            for( int i = 0; i < listOuter.size(); i++ ){
                String resource;
                String interval;
                Bitmap bmp;
                int   nInterval = 0;

                List<String> listInner = listOuter.get(i);

                if( listInner.size() < 2 ){
                    continue;
                }

                resource = listInner.get(0);
                interval = listInner.get(1);

                bmp = RapidImageLoader.get(object.getAnimationCenter().getContext(), resource);
                nInterval = Integer.parseInt(interval);

                if( bmp == null ){
                    continue;
                }

                ((RapidAnimationDrawable)animation).addFrame(new BitmapDrawable(bmp), nInterval);
            }
        }
    }

    private static class initstart implements IFunction {
        public initstart() {
        }

        public void run(AnimationObject object, Object animation, String value) {
            ((RapidAnimationDrawable)animation).start();
        }
    }

    private static class initstop implements IFunction {
        public initstop() {
        }

        public void run(AnimationObject object, Object animation, String value) {
            ((RapidAnimationDrawable)animation).stop();
        }
    }

    private static class initoneshot implements IFunction {
        public initoneshot() {
        }

        public void run(AnimationObject object, Object animation, String value) {
            ((RapidAnimationDrawable)animation).setOneShot(RapidStringUtils.stringToBoolean(value));
        }
    }

    private static class initvisible implements IFunction {
        public initvisible() {
        }

        public void run(AnimationObject object, Object animation, String value) {
            boolean visible = true;
            boolean restart = false;

            List<String> listValue = RapidStringUtils.stringToList(value);

            if( listValue.size() < 1 ){
                return;
            }

            visible = RapidStringUtils.stringToBoolean(listValue.get(0));
            if( listValue.size() > 1 ){
                restart = RapidStringUtils.stringToBoolean(listValue.get(1));
            }

            ((RapidAnimationDrawable)animation).setVisible(visible, restart);
        }
    }

    private static class initstartoffset implements IFunction {
        public initstartoffset() {
        }

        public void run(AnimationObject object, Object animation, String value) {
            ((RapidAnimationDrawable)animation).setStartOffset(Long.parseLong(value));
        }
    }

    private static class initanimationstart implements IFunction {
        public initanimationstart() {
        }

        public void run(AnimationObject object, Object animation, String value) {
            ((RapidFrameAnimation)object).mStartTask = value;
        }
    }

    private static class initanimationend implements IFunction {
        public initanimationend() {
        }

        public void run(AnimationObject object, Object animation, String value) {
            ((RapidFrameAnimation)object).mEndTask = value;
        }
    }

    @Override
    protected Animation createAnimation(){
        return null;
    }
}
