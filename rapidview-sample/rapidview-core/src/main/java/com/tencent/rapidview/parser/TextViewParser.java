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

import android.graphics.Color;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.ViewUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class TextViewParser
 * @Desc RapidView界面TextView解析器
 *
 * @author arlozhang
 * @date 2015.09.22
 */
public class TextViewParser extends ViewParser {

    private static Map<String, IFunction> mTextViewClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mTextViewClassMap.put("ems", initems.class.newInstance());
            mTextViewClassMap.put("maxems", initmaxems.class.newInstance());
            mTextViewClassMap.put("minems", initminems.class.newInstance());
            mTextViewClassMap.put("singleline", initsingleline.class.newInstance());
            mTextViewClassMap.put("ellipsize", initellipsize.class.newInstance());
            mTextViewClassMap.put("text", inittext.class.newInstance());
            mTextViewClassMap.put("textstyle", inittextstyle.class.newInstance());
            mTextViewClassMap.put("textsize", inittextsize.class.newInstance());
            mTextViewClassMap.put("textcolor", inittextcolor.class.newInstance());
            mTextViewClassMap.put("line", initline.class.newInstance());
            mTextViewClassMap.put("flag", initflag.class.newInstance());
            mTextViewClassMap.put("gravity", initgravity.class.newInstance());
            mTextViewClassMap.put("maxlines", initmaxlines.class.newInstance());
            mTextViewClassMap.put("linespacingextra", initlinespacingextra.class.newInstance());
            mTextViewClassMap.put("linespacingmultiplier", initlinespacingmultiplier.class.newInstance());
            mTextViewClassMap.put("scalex", initscalex.class.newInstance());
            mTextViewClassMap.put("scaley", initscaley.class.newInstance());
            mTextViewClassMap.put("textscalex", inittextscalex.class.newInstance());
            mTextViewClassMap.put("freezestext", initfreezestext.class.newInstance());
            mTextViewClassMap.put("maxheight", initmaxheight.class.newInstance());
            mTextViewClassMap.put("minheight", initminheight.class.newInstance());
            mTextViewClassMap.put("maxwidth", initmaxwidth.class.newInstance());
            mTextViewClassMap.put("minwidth", initminwidth.class.newInstance());
            mTextViewClassMap.put("autolink", initautolink.class.newInstance());
            mTextViewClassMap.put("buffertype", initbuffertype.class.newInstance());
            mTextViewClassMap.put("cursorvisible", initcursorvisible.class.newInstance());
            mTextViewClassMap.put("hint", inithint.class.newInstance());
            mTextViewClassMap.put("imeactionid", initimeactionid.class.newInstance());
            mTextViewClassMap.put("imeactionlabel", initimeactionlabel.class.newInstance());
            mTextViewClassMap.put("imeoptions", initimeoptions.class.newInstance());
            mTextViewClassMap.put("includefontpadding", initincludefontpadding.class.newInstance());
            mTextViewClassMap.put("inputtype", initinputtype.class.newInstance());
            mTextViewClassMap.put("rawinputtype", initrawinputtype.class.newInstance());
        }
        catch ( Exception e){
            e.printStackTrace();
        }

    }

    public TextViewParser(){}

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mTextViewClassMap.get(key);

        return clazz;
    }

    private static class initautolink implements IFunction {
        public initautolink(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> list = RapidStringUtils.stringToList(value.getString());
            int mask = 0;

            for( int i = 0; i < list.size(); i++ ){
                String str = list.get(i);
                if( str.compareToIgnoreCase("phone") == 0 ){
                    mask |= Linkify.PHONE_NUMBERS;
                }
                else if( str.compareToIgnoreCase("web") == 0 ){
                    mask |= Linkify.WEB_URLS;
                }
                else if( str.compareToIgnoreCase("email") == 0 ){
                    mask |= Linkify.EMAIL_ADDRESSES;
                }
                else if( str.compareToIgnoreCase("map") == 0 ){
                    mask |= Linkify.MAP_ADDRESSES;
                }
                else if( str.compareToIgnoreCase("all") == 0 ){
                    mask |= Linkify.ALL;
                }
            }

            ((TextView)view).setAutoLinkMask(mask);
        }
    }

    private static class initbuffertype implements IFunction {
        public initbuffertype(){}

        public void run(RapidParserObject object, Object view, Var value) {
            String str = value.getString();
            if( str.compareToIgnoreCase("normal") == 0 ){
                ((TextView)view).setText(((TextView)view).getText(), TextView.BufferType.NORMAL);
            }
            else if( str.compareToIgnoreCase("spannable") == 0 ){
                ((TextView)view).setText(((TextView)view).getText(), TextView.BufferType.SPANNABLE);
            }
            else if( str.compareToIgnoreCase("editable") == 0 ){
                ((TextView)view).setText(((TextView)view).getText(), TextView.BufferType.EDITABLE);
            }
        }
    }

    private static class initcursorvisible implements IFunction {
        public initcursorvisible(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setCursorVisible(value.getBoolean());
        }
    }

    private static class inithint implements IFunction {
        public inithint(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setHint(value.getString());
        }
    }

    private static class initimeactionid implements IFunction {
        public initimeactionid(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setImeActionLabel(((TextView)view).getImeActionLabel(), value.getInt());
        }
    }

    private static class initimeactionlabel implements IFunction {
        public initimeactionlabel(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setImeActionLabel(value.getString(), ((TextView)view).getImeActionId());
        }
    }

    private static class initimeoptions implements IFunction {
        public initimeoptions(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> list = RapidStringUtils.stringToList(value.getString());
            int options = 0;

            for( int i = 0; i < list.size(); i++ ){
                String str = list.get(i);

                if( str.compareToIgnoreCase("normal") == 0 ){
                    options |= EditorInfo.IME_NULL;
                }
                else if( str.compareToIgnoreCase("actionunspecified") == 0 ){
                    options |= EditorInfo.IME_NULL;
                }
                else if( str.compareToIgnoreCase("actionNone") == 0 ){
                    options |= EditorInfo.IME_ACTION_NONE;
                }
                else if( str.compareToIgnoreCase("actionGo") == 0 ){
                    options |= EditorInfo.IME_ACTION_GO;
                }
                else if( str.compareToIgnoreCase("actionSearch") == 0 ){
                    options |= EditorInfo.IME_ACTION_SEARCH;
                }
                else if( str.compareToIgnoreCase("actionSend") == 0 ){
                    options |= EditorInfo.IME_ACTION_SEND;
                }
                else if( str.compareToIgnoreCase("actionNext") == 0 ){
                    options |= EditorInfo.IME_ACTION_NEXT;
                }
                else if( str.compareToIgnoreCase("actionDone") == 0 ){
                    options |= EditorInfo.IME_ACTION_DONE;
                }
                else if( str.compareToIgnoreCase("actionPrevious") == 0 ){
                    options |= EditorInfo.IME_ACTION_PREVIOUS;
                }
                else if( str.compareToIgnoreCase("flagNoFullscreen") == 0 ){
                    options |= EditorInfo.IME_FLAG_NO_FULLSCREEN;
                }
                else if( str.compareToIgnoreCase("flagNavigatePrevious") == 0 ){
                    options |= EditorInfo.IME_ACTION_PREVIOUS;
                }
                else if( str.compareToIgnoreCase("flagNavigateNext") == 0 ){
                    options |= EditorInfo.IME_FLAG_NAVIGATE_NEXT;
                }
                else if( str.compareToIgnoreCase("flagNoExtractUi") == 0 ){
                    options |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;
                }
                else if( str.compareToIgnoreCase("flagNoAccessoryAction") == 0 ){
                    options |= EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION;
                }
                else if( str.compareToIgnoreCase("flagNoEnterAction") == 0 ){
                    options |= EditorInfo.IME_FLAG_NO_ENTER_ACTION;
                }
                else if( str.compareToIgnoreCase("flagForceAscii") == 0 ){
                    options |= EditorInfo.IME_FLAG_FORCE_ASCII;
                }
            }

            ((TextView)view).setImeOptions(options);
        }
    }

    private static class initincludefontpadding implements IFunction {
        public initincludefontpadding(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setIncludeFontPadding(value.getBoolean());
        }
    }

    private static class initinputtype implements IFunction {
        public initinputtype(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setInputType(value.getInt());
        }
    }

    private static class initrawinputtype implements IFunction {
        public initrawinputtype(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setRawInputType(value.getInt());
        }
    }

    private static class initgravity implements IFunction {
        public initgravity(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setGravity(value.getInt());
        }
    }

    private static class initems implements IFunction {
        public initems(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setEms(value.getInt());
        }
    }

    private static class initmaxems implements IFunction {
        public initmaxems(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setMaxEms(value.getInt());
        }
    }

    private static class initminems implements IFunction {
        public initminems(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setMinEms(value.getInt());
        }
    }

    private static class initsingleline implements IFunction {
        public initsingleline(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setSingleLine(value.getBoolean());
        }
    }

    private static class initfreezestext implements IFunction {
        public initfreezestext(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setFreezesText(value.getBoolean());
        }
    }


    private static class initellipsize implements IFunction {
        public initellipsize(){}

        public void run(RapidParserObject object, Object view, Var value) {
            String str = value.getString();

            if( str.compareToIgnoreCase("end") == 0 ){
                ((TextView)view).setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            }
            else if( str.compareToIgnoreCase("start") == 0 ){
                ((TextView)view).setEllipsize(TextUtils.TruncateAt.valueOf("START"));
            }else if( str.compareToIgnoreCase("middle") == 0 ){
                ((TextView)view).setEllipsize(TextUtils.TruncateAt.valueOf("MIDDLE"));
            }else if( str.compareToIgnoreCase("marquee") == 0 ){
                ((TextView)view).setEllipsize(TextUtils.TruncateAt.valueOf("MARQUEE"));
            }
        }
    }

    private static class inittext implements IFunction {
        public inittext(){}

        public void run(RapidParserObject object, Object view, Var value) {
            String realValue = value.getString().replace("#", "\n");

            ((TextView)view).setText(realValue);
        }
    }

    private static class inittextstyle implements IFunction {
        public inittextstyle(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> list = RapidStringUtils.stringToList(value.getString());

            for( int i = 0; i < list.size(); i++ ){
                if( list.get(i).compareToIgnoreCase("bold") == 0 ){
                    ((TextView)view).getPaint().setFakeBoldText(true);
                }
                else if( list.get(i).compareToIgnoreCase("italic") == 0 ){
                    ((TextView)view).getPaint().setTextSkewX(-0.5f);
                }
                else if( list.get(i).compareToIgnoreCase("leftitalic") == 0 ){
                    ((TextView)view).getPaint().setTextSkewX(0.5f);
                }
                else if( list.get(i).compareToIgnoreCase("strikethru") == 0 ){
                    ((TextView)view).getPaint().setStrikeThruText(true);
                }
                else if( list.get(i).compareToIgnoreCase("underline") == 0 ){
                    ((TextView)view).getPaint().setUnderlineText(true);
                }
            }
        }
    }

    private static class inittextsize implements IFunction {
        public inittextsize(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewUtils.dip2px(((View)view).getContext(), value.getFloat()));
        }
    }

    private static class inittextcolor implements IFunction {
        public inittextcolor(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setTextColor(Color.parseColor("#" + value.getString()));
        }
    }

    private static class initline implements IFunction {
        public initline(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setLines(value.getInt());
        }
    }

    private static class initflag implements IFunction {
        public initflag(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).getPaint().setFlags(value.getInt());
        }
    }

    private static class initmaxlines implements IFunction {
        public initmaxlines(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setMaxLines(value.getInt());
        }
    }

    private static class initlinespacingextra implements IFunction {
        public initlinespacingextra(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setLineSpacing(ViewUtils.dip2px(((View) view).getContext(), value.getFloat()), 0);
        }
    }

    private static class initlinespacingmultiplier implements IFunction {
        public initlinespacingmultiplier(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setLineSpacing(0, value.getFloat());
        }
    }

    private static class initscalex implements IFunction {
        public initscalex(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setScaleX(value.getFloat());
        }
    }

    private static class initscaley implements IFunction {
        public initscaley(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setScaleY(value.getFloat());
        }
    }

    private static class inittextscalex implements IFunction {
        public inittextscalex(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((TextView)view).setTextScaleX(value.getFloat());
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

            ((TextView)view).setMaxHeight(height);
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

            ((TextView)view).setMinHeight(height);
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

            ((TextView)view).setMaxWidth(width);
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

            ((TextView)view).setMinWidth(width);
        }
    }
}
