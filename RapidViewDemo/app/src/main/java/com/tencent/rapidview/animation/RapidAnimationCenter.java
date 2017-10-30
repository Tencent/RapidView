package com.tencent.rapidview.animation;

import android.content.Context;
import android.os.Handler;
import android.view.animation.Animation;

import com.tencent.rapidview.task.RapidTaskCenter;

import org.w3c.dom.Element;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidAnimationCenter
 * @Desc RapidView 动画中心
 *
 * @author arlozhang
 * @date 2016.08.16
 */
public class RapidAnimationCenter {

    private Map<String, IAnimation> mMap = new ConcurrentHashMap<String, IAnimation>();

    private RapidTaskCenter mTaskCenter = null;

    private Context mContext = null;

    private Handler mUIHandler = null;

    public RapidAnimationCenter(Context context, RapidTaskCenter center){
        mTaskCenter = center;
        mContext = context;
    }

    public boolean isAnimation(Element element){
        return AnimationChooser.isExist(element.getTagName().toLowerCase());
    }

    public Context getContext(){
        return mContext;
    }

    public void setUiHandler(Handler uiHandler){
        mUIHandler = uiHandler;
    }

    public Handler getUIHandler(){
        return mUIHandler;
    }

    public void add(Element element){
        IAnimation animation = null;

        String tag;

        if( element == null ){
            return;
        }

        tag = element.getTagName().toLowerCase();

        animation = AnimationChooser.get(this, tag);
        if( animation == null ){
            return;
        }

        animation.initialize(element);
        animation.load();

        if( animation.getID().compareTo("") == 0 ){
            return;
        }

        mMap.put(animation.getID(), animation);
    }

    public Animation getTween(String id){
        IAnimation animation = mMap.get(id);

        if( animation == null ){
            return null;
        }

        return animation.getTween();
    }

    public RapidAnimationDrawable getFrame(String id){
        IAnimation animation = mMap.get(id);

        if( animation == null ){
            return null;
        }

        return animation.getFrame();
    }

    public RapidTaskCenter getTaskCenter(){
        return mTaskCenter;
    }
}
