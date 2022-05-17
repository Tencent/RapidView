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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.control.IItemDecorationListener;
import com.tencent.rapidview.deobfuscated.control.IRapidRecyclerView;
import com.tencent.rapidview.utils.DeviceQualityUtils;

import org.luaj.vm2.LuaTable;

import java.util.List;
import java.util.Map;

/**
 * @Class NormalRecyclerView
 * @Desc 通用RecyclerView
 *
 * @author arlozhang
 * @date 2017.09.14
 */
public class NormalRecyclerView extends RecyclerView implements IRapidRecyclerView {

    private NormalRecyclerViewAdapter mAdapter = new NormalRecyclerViewAdapter();

    private IScrollStateChangedListener mScrollStateChangedListener = null;

    private IScrolledListener mScrolledListener = null;

    private IScrollBottomListener mBottomListener = null;

    protected IScrollNearBottomListener mNearBottomListener = null;

    private IScrollTopListener mTopListener = null;

    private IInterruptTouchListener mInterruptListener = null;

    private MANAGER_TYPE mManagerType = MANAGER_TYPE.LINEAR;

    private int mFlingCount = 15000;

    protected int mNearBottomPxCount = 0;

    private boolean mScrollEnable = true;

    private int mLinearOrientation = 1;

    private enum MANAGER_TYPE {
        LINEAR,
        GRID,
    }

    public NormalRecyclerView(Context context){
        super(context);
        initFlingCount();
        initView();
    }

    public NormalRecyclerViewAdapter getAdapter(){
        return mAdapter;
    }

    @Override
    public void setInterruptTouchEvent(IInterruptTouchListener listener){
        mInterruptListener = listener;
    }

    @Override
    public void setScrollStateChangedListener(IScrollStateChangedListener listener){
        mScrollStateChangedListener = listener;
    }

    @Override
    public void setScrolledListener(IScrolledListener listener){
        mScrolledListener = listener;
    }

    @Override
    public void setScrollBottomListener(IScrollBottomListener listener){
        mBottomListener = listener;
    }

    @Override
    public void setScrollNearBottomListener(int px, IScrollNearBottomListener listener){
        mNearBottomPxCount = px;
        mNearBottomListener = listener;
    }


    @Override
    public void setScrollTopListener(IScrollTopListener listener){
        mTopListener = listener;
    }

    @Override
    public void updateData(String view, Map<String, Var> data){
        mAdapter.updateData(view, data);
    }

    @Override
    public void updateData(List<Map<String, Var>> dataList, List<String> viewList){
        mAdapter.updateData(dataList, viewList, false);
    }

    @Override
    public void updateData(String view, LuaTable data, Boolean clear){
        mAdapter.updateData(view, data, clear);
    }

    @Override
    public void updateData(List<Map<String, Var>> dataList, List<String> viewList, Boolean clear){
        mAdapter.updateData(dataList, viewList, clear);
    }

    @Override
    public void updateData(LuaTable viewList, LuaTable dataList){
        mAdapter.updateData(viewList, dataList);
    }

    @Override
    public void updateItemData(int index, String key, Object value){
        mAdapter.updateItemData(index, key, value);
    }

    @Override
    public void setFooter(String viewName, Map<String, Var> mapData) {
        mAdapter.setFooter(viewName, mapData);
    }

    @Override
    public void updateFooterData(String key, Object value){
        mAdapter.updateFooterData(key, value);
    }

    @Override
    public int getTypeByName(String name){
        return mAdapter.getTypeByName(name);
    }

    @Override
    public String getNameByType(int type){
        return mAdapter.getNameByType(type);
    }

    @Override
    public int getItemViewType(int position){
        return mAdapter.getItemViewType(position);
    }


    @Override
    public void setMaxFlingCount(int count){
        mFlingCount = count;
    }

    @Override
    public void setScrollEnable(Boolean enable){
        mScrollEnable = enable;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        if( velocityX > 8000 ){
            velocityX *= 0.5;
        }

        if( velocityY > 8000 ){
            velocityY *= 0.5;
        }

        if( mFlingCount != 0 ){
            if( velocityX > mFlingCount || velocityX < -mFlingCount ){
                if( velocityX < 0 ){
                    velocityX = -mFlingCount;
                }
                else {
                    velocityX = mFlingCount;
                }
            }

            if( velocityY > mFlingCount || velocityY < -mFlingCount ){
                if( velocityY < 0 ){
                    velocityY = -mFlingCount;
                }
                else {
                    velocityY = mFlingCount;
                }
            }
        }

        return super.fling(velocityX, velocityY);
    }

    @Override
    public void hideFooter(){
        mAdapter.hideFooter();
    }

    @Override
    public void showFooter(){
        mAdapter.showFooter();
    }

