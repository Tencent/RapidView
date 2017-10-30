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
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;
import android.widget.ImageView;

import com.tencent.rapidview.animation.RapidAnimationDrawable;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.BackgroundWrapper;
import com.tencent.rapidview.utils.RapidImageLoader;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.ViewUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ImageViewParser
 * @Desc RapidView界面控件ImageView解析器
 *
 * @author arlozhang
 * @date 2015.10.08
 */
public class ImageViewParser extends ViewParser {

    private static Map<String, IFunction> mImageViewClassMap = new ConcurrentHashMap<String, IFunction>();

    private RapidAnimationDrawable mAnimationDrawable = null;

    static{
        try{
            mImageViewClassMap.put("background", initbackground.class.newInstance());
            mImageViewClassMap.put("image", initimage.class.newInstance());
            mImageViewClassMap.put("resizeimage", initresizeimage.class.newInstance());
            mImageViewClassMap.put("backgroundcolor", initbackgroundcolor.class.newInstance());
            mImageViewClassMap.put("scaletype", initscaletype.class.newInstance());
            mImageViewClassMap.put("frameanimation", initframeanimation.class.newInstance());
            mImageViewClassMap.put("startframeanimation", initstartframeanimation.class.newInstance());
            mImageViewClassMap.put("stopframeanimation", initstopframeanimation.class.newInstance());
            mImageViewClassMap.put("oneshotframeanimation", initoneshotframeanimation.class.newInstance());
            mImageViewClassMap.put("visibleframeanimation", initvisibleframeanimation.class.newInstance());
            mImageViewClassMap.put("startoffsetframeanimation", initstartoffsetframeanimation.class.newInstance());
            mImageViewClassMap.put("adjustviewbounds", initadjustviewbounds.class.newInstance());
            mImageViewClassMap.put("maxheight", initmaxheight.class.newInstance());
            mImageViewClassMap.put("minheight", initminheight.class.newInstance());
            mImageViewClassMap.put("maxwidth", initmaxwidth.class.newInstance());
            mImageViewClassMap.put("minwidth", initminwidth.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ImageViewParser(){}

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mImageViewClassMap.get(key);

        return clazz;
    }

    private static class initbackground implements RapidParserObject.IFunction {
        public initbackground(){}

        public void run(RapidParserObject object, Object view, Var value) {
            BackgroundWrapper.getInstance().setBackground((View)view, value.getString(), object.getRapidID(), object.isLimitLevel());
        }
    }

    private static class initbackgroundcolor implements RapidParserObject.IFunction {
        public initbackgroundcolor(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ImageView)view).setBackgroundColor(Color.parseColor("#" + value.getString()));
        }
    }

    private static class initframeanimation implements RapidParserObject.IFunction {
        public initframeanimation(){}

        public void run(RapidParserObject object, Object view, Var value) {
            RapidAnimationDrawable drawable = object.getAnimationCenter().getFrame(value.getString());
            if( drawable == null ){
                return;
            }

            ((ImageView)view).setBackgroundDrawable(drawable);
            ((ImageViewParser)object).mAnimationDrawable = drawable;
        }
    }

    private static class initstartframeanimation implements RapidParserObject.IFunction {
        public initstartframeanimation(){}

        public void run(RapidParserObject object, Object view, Var value) {
            RapidAnimationDrawable drawable = ((ImageViewParser)object).mAnimationDrawable;
            if( drawable == null ){
                return;
            }

            drawable.start();
        }
    }

    private static class initstopframeanimation implements RapidParserObject.IFunction {
        public initstopframeanimation(){}

        public void run(RapidParserObject object, Object view, Var value) {
            RapidAnimationDrawable drawable = ((ImageViewParser)object).mAnimationDrawable;
            if( drawable == null ){
                return;
            }

            drawable.stop();
        }
    }

    private static class initoneshotframeanimation implements RapidParserObject.IFunction {
        public initoneshotframeanimation(){}

