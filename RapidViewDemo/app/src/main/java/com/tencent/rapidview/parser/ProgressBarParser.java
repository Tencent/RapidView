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
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.RapidImageLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ProgressBarParser
 * @Desc RapidView界面ProgressBar解析器
 *
 * @author arlozhang
 * @date 2015.10.08
 */
public class ProgressBarParser extends ViewParser {

    private static Map<String, IFunction> mProgressBarClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mProgressBarClassMap.put("progressbackgroundcolor", initprogressbackgroundcolor.class.newInstance());
            mProgressBarClassMap.put("progresscolor", initprogresscolor.class.newInstance());
            mProgressBarClassMap.put("progressimage", initprogressimage.class.newInstance());
            mProgressBarClassMap.put("indeterminate", initindeterminate.class.newInstance());
            mProgressBarClassMap.put("progress", initprogress.class.newInstance());
            mProgressBarClassMap.put("max", initmax.class.newInstance());
            mProgressBarClassMap.put("secondaryprogress", initsecondaryprogress.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ProgressBarParser(){}

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mProgressBarClassMap.get(key);

        return clazz;
    }

    private static class initprogressbackgroundcolor implements IFunction {
        public initprogressbackgroundcolor(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ProgressBar)view).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + value.getString())));
        }
    }

    private static class initprogresscolor implements IFunction {
        public initprogresscolor(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ProgressBar)view).setProgressDrawable(new ClipDrawable(new ColorDrawable(Color.parseColor("#" + value.getString())), Gravity.LEFT, ClipDrawable.HORIZONTAL));
        }
    }

    private static class initprogressimage implements IFunction {
        public initprogressimage(){}

        public void run(RapidParserObject object, Object view, Var value) {
            final ProgressBar fBar = (ProgressBar)view;

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
                                drawable = new NinePatchDrawable(fBar.getContext().getResources(), bmp, chunk, new Rect(), null);
                            }
                            else{
                                drawable = new BitmapDrawable(bmp);
                            }

                            fBar.setProgressDrawable(drawable);
                        }
                    });
        }
    }

    private static class initindeterminate implements IFunction {
        public initindeterminate(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ProgressBar)view).setIndeterminate(value.getBoolean());
        }
    }

    private static class initprogress implements IFunction {
        public initprogress(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ProgressBar)view).setProgress(value.getInt());
        }
    }

    private static class initmax implements IFunction {
        public initmax(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ProgressBar)view).setMax(value.getInt());
        }
    }

    private static class initsecondaryprogress implements IFunction {
        public initsecondaryprogress(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ProgressBar)view).setSecondaryProgress(value.getInt());
        }
    }
}
