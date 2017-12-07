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

import android.widget.ScrollView;

import com.tencent.rapidview.control.NormalScrollView;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.deobfuscated.IScrollView;
import com.tencent.rapidview.utils.RapidStringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ScrollViewParser
 * @Desc RapidView界面控件ScrollView解析器
 *
 * @author arlozhang
 * @date 2016.03.31
 */
public class ScrollViewParser extends FrameLayoutParser {
    private static Map<String, IFunction> mScrollViewClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mScrollViewClassMap.put("fling", initfling.class.newInstance());
            mScrollViewClassMap.put("fullscroll", initfullscroll.class.newInstance());
            mScrollViewClassMap.put("scrollto", initscrollto.class.newInstance());
            mScrollViewClassMap.put("scrolltochild", initscrolltochild.class.newInstance());
            mScrollViewClassMap.put("smoothscrollingenabled", initsmoothscrollingenabled.class.newInstance());
            mScrollViewClassMap.put("smoothscrollby", initsmoothscrollby.class.newInstance());
            mScrollViewClassMap.put("smoothscrollto", initsmoothscrollto.class.newInstance());
            mScrollViewClassMap.put("notifychildscroll", initnotifychildscroll.class.newInstance());
            mScrollViewClassMap.put("overscrollmode", initoverscrollmode.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public ScrollViewParser(){}

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mScrollViewClassMap.get(key);

        return clazz;
    }

    private static class initfling implements IFunction {
        public initfling(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ScrollView)view).fling(value.getInt());
        }
    }

    private static class initoverscrollmode implements IFunction {
        public initoverscrollmode(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ScrollView)view).setOverScrollMode(value.getInt());
        }
    }


    private static class initnotifychildscroll implements IFunction {
        public initnotifychildscroll(){}

        public void run(RapidParserObject object, Object view, Var value) {
            if( !value.getBoolean() ){
                return;
            }

            final RapidParserObject obj = object;
            final Object fview = view;

            ((NormalScrollView)view).setScrollListener(new IScrollView.IScrollViewListener()  {

                @Override
                public void onScrollChanged(int l, int t, int oldl, int oldt){
                    obj.notify(EVENT.enum_parent_scroll, null, fview, l, t, oldl ,oldt);
                }

                @Override
                public void onOverScrolled(int scrollX, int scrollY, Boolean clampedX, Boolean clampedY){
                    obj.notify(EVENT.enum_parent_over_scrolled, null, fview, scrollX, scrollY, clampedX, clampedY);
                }
            });
        }
    }

    private static class initfullscroll implements IFunction {
        public initfullscroll(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ScrollView)view).fullScroll(value.getInt());
        }
    }

    private static class initsmoothscrollingenabled implements IFunction {
        public initsmoothscrollingenabled(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ((ScrollView)view).setSmoothScrollingEnabled(value.getBoolean());
        }
    }

    private static class initscrollto implements IFunction {
        public initscrollto(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> list = RapidStringUtils.stringToList(value.getString());
            if( list.size() < 2 ){
                return;
            }

            ((ScrollView)view).scrollTo(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
        }
    }

    private static class initscrolltochild implements IFunction {
        public initscrolltochild(){}

        public void run(RapidParserObject object, Object view, Var value) {
            IRapidView viewChild = object.getChildView(value.getString());
            IRapidView parent = null;
            int left = 0;
            int top = 0;

            if( viewChild == null || viewChild.getView() == null ){
                return;
            }

            left = viewChild.getView().getLeft();
            top = viewChild.getView().getTop();
            parent = viewChild.getParser().getParentView();

            while( parent != null && parent.getParser().getID().compareTo(object.getID()) != 0 ){
                left += parent.getView().getLeft();
                top += parent.getView().getTop();
                parent = parent.getParser().getParentView();
            }

            ((ScrollView)view).scrollTo(left, top);
        }
    }

    private static class initsmoothscrollby implements IFunction {
        public initsmoothscrollby(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> list = RapidStringUtils.stringToList(value.getString());
            if( list.size() < 2 ){
                return;
            }

            ((ScrollView)view).smoothScrollBy(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
        }
    }

    private static class initsmoothscrollto implements IFunction {
        public initsmoothscrollto(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> list = RapidStringUtils.stringToList(value.getString());
            if( list.size() < 2 ){
                return;
            }

            ((ScrollView)view).smoothScrollTo(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
        }
    }
}