        public void run(RapidParserObject object, Object view, Var value) {
            RapidAnimationDrawable drawable = ((ImageViewParser)object).mAnimationDrawable;
            if( drawable == null ){
                return;
            }

            drawable.setOneShot(value.getBoolean());
        }
    }

    private static class initvisibleframeanimation implements RapidParserObject.IFunction {
        public initvisibleframeanimation(){}

        public void run(RapidParserObject object, Object view, Var value) {
            boolean visible = true;
            boolean restart = false;
            AnimationDrawable drawable = ((ImageViewParser)object).mAnimationDrawable;
            if( drawable == null ){
                return;
            }

            List<String> listValue = RapidStringUtils.stringToList(value.getString());

            if( listValue.size() < 1 ){
                return;
            }

            visible = RapidStringUtils.stringToBoolean(listValue.get(0));
            if( listValue.size() > 1 ){
                restart = RapidStringUtils.stringToBoolean(listValue.get(1));
            }

            drawable.setVisible(visible, restart);
        }
    }

    private static class initstartoffsetframeanimation implements RapidParserObject.IFunction {
        public initstartoffsetframeanimation(){}

        public void run(RapidParserObject object, Object view, Var value) {
            RapidAnimationDrawable drawable = ((ImageViewParser)object).mAnimationDrawable;
            if( drawable == null ){
                return;
            }

            drawable.setStartOffset(value.getLong());
        }
    }

