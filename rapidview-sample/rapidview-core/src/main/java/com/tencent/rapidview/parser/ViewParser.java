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
package com.tencent.rapidview.parser;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.StateSet;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.framework.RapidResource;
import com.tencent.rapidview.task.RapidTaskCenter;
import com.tencent.rapidview.utils.RapidImageLoader;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.ViewUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ViewParser
 * @Desc RapidView界面控件View解析器
 *
 * @author arlozhang
 * @date 2015.09.24
 */
public class ViewParser extends RapidParserObject {

    private static Map<String, IFunction> mViewClassMap = new ConcurrentHashMap<String, IFunction>();

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if( mTouchActionDown != null ){
                        List<String> list = RapidStringUtils.stringToList(mTouchActionDown);
                        run(list);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if( mTouchActionUp != null ){
                        List<String> list = RapidStringUtils.stringToList(mTouchActionUp);
                        run(list);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if( mTouchActionMove != null ){
                        List<String> list = RapidStringUtils.stringToList(mTouchActionMove);
                        run(list);
                    }
                    break;
            }

            return true;
        }
    };

    private String mTouchActionDown = null;

    private String mTouchActionMove = null;

    private String mTouchActionUp = null;

    static{
        try{
            mViewClassMap.put("background", initbackground.class.newInstance());
            mViewClassMap.put("backgroundresource", initbackgroundresource.class.newInstance());
            mViewClassMap.put("backgrounddrawable", initbackgrounddrawable.class.newInstance());
            mViewClassMap.put("backgroundcolor", initbackgroundcolor.class.newInstance());
            mViewClassMap.put("clickable", initclickable.class.newInstance());
            mViewClassMap.put("contentdescription", initcontentdescription.class.newInstance());
            mViewClassMap.put("contextclickable", initcontextclickable.class.newInstance());
            mViewClassMap.put("drawingcachebackgroundcolor", initdrawingcachebackgroundcolor.class.newInstance());
            mViewClassMap.put("drawingcacheenabled", initdrawingcacheenabled.class.newInstance());
            mViewClassMap.put("drawingcachequality", initdrawingcachequality.class.newInstance());
            mViewClassMap.put("duplicateparentstateenabled", initduplicateparentstateenabled.class.newInstance());
            mViewClassMap.put("duplicateparentstate", initduplicateparentstateenabled.class.newInstance());
            mViewClassMap.put("enabled", initenabled.class.newInstance());
            mViewClassMap.put("focusable", initfocusable.class.newInstance());
            mViewClassMap.put("focusableintouchmode", initfocusableintouchmode.class.newInstance());
            mViewClassMap.put("hapticfeedbackenabled", inithapticfeedbackenabled.class.newInstance());
            mViewClassMap.put("fadingedge", initfadingedge.class.newInstance());
            mViewClassMap.put("horizontalfadingedgeenabled", inithorizontalfadingedgeenabled.class.newInstance());
            mViewClassMap.put("horizontalscrollbarenabled", inithorizontalscrollbarenabled.class.newInstance());
            mViewClassMap.put("keepscreenon", initkeepscreenon.class.newInstance());
            mViewClassMap.put("longclickable", initlongclickable.class.newInstance());
            mViewClassMap.put("minimumheight", initminimumheight.class.newInstance());
            mViewClassMap.put("minimumwidth", initminimumwidth.class.newInstance());
            mViewClassMap.put("padding", initpadding.class.newInstance());
            mViewClassMap.put("saveenabled", initsaveenabled.class.newInstance());
            mViewClassMap.put("scrollcontainer", initscrollcontainer.class.newInstance());
            mViewClassMap.put("scrollbarfadingenabled", initscrollbarfadingenabled.class.newInstance());
            mViewClassMap.put("selected", initselected.class.newInstance());
            mViewClassMap.put("soundeffectsenabled", initsoundeffectsenabled.class.newInstance());
            mViewClassMap.put("verticalfadingedgeenabled", initverticalfadingedgeenabled.class.newInstance());
            mViewClassMap.put("verticalscrollbarenabled", initverticalscrollbarenabled.class.newInstance());
            mViewClassMap.put("visibility", initvisibility.class.newInstance());
            mViewClassMap.put("willnotcachedrawing", initwillnotcachedrawing.class.newInstance());
            mViewClassMap.put("willnotdraw", initwillnotdraw.class.newInstance());
            mViewClassMap.put("click", initclick.class.newInstance());
            mViewClassMap.put("touchdown", inittouchdown.class.newInstance());
            mViewClassMap.put("touchmove", inittouchmove.class.newInstance());
            mViewClassMap.put("touchup", inittouchup.class.newInstance());
            mViewClassMap.put("longclick", initlongclick.class.newInstance());
            mViewClassMap.put("keyevent", initkeyevent.class.newInstance());
            mViewClassMap.put("createcontextmenu", initcreatecontextmenu.class.newInstance());
            mViewClassMap.put("focuschange", initfocuschange.class.newInstance());
            mViewClassMap.put("touch", inittouch.class.newInstance());
            mViewClassMap.put("animation", initanimation.class.newInstance());
            mViewClassMap.put("startanimation", initstartanimation.class.newInstance());
            mViewClassMap.put("clearanimation", initclearanimation.class.newInstance());
            mViewClassMap.put("realid", initrealid.class.newInstance());
            mViewClassMap.put("scrollexposure", initscrollexposure.class.newInstance());
            mViewClassMap.put("statelistdrawable", initstatelistdrawable.class.newInstance());
            mViewClassMap.put("requestlayout", initrequestlayout.class.newInstance());
            mViewClassMap.put("invalidate", initinvalidate.class.newInstance());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ViewParser(){}

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mViewClassMap.get(key);

        return clazz;
    }


    private static class initstatelistdrawable implements IFunction {
        public initstatelistdrawable() {
        }

        public void run(RapidParserObject object, Object view, Var value) {
            StateListDrawable drawable = new StateListDrawable();
            Map<String, String> map = RapidStringUtils.stringToMap(value.getString());

            for (Map.Entry<String, String> entry : map.entrySet()) {

                if (entry.getKey().compareToIgnoreCase("enabled") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_enabled}, getDrawable(object, entry.getValue()));
                } else if (entry.getKey().compareToIgnoreCase("pressed") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_pressed}, getDrawable(object, entry.getValue()));
                } else if (entry.getKey().compareToIgnoreCase("selected") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_selected}, getDrawable(object, entry.getValue()));
                } else if (entry.getKey().compareToIgnoreCase("activated") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_activated}, getDrawable(object, entry.getValue()));
                } else if (entry.getKey().compareToIgnoreCase("active") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_active}, getDrawable(object, entry.getValue()));
                } else if (entry.getKey().compareToIgnoreCase("first") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_first}, getDrawable(object, entry.getValue()));
                } else if (entry.getKey().compareToIgnoreCase("focused") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_focused}, getDrawable(object, entry.getValue()));
                } else if (entry.getKey().compareToIgnoreCase("last") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_last}, getDrawable(object, entry.getValue()));
                } else if (entry.getKey().compareToIgnoreCase("middle") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_middle}, getDrawable(object, entry.getValue()));
                } else if (entry.getKey().compareToIgnoreCase("single") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_single}, getDrawable(object, entry.getValue()));
                } else if (entry.getKey().compareToIgnoreCase("window_focused") == 0) {
                    drawable.addState(new int[]{android.R.attr.state_window_focused}, getDrawable(object, entry.getValue()));
                } else if(entry.getKey().compareToIgnoreCase("wild_card") == 0){
                    drawable.addState(StateSet.WILD_CARD, getDrawable(object, entry.getValue()));
                }
            }

            ((View) view).setBackgroundDrawable(drawable);

        }

        private Drawable getDrawable(RapidParserObject object, String value) {
            if (value.contains(".") || value.contains("res@")) {
                Bitmap bmp = RapidImageLoader.get(object.getContext(), value, object.getRapidID(), object.isLimitLevel());
                if (bmp != null) {
                    return new BitmapDrawable(bmp);
                }
            }
            else{
                ColorDrawable drawable = new ColorDrawable(Color.parseColor("#" + value));

                return drawable;
            }


            return new ColorDrawable(Color.WHITE);
        }
    }

    private static class initbackground implements IFunction {
        public initbackground(){}

        public void run(RapidParserObject object, Object view, Var value){
            final View fView = (View)view;
            Drawable drawable = null;

            RapidImageLoader.get(((View) view).getContext(), value.getString(), object.getRapidID(), object.isLimitLevel(),
                    new RapidImageLoader.ICallback() {

                @Override
                public void finish(boolean succeed, String name, Bitmap bmp) {
                    Drawable drawable = null;

                    if( !succeed ){
                        return;
                    }

                    byte[] chunk = bmp.getNinePatchChunk();
                    if(chunk != null && NinePatch.isNinePatchChunk(chunk)){
                        drawable = new NinePatchDrawable(fView.getContext().getResources(), bmp, chunk, new Rect(), null);
                    }
                    else{
                        drawable = new BitmapDrawable(bmp);
                    }

                    fView.setBackgroundDrawable(drawable);
                }
            });
        }
    }

    private static class initbackgroundresource implements IFunction {
        public initbackgroundresource(){}

        public void run(RapidParserObject object, Object view, Var value){
            String str = value.getString();

            if( str.compareTo("") == 0 ){
                return;
            }

            if( str.length() > 4 && str.substring(0, 4).compareToIgnoreCase("res@") == 0 ){
                str = str.substring(4, str.length());
            }

            ((View)view).setBackgroundResource(RapidResource.mResourceMap.get(str));
        }
    }


    private static class initbackgrounddrawable implements IFunction {
        public initbackgrounddrawable(){}

        public void run(RapidParserObject object, Object view, Var value){
            String str = value.getString();

            if( str.length() > 4 && str.substring(0, 4).compareToIgnoreCase("res@") == 0 ){
                str = str.substring(4, str.length());
            }

            ((View)view).setBackgroundDrawable(((View)view).getResources().getDrawable(RapidResource.mResourceMap.get(str)));
        }
    }

    private static class initbackgroundcolor implements IFunction {
        public initbackgroundcolor(){}

        public void run(RapidParserObject object, Object view, Var value) {
            if(TextUtils.isEmpty(value.getString())){
                return;
            }

            ((View)view).setBackgroundColor(Color.parseColor("#" + value.getString()));
        }
    }

    private static class initclickable implements IFunction {
        public initclickable(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setClickable(value.getBoolean());
        }
    }

    private static class initcontentdescription implements IFunction {
        public initcontentdescription(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setContentDescription(value.getString());
        }
    }

    private static class initcontextclickable implements IFunction {
        public initcontextclickable(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setContentDescription(value.getString());
        }
    }

    private static class initdrawingcachebackgroundcolor implements IFunction {
        public initdrawingcachebackgroundcolor(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setDrawingCacheBackgroundColor(Color.parseColor("#" + value.getString()));
        }
    }

    private static class initdrawingcacheenabled implements IFunction {
        public initdrawingcacheenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setDrawingCacheEnabled(value.getBoolean());
        }
    }

    private static class initdrawingcachequality implements IFunction {
        public initdrawingcachequality(){}

        public void run(RapidParserObject object, Object view, Var value) {
            String str = value.getString();

            if( str.compareToIgnoreCase("DRAWING_CACHE_QUALITY_AUTO") == 0 ){
                ((View)view).setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
            }
            else if( str.compareToIgnoreCase("DRAWING_CACHE_QUALITY_HIGH") == 0 ){
                ((View)view).setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            }
            else if( str.compareToIgnoreCase("DRAWING_CACHE_QUALITY_LOW") == 0 ){
                ((View)view).setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            }
        }
    }

    private static class initduplicateparentstateenabled implements IFunction {
        public initduplicateparentstateenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setDuplicateParentStateEnabled(value.getBoolean());
        }
    }

    private static class initenabled implements IFunction {
        public initenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setEnabled(value.getBoolean());
        }
    }

    private static class initfocusable implements IFunction {
        public initfocusable(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setFocusable(value.getBoolean());
        }
    }

    private static class initfocusableintouchmode implements IFunction {
        public initfocusableintouchmode(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setFocusableInTouchMode(value.getBoolean());
        }
    }

    private static class inithapticfeedbackenabled implements IFunction {
        public inithapticfeedbackenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setHapticFeedbackEnabled(value.getBoolean());
        }
    }

    private static class initfadingedge implements IFunction {
        public initfadingedge(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setFadingEdgeLength(value.getInt());
        }
    }


    private static class inithorizontalfadingedgeenabled implements IFunction {
        public inithorizontalfadingedgeenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setHorizontalFadingEdgeEnabled(value.getBoolean());
        }
    }

    private static class inithorizontalscrollbarenabled implements IFunction {
        public inithorizontalscrollbarenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setHorizontalScrollBarEnabled(value.getBoolean());
        }
    }

    private static class initkeepscreenon implements IFunction {
        public initkeepscreenon(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setKeepScreenOn(value.getBoolean());
        }
    }

    private static class initlongclickable implements IFunction {
        public initlongclickable(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setLongClickable(value.getBoolean());
        }
    }

    private static class initminimumheight implements IFunction {
        public initminimumheight(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setMinimumHeight(ViewUtils.dip2px(((View) view).getContext(), value.getFloat()));
        }
    }

    private static class initminimumwidth implements IFunction {
        public initminimumwidth(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setMinimumWidth(ViewUtils.dip2px(((View)view).getContext(), value.getFloat()));
        }
    }

    private static class initpadding implements IFunction {
        public initpadding(){}

        public void run(RapidParserObject object, Object view, Var value) {
            String[] strPadding = value.getString().split(",");

            ((View)view).setPadding(ViewUtils.dip2px(((View)view).getContext(), Float.parseFloat(strPadding[0])),
                    ViewUtils.dip2px(((View)view).getContext(), Float.parseFloat(strPadding[1])),
                    ViewUtils.dip2px(((View)view).getContext(), Float.parseFloat(strPadding[2])),
                    ViewUtils.dip2px(((View)view).getContext(), Float.parseFloat(strPadding[3])));
        }
    }

    private static class initsaveenabled implements IFunction {
        public initsaveenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setSaveEnabled(value.getBoolean());
        }
    }

    private static class initscrollcontainer implements IFunction {
        public initscrollcontainer(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setScrollContainer(value.getBoolean());
        }
    }

    private static class initscrollbarfadingenabled implements IFunction {
        public initscrollbarfadingenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setScrollbarFadingEnabled(value.getBoolean());
        }
    }

    private static class initselected implements IFunction {
        public initselected(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setSelected(value.getBoolean());
        }
    }

    private static class initsoundeffectsenabled implements IFunction {
        public initsoundeffectsenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setSoundEffectsEnabled(value.getBoolean());
        }
    }

    private static class initverticalfadingedgeenabled implements IFunction {
        public initverticalfadingedgeenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setVerticalFadingEdgeEnabled(value.getBoolean());
        }
    }

    private static class initverticalscrollbarenabled implements IFunction {
        public initverticalscrollbarenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setVerticalScrollBarEnabled(value.getBoolean());
        }
    }

    private static class initvisibility implements IFunction {
        public initvisibility(){}

        public void run(RapidParserObject object, Object view, Var value) {
            String str = value.getString();

            if( str.compareToIgnoreCase("VISIBLE") == 0 ){
                ((View)view).setVisibility(View.VISIBLE);
            }
            else if( str.compareToIgnoreCase("INVISIBLE") == 0 ){
                ((View)view).setVisibility(View.INVISIBLE);
            }
            else if( str.compareToIgnoreCase("GONE") == 0 ){
                ((View)view).setVisibility(View.GONE);
            }
        }
    }

    private static class initwillnotcachedrawing implements IFunction {
        public initwillnotcachedrawing(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setWillNotCacheDrawing(value.getBoolean());
        }
    }

    private static class initwillnotdraw implements IFunction {
        public initwillnotdraw(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).setWillNotDraw(value.getBoolean());
        }
    }

    private static class initclick implements IFunction {
        public initclick(){}

        public void run(final RapidParserObject object, Object view, Var value) {
            final String fValue = value.getString();

            if( object.mRapidView == null ){
                return;
            }

            ((View)view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> list = RapidStringUtils.stringToList(fValue);

                    object.run(list);
                }
            });
        }
    }

    private static class inittouchdown implements IFunction {
        public inittouchdown(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ViewParser)object).mTouchActionDown = value.getString();
            ((View)view).setOnTouchListener(((ViewParser)object).mTouchListener);
        }
    }

    private static class inittouchmove implements IFunction {
        public inittouchmove(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ViewParser)object).mTouchActionMove = value.getString();
            ((View)view).setOnTouchListener(((ViewParser)object).mTouchListener);
        }
    }

    private static class inittouchup implements IFunction {
        public inittouchup(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ViewParser)object).mTouchActionUp = value.getString();
            ((View)view).setOnTouchListener(((ViewParser)object).mTouchListener);
        }
    }

    private static class initlongclick implements IFunction {
        public initlongclick(){}

        public void run(final RapidParserObject object, Object view, Var value) {
            final String fValue = value.getString();

            if( object.mRapidView == null ){
                return;
            }

            ((View)view).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    List<String> list = RapidStringUtils.stringToList(fValue);

                    object.run(list);

                    return true;
                }
            });
        }
    }

    private static class initkeyevent implements IFunction {
        public initkeyevent(){}

        public void run(final RapidParserObject object, Object view, Var value) {
            final String fValue = value.getString();

            if( object.mRapidView == null ){
                return;
            }

            ((View)view).setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    Map<String, String> map = RapidStringUtils.stringToMap(fValue);
                    String strID = map.get("id");
                    List<String> list;

                    if (strID == null) {
                        return false;
                    }

                    if (!isEventPass(map.get("event"), keyCode)) {
                        return false;
                    }

                    list = RapidStringUtils.stringToList(strID);

                    object.run(list);

                    return true;
                }

                private boolean isEventPass(String strEvent, int keyCode) {
                    if (strEvent == null) {
                        return true;
                    }

                    try {
                        int eventID = Integer.parseInt(strEvent);
                        if (eventID != keyCode) {
                            return false;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }

                    return true;
                }
            });
        }
    }

    private static class initcreatecontextmenu implements IFunction {
        public initcreatecontextmenu(){}

        public void run(final RapidParserObject object, Object view, Var value) {
            final String fValue = value.getString();


            if( object.mRapidView == null ){
                return;
            }

            ((View)view).setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    List<String> list = RapidStringUtils.stringToList(fValue);

                    object.run(list);
                }
            });
        }
    }

    private static class initfocuschange implements IFunction {
        public initfocuschange(){}

        public void run(final RapidParserObject object, Object view, Var value) {
            final String fValue = value.getString();

            if( object.mRapidView == null ){
                return;
            }

            ((View)view).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Map<String, String> map = RapidStringUtils.stringToMap(fValue);
                    String strID = map.get("id");
                    List<String> list;

                    if (strID == null) {
                        return;
                    }

                    if (!isFocusPass(map.get("focus"), hasFocus)) {
                        return;
                    }

                    list = RapidStringUtils.stringToList(strID);

                    object.run(list);
                }

                private boolean isFocusPass(String strFocus, boolean hasFocus) {
                    if (strFocus == null) {
                        return true;
                    }

                    try {
                        boolean focus = RapidStringUtils.stringToBoolean(strFocus);
                        if (focus != hasFocus) {
                            return false;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }

                    return true;
                }
            });
        }
    }

    private static class inittouch implements IFunction {
        public inittouch(){}

        public void run(final RapidParserObject object, Object view, Var value) {
            final String fValue = value.getString();

            if( object.mRapidView == null ){
                return;
            }

            final RapidTaskCenter taskCenter = object.mRapidView.getParser().getTaskCenter();
            if (taskCenter == null) {
                return;
            }

            ((View)view).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    List<String> list = RapidStringUtils.stringToList(fValue);

                    object.run(list);

                    return true;
                }
            });
        }
    }

    private static class initanimation implements IFunction {
        public initanimation(){}

        public void run(RapidParserObject object, Object view, Var value) {
            Animation animation = object.getAnimationCenter().getTween(value.getString());

            if( animation == null ){
                return;
            }

            ((View)view).setAnimation(animation);
        }
    }

    private static class initstartanimation implements IFunction {
        public initstartanimation(){}

        public void run(RapidParserObject object, Object view, Var value) {
            Animation animation = object.getAnimationCenter().getTween(value.getString());

            if( animation == null ){
                return;
            }

            ((View)view).startAnimation(animation);
        }
    }

    private static class initclearanimation implements IFunction {
        public initclearanimation(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).clearAnimation();
        }
    }


    private static class initrealid implements IFunction {
        public initrealid(){}

        public void run(RapidParserObject object, Object view, Var value) {

            if( RapidResource.mResourceMap.get(value.getString()) == null ){
                return;
            }

            ((View)view).setId(RapidResource.mResourceMap.get(value.getString()));
        }
    }

    private static class initrequestlayout implements IFunction {
        public initrequestlayout(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).requestLayout();
        }
    }


    private static class initinvalidate implements IFunction {
        public initinvalidate(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((View)view).invalidate();
        }
    }

    private static class initscrollexposure implements IFunction {
        public initscrollexposure(){}

        public void run(RapidParserObject object, Object view, Var value) {
            if( value.getBoolean() ){
                object.mIsNotifyExposure = true;
}
            else{
                object.mIsNotifyExposure = false;
            }
        }
    }
}