    @Override
    public void scrollToBottom(){
        scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void scrollToTop(){
        scrollToPosition(0);
    }

    @Override
    public void setActionListener(IRapidActionListener listener){
        mAdapter.setActionListener(listener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int ret;

        if( mInterruptListener == null ) {
            return super.onInterceptTouchEvent(ev);
        }

        ret = mInterruptListener.onInterceptTouchEvent(ev);
        if( ret == 1 ){
            return true;
        }

        if( ret == 0 ){
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void addItemDecoration(IItemDecorationListener listener){
        NormalItemDecoration itemDecoration = new NormalItemDecoration();

        if( listener == null ){
            return;
        }

        itemDecoration.setListener(listener);
        addItemDecoration(itemDecoration);
    }

    @Override
    public void clear(){
        mAdapter.clear();
    }

    private boolean getScrollEnable(){
        return mScrollEnable;
    }

    private void initView(){
        setAdapter(mAdapter);

        setOnScrollListener(new RecyclerView.OnScrollListener(){

            private int mRandomCount = 0;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mRandomCount++;
                if( mRandomCount > 60000 ){
                    mRandomCount = 0;
                }

                if (mScrollStateChangedListener != null){
                    mScrollStateChangedListener.onScrollStateChanged(recyclerView, newState);
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE ) {

                    if( mManagerType == MANAGER_TYPE.LINEAR && mLinearOrientation == HORIZONTAL ){
                        int count = computeHorizontalScrollRange() - computeHorizontalScrollExtent() - computeHorizontalScrollOffset();

                        if (mBottomListener != null && count <= 0) {
                            mBottomListener.onScrollToBottom();
                        }

                        if( mTopListener != null && computeHorizontalScrollOffset() == 0 ){
                            mTopListener.onScrollToTop();
                        }
                    }
                    else{
                        int count = computeVerticalScrollRange() - computeVerticalScrollExtent() - computeVerticalScrollOffset();

                        if (mBottomListener != null && count <= 0 ) {
                            mBottomListener.onScrollToBottom();
                        }

                        if( mTopListener != null && computeVerticalScrollOffset() == 0 ){
                            mTopListener.onScrollToTop();
                        }
                    }

                }
                else if( mRandomCount % 3 == 0 && mNearBottomListener != null ){

                    if( mManagerType == MANAGER_TYPE.LINEAR && mLinearOrientation == HORIZONTAL ){
                        int count = computeHorizontalScrollRange() - computeHorizontalScrollExtent() - computeHorizontalScrollOffset();

                        if( mNearBottomListener != null && count <= mNearBottomPxCount ){
                            mNearBottomListener.onScrollNearBottom();
                        }
                    }
                    else{
                        int count = computeVerticalScrollRange() - computeVerticalScrollExtent() - computeVerticalScrollOffset();

                        if( mNearBottomListener != null && count <= mNearBottomPxCount ){
                            mNearBottomListener.onScrollNearBottom();
                        }

                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if( mScrolledListener != null ){
                    mScrolledListener.onScrolled(recyclerView, dx, dy);
                }
            }

        });
    }

    @Override
    public void setMaxRecycledViews(String viewName, int max){
        getRecycledViewPool().setMaxRecycledViews(mAdapter.getViewType(viewName), max);
    }

    public void setLinearLayoutManager(int orientation, boolean reverselayout){
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), orientation, reverselayout){

            @Override
            public boolean canScrollVertically() {
                return getScrollEnable() && super.canScrollVertically();
            }

            @Override
            public boolean canScrollHorizontally(){
                return getScrollEnable() && super.canScrollHorizontally();
            }
        };

        mManagerType = MANAGER_TYPE.LINEAR;
        mLinearOrientation = orientation;
        setLayoutManager(manager);
    }

    public void setGridLayoutManager(int spanCount){
        GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount){

            @Override
            public boolean canScrollVertically() {
                return getScrollEnable() && super.canScrollVertically();
            }

            @Override
            public boolean canScrollHorizontally(){
                return getScrollEnable() && super.canScrollHorizontally();
            }
        };

        mManagerType = MANAGER_TYPE.GRID;
        setLayoutManager(manager);
    }

    public void setStaggeredGridLayoutManager(int spanCount, int orientation){
//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(spanCount, orientation);
//
//        mManagerType = MANAGER_TYPE.GRID;
//        setLayoutManager(manager);
    }

    private void initFlingCount(){
        switch ( DeviceQualityUtils.getDeviceQuality() ){
            case enum_low_quality:
                mFlingCount = 5600;
                break;
            case enum_middum_quality:
                mFlingCount = 11000;
                break;
            case enum_high_quality:
                mFlingCount = 15000;
                break;
        }
    }
}