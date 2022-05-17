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

import android.widget.HorizontalScrollView;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.deobfuscated.IRapidView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class HorizontalScrollViewParser
 * @Desc RapidView 界面ScrollViewParser
 *
 * @author arlozhang
 * @date 2016.04.01
 */
public class HorizontalScrollViewParser extends FrameLayoutParser {

    private static Map<String, RapidParserObject.IFunction> mHorizontalScrollViewClassMap = new ConcurrentHashMap<String, RapidParserObject.IFunction>();

    static{
        try{
            mHorizontalScrollViewClassMap.put("fling", initfling.class.newInstance());
            mHorizontalScrollViewClassMap.put("fullscroll", initfullscroll.class.newInstance());
            mHorizontalScrollViewClassMap.put("scrollto", initscrollto.class.newInstance());
            mHorizontalScrollViewClassMap.put("smoothscrollingenabled", initsmoothscrollingenabled.class.newInstance());
            mHorizontalScrollViewClassMap.put("smoothscrollby", initsmoothscrollby.class.newInstance());
            mHorizontalScrollViewClassMap.put("smoothscrollto", initsmoothscrollto.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public HorizontalScrollViewParser(){}

    @Override
    protected RapidParserObject.IFunction getAttributeFunction(String key, IRapidView view){
        RapidParserObject.IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mHorizontalScrollViewClassMap.get(key);

        return clazz;
    }

    private static class initfling implements RapidParserObject.IFunction {
        public initfling(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((HorizontalScrollView)view).fling(value.getInt());
        }
    }

    private static class initfullscroll implements RapidParserObject.IFunction {
        public initfullscroll(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((HorizontalScrollView)view).fullScroll(value.getInt());
        }
    }

    private static class initsmoothscrollingenabled implements RapidParserObject.IFunction {
        public initsmoothscrollingenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((HorizontalScrollView)view).setSmoothScrollingEnabled(value.getBoolean());
        }
    }

    private static class initscrollto implements RapidParserObject.IFunction {
        public initscrollto(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> list = RapidStringUtils.stringToList(value.getString());
            if( list.size() < 2 ){
                return;
            }

            ((HorizontalScrollView)view).scrollTo(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
        }
    }

    private static class initsmoothscrollby implements RapidParserObject.IFunction {
        public initsmoothscrollby(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> list = RapidStringUtils.stringToList(value.getString());
            if( list.size() < 2 ){
                return;
            }

            ((HorizontalScrollView)view).smoothScrollBy(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
        }
    }

    private static class initsmoothscrollto implements RapidParserObject.IFunction {
        public initsmoothscrollto(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> list = RapidStringUtils.stringToList(value.getString());
            if( list.size() < 2 ){
                return;
            }

            ((HorizontalScrollView)view).smoothScrollTo(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
        }
    }
}
