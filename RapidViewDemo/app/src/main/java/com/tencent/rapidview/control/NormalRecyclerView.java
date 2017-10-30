package com.tencent.rapidview.control;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.control.IItemDecorationListener;
import com.tencent.rapidview.deobfuscated.control.IRapidRecyclerView;

import java.util.List;
import java.util.Map;

/**
 * @Class NormalRecyclerView
 * @Desc 对CP开放的下载按钮，下载信息自拉取
 *
 * @author arlozhang
 * @date 2017.09.14
 */
public class NormalRecyclerView extends RecyclerView implements IRapidRecyclerView {

    private NormalRecyclerViewAdapter mAdapter = new NormalRecyclerViewAdapter();

    private IScrollStateChangedListener mScrollStateChangedListener = null;

    private IScrolledListener mScrolledListener = null;

    private IScrollBottomListener mBottomListener = null;

    private IScrollTopListener mTopListener = null;

    private MANAGER_TYPE mManagerType = MANAGER_TYPE.LINEAR;

    private int mLinearOrientation = 1;

    private enum MANAGER_TYPE {
        LINEAR,
        GRID,
    }

    public NormalRecyclerView(Context context){
        super(context);

        initView();
    }

    public NormalRecyclerViewAdapter getAdapter(){
        return mAdapter;
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
    public void setScrollTopListener(IScrollTopListener listener){
        mTopListener = listener;
    }

    @Override
    public void updateData(List<Map<String, Var>> dataList, List<String> viewList){
        mAdapter.updateData(dataList, viewList, false);
    }

    @Override
    public void updateData(List<Map<String, Var>> dataList, List<String> viewList, boolean clear){
        mAdapter.updateData(dataList, viewList, clear);
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
    public void addItemDecoration(IItemDecorationListener listener){
        NormalItemDecoration itemDecoration = new NormalItemDecoration();

        if( listener == null ){
            return;
        }

        itemDecoration.setListener(listener);
        addItemDecoration(itemDecoration);
    }

    private void initView(){
        setAdapter(mAdapter);

        setOnScrollListener(new RecyclerView.OnScrollListener(){

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                if (newState == RecyclerView.SCROLL_STATE_IDLE ) {

                    if( mManagerType == MANAGER_TYPE.LINEAR && mLinearOrientation == HORIZONTAL ){
                        if (mBottomListener != null && computeHorizontalScrollExtent() + computeHorizontalScrollOffset() >= computeHorizontalScrollRange()) {
                            mBottomListener.onScrollToBottom();
                        }

                        if( mTopListener != null && computeHorizontalScrollOffset() == 0 ){
                            mTopListener.onScrollToTop();
                        }
                    }
                    else{
                        if (mBottomListener != null && computeVerticalScrollExtent() + computeVerticalScrollOffset() >= computeVerticalScrollRange()) {
                            mBottomListener.onScrollToBottom();
                        }

                        if( mTopListener != null && computeVerticalScrollOffset() == 0 ){
                            mTopListener.onScrollToTop();
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

    public void setLinearLayoutManager(int orientation, boolean reverselayout){
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), orientation, reverselayout);

        mManagerType = MANAGER_TYPE.LINEAR;
        mLinearOrientation = orientation;

        setLayoutManager(manager);
    }

    public void setGridLayoutManager(int spanCount){
        GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount);

        mManagerType = MANAGER_TYPE.GRID;
        setLayoutManager(manager);
    }
}
