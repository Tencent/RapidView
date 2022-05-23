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
package com.tencent.rapidview.control;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.deobfuscated.control.IRapidViewPager;
import com.tencent.rapidview.deobfuscated.control.IViewPagerListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Class NormalViewPager
 * @Desc 通用的viewpager，把adapter收拢进来
 *
 * @author arlozhang
 * @date 2017.07.03
 */
public class NormalViewPager extends ViewPager implements IRapidViewPager {

    private RapidPagerAdapter mAdapter = new RapidPagerAdapter(null);

    private IViewPagerListener mListener = null;

    private int mLastPage = 0;

    private Map<Integer, Boolean> mInitMap = new ConcurrentHashMap<Integer, Boolean>();

    private Map<Integer, String> mTagMap = new ConcurrentHashMap<Integer, String>();


    public NormalViewPager(Context context) {
        super(context);

        setAdapter(mAdapter);

        setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                String tag = mTagMap.get(i);

                if( tag == null ){
                    tag = "";
                }

                if( mListener != null ){
                    mListener.onPause(mLastPage, tag);
                }

                mLastPage = i;

                if( mListener != null ){
                    mListener.onResume(i, tag);

                    Boolean isInit = mInitMap.get(i);
                    if( isInit == null ){
                        isInit = new Boolean(false);
                    }

                    mListener.onPageSelected(i , tag, !isInit);
                    mInitMap.put(i, true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public RapidPagerAdapter getAdapter(){
        return mAdapter;
    }

    @Override
    public View getCurrentView(){
        return mAdapter.getView(getCurrentItem()).getView();
    }

    @Override
    public IRapidView getCurrentPhotonView(){
        return mAdapter.getView(getCurrentItem());
    }

    @Override
    public void setViewPagerListener(IViewPagerListener listener){
        mListener = listener;
    }

    @Override
    public void setTabTag(int index, String tag){
        mTagMap.put(index, tag);
    }

    @Override
    public String getTabTag(int index){
        if(mTagMap != null && mTagMap.size() > 0){
            return mTagMap.get(Integer.valueOf(index));
        }
        return "";
    }

    public void onPause(){
        String tag = mTagMap.get(mLastPage);

        if( tag == null ){
            tag = "";
        }

        if( mListener == null ){
            return;
        }

        mListener.onPause(mLastPage, tag);
    }

    public void onResume(){
        String tag = mTagMap.get(mLastPage);

        if( tag == null ){
            tag = "";
        }

        if( mListener == null ){
            return;
        }

        mListener.onResume(mLastPage, tag);
    }
}
