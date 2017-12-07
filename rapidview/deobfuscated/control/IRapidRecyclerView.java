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

    void updateData(List<Map<String, Var>> dataList, List<String> viewList, Boolean clear);

    void setFooter(String viewName, Map<String, Var> mapData);

    void updateFooterData(String key, Object value);

    void hideFooter();

    void showFooter();

    void scrollToBottom();

    void scrollToTop();

    void setActionListener(IRapidActionListener listener);

    void setMaxFlingCount(int count);
}
