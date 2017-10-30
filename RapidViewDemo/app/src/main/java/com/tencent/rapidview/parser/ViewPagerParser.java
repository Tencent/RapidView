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
