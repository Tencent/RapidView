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
package com.tencent.rapidview.framework;

import com.tencent.rapidview.view.RapidAbsoluteLayout;
import com.tencent.rapidview.view.RapidButton;
import com.tencent.rapidview.view.RapidFrameLayout;
import com.tencent.rapidview.view.RapidHorizontalScrollView;
import com.tencent.rapidview.view.RapidImageButton;
import com.tencent.rapidview.view.RapidImageView;
import com.tencent.rapidview.view.RapidLinearLayout;
import com.tencent.rapidview.view.RapidProgressBar;
import com.tencent.rapidview.view.RapidRecyclerView;
import com.tencent.rapidview.view.RapidRelativeLayout;
import com.tencent.rapidview.view.RapidRuntimeView;
import com.tencent.rapidview.view.RapidScrollView;
import com.tencent.rapidview.view.RapidShaderView;
import com.tencent.rapidview.view.RapidTextView;
import com.tencent.rapidview.view.RapidUserView;
import com.tencent.rapidview.view.RapidViewPager;
import com.tencent.rapidview.view.RapidViewStub;

import org.w3c.dom.Element;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidChooser
 * @Desc RapidView控件选择器
 *
 * @author arlozhang
 * @date 2015.09.22
 */
public class RapidChooser {

    private static RapidChooser msInstance = null;

    private static Map<String, Class> mAllClassMap = new ConcurrentHashMap<String, Class>();

    private static Map<String, Class> mLimitClassMap = new ConcurrentHashMap<String, Class>();

    static{
        mAllClassMap.put("userview", RapidUserView.class);
        mAllClassMap.put("relativelayout", RapidRelativeLayout.class);
        mAllClassMap.put("linearlayout", RapidLinearLayout.class);
        mAllClassMap.put("absolutelayout", RapidAbsoluteLayout.class);
        mAllClassMap.put("textview", RapidTextView.class);
        mAllClassMap.put("imageview", RapidImageView.class);
        mAllClassMap.put("progressbar", RapidProgressBar.class);
        mAllClassMap.put("imagebutton", RapidImageButton.class);
        mAllClassMap.put("button", RapidButton.class);
        mAllClassMap.put("framelayout", RapidFrameLayout.class);
        mAllClassMap.put("scrollview", RapidScrollView.class);
        mAllClassMap.put("horizontalscrollview", RapidHorizontalScrollView.class);
        mAllClassMap.put("shaderview", RapidShaderView.class);
        mAllClassMap.put("viewstub", RapidViewStub.class);
        mAllClassMap.put("runtimeview", RapidRuntimeView.class);
        mAllClassMap.put("viewpager", RapidViewPager.class);
        mAllClassMap.put("recyclerview", RapidRecyclerView.class);
     }

    static{
        mLimitClassMap.put("relativelayout", RapidRelativeLayout.class);
        mLimitClassMap.put("linearlayout", RapidLinearLayout.class);
        mLimitClassMap.put("absolutelayout", RapidAbsoluteLayout.class);
        mLimitClassMap.put("textview", RapidTextView.class);
        mLimitClassMap.put("imagebutton", RapidImageButton.class);
        mLimitClassMap.put("button", RapidButton.class);
        mLimitClassMap.put("framelayout", RapidFrameLayout.class);
        mLimitClassMap.put("scrollview", RapidScrollView.class);
        mLimitClassMap.put("horizontalscrollview", RapidHorizontalScrollView.class);
        mLimitClassMap.put("shaderview", RapidShaderView.class);
        mLimitClassMap.put("viewstub", RapidViewStub.class);
        mLimitClassMap.put("viewpager", RapidViewPager.class);
        mLimitClassMap.put("recyclerview", RapidRecyclerView.class);
    }

    private RapidChooser(){}

    public static RapidChooser getInstance(){
        if( msInstance == null ){
            msInstance = new RapidChooser();
        }

        return msInstance;
    }

    public Class getDisposalClass(Element element, boolean limitLevel){
        Class clazz;
        String strClass;

        try{
            strClass = element.getAttribute("disposal");

            if( strClass.compareToIgnoreCase("") != 0 ){
                clazz = Class.forName(strClass);
            }
            else{
                String elementName = element.getTagName();

                if( limitLevel ){
                    clazz = mLimitClassMap.get(elementName.toLowerCase());
                }
                else{
                    clazz = mAllClassMap.get(elementName.toLowerCase());
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            clazz = null;
        }

        return clazz;
    }
}
