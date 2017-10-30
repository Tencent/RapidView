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

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class AnimationObject
 * @Desc Animation基类
 *
 * @author arlozhang
 * @date 2016.08.16
 */
public abstract class AnimationObject implements IAnimation{

    protected Animation mAnimation = null;

    protected RapidAnimationDrawable mAnimationDrawable = null;

    protected String mID = "";

    protected String mTag = "";

    protected RapidAnimationCenter mAnimationCenter = null;

    protected Map<String, String> mMapAttribute = new ConcurrentHashMap<String, String>();

    protected List<FUN_NODE> mListNode = new ArrayList<FUN_NODE>();

    public AnimationObject(RapidAnimationCenter center){
        mAnimationCenter = center;
        mAnimationDrawable = new RapidAnimationDrawable(center);
    }

    private class FUN_NODE {
        public IFunction function;
        public String value;
    }

    protected interface IFunction {
        void run(AnimationObject object, Object animation, String value);
    }

    @Override
    public Animation getTween(){

        if( mAnimation == null ){
            mAnimation = createAnimation();
        }

        return mAnimation;
    }

    @Override
    public RapidAnimationDrawable getFrame(){
        return mAnimationDrawable;
    }

    @Override
    public void initialize(Element element){
        if( element == null ){
            return;
        }

        initTag(element);
        initAttribute(element);
        initFunctionList();
    }

    @Override
    public void load(){

        for( int i = 0; i < mListNode.size(); i++ ){
            FUN_NODE node = mListNode.get(i);
            if( node == null || node.function == null || node.value == null ){
                continue;
            }

            try{
                node.function.run(this, isFrameAnimation() ? getFrame() : getTween(), node.value);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        loadFinish();
    }

    @Override
    public String getID(){
        return mID;
    }

    public RapidAnimationCenter getAnimationCenter(){
        return mAnimationCenter;
    }

    private void initAttribute(Element element){
        NamedNodeMap mapAttrs;

        if( element == null ){
            return;
        }

        mapAttrs = element.getAttributes();
        mMapAttribute.clear();

        for( int i = 0; i < mapAttrs.getLength(); i++){
            String key = mapAttrs.item(i).getNodeName().toLowerCase();
            String value = mapAttrs.item(i).getNodeValue();

            mMapAttribute.put(key, value);
        }

        mID = mMapAttribute.get("id");
        if( mID == null ){
            mID = "";
        }
    }

    private void initTag(Element element){
        mTag = element.getTagName().toLowerCase();
    }

    private void initFunctionList(){

        for( Map.Entry<String, String> entry : mMapAttribute.entrySet() ){
            FUN_NODE node = null;
            IFunction function = null;

            function = getFunction(entry.getKey());
            if( function == null ){
                continue;
            }

            node = new FUN_NODE();
            node.function = function;
            node.value = entry.getValue();

            mListNode.add(node);
        }
    }

    private boolean isFrameAnimation(){
        return mTag.compareTo("animationlist") == 0;
    }

    protected abstract Animation createAnimation();

    protected abstract IFunction getFunction(String key);

    protected abstract void loadFinish();
}
