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
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tencent.rapidview.deobfuscated.IRapidView;

/**
 * @Class NormalRecyclerViewHolder
 * @Desc RapidView recyclerView的Holder
 *
 * @author arlozhang
 * @date 2017.09.15
 */
public class NormalRecyclerViewHolder extends RecyclerView.ViewHolder{

    private IRapidView mView = null;

    private View mEmptyView = null;

    public NormalRecyclerViewHolder(Context context, IRapidView view){
        super(view.getView());

        mView = view;
    }

    public NormalRecyclerViewHolder(Context context, View emptyView){
        super(emptyView);

        emptyView.setTag("NONE");
        mEmptyView = emptyView;
    }


    public IRapidView getView(){
        return mView;
    }
}
