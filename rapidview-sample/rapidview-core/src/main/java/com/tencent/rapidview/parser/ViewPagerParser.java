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

import com.tencent.rapidview.control.NormalViewPager;
import com.tencent.rapidview.deobfuscated.IRapidView;

/**
 * @Class ViewPagerParser
 * @Desc RapidView界面控件ViewPager解析器
 *
 * @author arlozhang
 * @date 2017.07.03
 */
public class ViewPagerParser extends ViewGroupParser {

    @Override
    public void onPause(){
        ((NormalViewPager)mRapidView.getView()).onPause();
    }

    @Override
    public void onResume(){
        ((NormalViewPager)mRapidView.getView()).onResume();
    }

    @Override
    public IRapidView getChildView(String id){
        IRapidView retView;

        if( id == null ){
            return null;
        }

        if( id.compareToIgnoreCase(getID()) == 0 ){
            return mRapidView;
        }

        retView = mMapChild.get(id);

        if( retView != null ){
            return retView;
        }

        for( IRapidView view : mMapChild.values() ){
            if( view == null ){
                continue;
            }

            retView = view.getParser().getChildView(id);

            if( retView != null ){
                return retView;
            }
        }

        retView = ((NormalViewPager)mRapidView.getView()).getAdapter().getChildView(id);


        return retView;
    }
}
