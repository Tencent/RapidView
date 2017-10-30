package com.tencent.rapidview.animation;

import android.view.animation.Animation;

import org.w3c.dom.Element;

public interface IAnimation {

    Animation getTween();

    RapidAnimationDrawable getFrame();

    String getID();

    void initialize(Element element);

    void load();
}
