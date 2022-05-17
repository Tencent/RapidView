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
import android.view.animation.AnimationSet;

import com.tencent.rapidview.utils.RapidStringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidAnimationSet
 * @Desc RapidView AnimationSet解析器
 *
 * @author arlozhang
 * @date 2016.08.16
 */
public class RapidAnimationSet extends RapidAnimation {

    public RapidAnimationSet(RapidAnimationCenter center){
        super(center);
    }

    private static Map<String, AnimationObject.IFunction> mAnimationSetMap = new ConcurrentHashMap<String, AnimationObject.IFunction>();

    static{
        try{
            mAnimationSetMap.put("addanimation", initaddanimation.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected IFunction getFunction(String key){
        IFunction function = super.getFunction(key);
        if( function != null ){
            return function;
        }

        if( key == null ){
            return null;
        }

        function = mAnimationSetMap.get(key);

        return function;
    }

    @Override
    protected Animation createAnimation(){
        String value = mMapAttribute.get("shareinterpolator");

        if( value == null ){
            value = "true";
        }

        return new AnimationSet(RapidStringUtils.stringToBoolean(value));
    }

    private static class initaddanimation implements IFunction{
        public initaddanimation(){}

        public void run(AnimationObject object, Object animation, String value){
            List<String> listAnimation = RapidStringUtils.stringToList(value);

            for( int i = 0; i < listAnimation.size(); i++ ){
                Animation ani = object.getAnimationCenter().getTween(listAnimation.get(i));

                if( ani == null ){
                    continue;
                }

                ((AnimationSet)animation).addAnimation(ani);
            }

        }
    }

}