    private static class initadjustviewbounds implements RapidParserObject.IFunction {
        public initadjustviewbounds(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ImageView)view).setAdjustViewBounds(value.getBoolean());
        }
    }

    private static class initscaletype implements RapidParserObject.IFunction {
        public initscaletype(){}

        public void run(RapidParserObject object, Object view, Var value) {
            String str = value.getString();

            if( str.compareToIgnoreCase("matrix") == 0 ){
                ((ImageView)view).setScaleType(ImageView.ScaleType.MATRIX);
            }
            else if( str.compareToIgnoreCase("fix_xy") == 0 ){
                ((ImageView)view).setScaleType(ImageView.ScaleType.FIT_XY);
            }
            else if( str.compareToIgnoreCase("fit_start") == 0 ){
                ((ImageView)view).setScaleType(ImageView.ScaleType.FIT_START);
            }
            else if( str.compareToIgnoreCase("fit_center") == 0 ){
                ((ImageView)view).setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            else if( str.compareToIgnoreCase("fit_end") == 0 ){
                ((ImageView)view).setScaleType(ImageView.ScaleType.FIT_END);
            }
            else if( str.compareToIgnoreCase("center") == 0 ){
                ((ImageView)view).setScaleType(ImageView.ScaleType.CENTER);
            }
            else if( str.compareToIgnoreCase("center_crop") == 0 ){
                ((ImageView)view).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else if( str.compareToIgnoreCase("center_inside") == 0 ){
                ((ImageView)view).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }

        }
    }

    private static class initimage implements RapidParserObject.IFunction {
        public initimage(){}

        public void run(RapidParserObject object, Object view, Var value) {
            final ImageView imageView = (ImageView) view;

            RapidImageLoader.get(((ImageView) view).getContext(), value.getString(), object.getRapidID(), object.isLimitLevel(),
                    new RapidImageLoader.ICallback() {

                @Override
                public void finish(boolean succeed, String name, Bitmap bmp) {

                    byte[] chunk;

                    if (!succeed || bmp == null || bmp.isRecycled()) {
                        return;
                    }

                    chunk = bmp.getNinePatchChunk();

                    if ( chunk != null && NinePatch.isNinePatchChunk(chunk) ) {
                        Drawable drawable;

                        drawable = new NinePatchDrawable(imageView.getContext().getResources(),
                                bmp, chunk, new Rect(), null);

                        imageView.setImageDrawable(drawable);
                        return;
                    }

                    imageView.setImageBitmap(bmp);
                }
            });
        }
    }

    private static class initresizeimage implements RapidParserObject.IFunction {
        public initresizeimage(){}

        public void run(RapidParserObject object, Object view, Var value) {
            final ImageView imageView = (ImageView)view;
            final String[] arrayValue = value.getString().split(",");

            if( arrayValue.length < 3 ){
                return;
            }

            RapidImageLoader.get(((ImageView)view).getContext(),
                    arrayValue[2], object.getRapidID(), object.isLimitLevel(), new RapidImageLoader.ICallback(){

                @Override
                public void finish(boolean succeed, String name, Bitmap bmp) {
                    if( !succeed ){
                        return;
                    }

                    Matrix matrix = new Matrix();
                    matrix.postScale((float) ViewUtils.dip2px(imageView.getContext(),
                                     Integer.parseInt(arrayValue[0])) / (float) bmp.getWidth() - 0.05f,
                                     (float) ViewUtils.dip2px(imageView.getContext(),
                                     Integer.parseInt(arrayValue[1])) / (float) bmp.getHeight() - 0.05f);

                    Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                            bmp.getHeight(), matrix, true);

                    imageView.setImageBitmap(resizeBmp);
                }

            });
        }
    }

    private static class initmaxheight implements IFunction {
        public initmaxheight(){}

        public void run(RapidParserObject object, Object view, Var value) {
            int height = 0;
            String str = value.getString();

            if( str.length() >= 1 && str.substring(str.length() - 1).compareToIgnoreCase("%") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 1)) / 100;
                height = (int)(percent * object.mScreenHeight);
            }
            else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%x") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                height = (int)(percent * object.mScreenWidth);
            }
            else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%y") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                height = (int)(percent * object.mScreenHeight);
            }
            else{
                height = ViewUtils.dip2px(object.mContext, value.getFloat());
            }

            ((ImageView)view).setMaxHeight(height);
        }
    }

    private static class initminheight implements IFunction {
        public initminheight(){}

        public void run(RapidParserObject object, Object view, Var value) {
            int height = 0;
            String str = value.getString();

            if( str.length() >= 1 && str.substring(str.length() - 1).compareToIgnoreCase("%") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 1)) / 100;
                height = (int)(percent * object.mScreenHeight);
            }
            else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%x") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                height = (int)(percent * object.mScreenWidth);
            }
            else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%y") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                height = (int)(percent * object.mScreenHeight);
            }
            else{
                height = ViewUtils.dip2px(object.mContext, value.getFloat());
            }

            ((ImageView)view).setMinimumHeight(height);
        }
    }

    private static class initmaxwidth implements IFunction {
        public initmaxwidth(){}

        public void run(RapidParserObject object, Object view, Var value) {
            int width = 0;
            String str = value.getString();

            if( str.length() >= 1 && str.substring(str.length() - 1).compareToIgnoreCase("%") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 1)) / 100;
                width = (int)(percent * object.mScreenWidth);
            }
            else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%x") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                width = (int)(percent * object.mScreenWidth);
            }
            else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%y") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                width = (int)(percent * object.mScreenHeight);
            }
            else{
                width = ViewUtils.dip2px(object.mContext, value.getFloat());
            }

            ((ImageView)view).setMaxWidth(width);
        }
    }

    private static class initminwidth implements IFunction {
        public initminwidth(){}

        public void run(RapidParserObject object, Object view, Var value) {
            int width = 0;
            String str = value.getString();

            if( str.length() >= 1 && str.substring(str.length() - 1).compareToIgnoreCase("%") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 1)) / 100;
                width = (int)(percent * object.mScreenWidth);
            }
            else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%x") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                width = (int)(percent * object.mScreenWidth);
            }
            else if( str.length() >= 2 && str.substring(str.length() - 2).compareToIgnoreCase("%y") == 0 ){
                float percent = Float.parseFloat(str.substring(0, str.length() - 2)) / 100;
                width = (int)(percent * object.mScreenHeight);
            }
            else{
                width = ViewUtils.dip2px(object.mContext, value.getFloat());
            }

            ((ImageView)view).setMinimumWidth(width);
        }
    }
}
