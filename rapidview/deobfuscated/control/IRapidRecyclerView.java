package com.tencent.rapidview.deobfuscated.control;

import android.support.v7.widget.RecyclerView;

import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.data.Var;

import java.util.List;
import java.util.Map;

/**
 * @Class IRapidRecyclerView
 * @Desc RecyclerView非混淆接口
 *
 * @author arlozhang
 * @date 2017.09.19
 */
public interface IRapidRecyclerView {

    interface IScrollStateChangedListener{

        void onScrollStateChanged(RecyclerView recyclerView, int newState);
    }

    interface IScrolledListener{

        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }

    interface IScrollBottomListener{

        void onScrollToBottom();
    }

    interface IScrollTopListener{

        void onScrollToTop();
    }

    void addItemDecoration(IItemDecorationListener listener);

    void setScrollStateChangedListener(IScrollStateChangedListener listener);

    void setScrolledListener(IScrolledListener listener);

    void setScrollBottomListener(IScrollBottomListener listener);

    void setScrollTopListener(IScrollTopListener listener);

    void updateData(List<Map<String, Var>> dataList, List<String> viewList);

    void updateData(List<Map<String, Var>> dataList, List<String> viewList, boolean clear);

    void setFooter(String viewName, Map<String, Var> mapData);

    void updateFooterData(String key, Object value);

    void hideFooter();

    void showFooter();

    void scrollToBottom();

    void scrollToTop();

    void setActionListener(IRapidActionListener listener);
}
