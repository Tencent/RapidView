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

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tencent.rapidview.deobfuscated.control.IItemDecorationListener;

/**
 * @Class NormalItemDecoration
 * @Desc RapidView使用的RecyclerView的通用Decoration
 *
 * @author arlozhang
 * @date 2017.09.20
 */
public class NormalItemDecoration extends RecyclerView.ItemDecoration{

    IItemDecorationListener mListener = null;

    public void setListener(IItemDecorationListener listener){
        mListener = listener;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state){
        super.onDraw(c, parent, state);

        if( mListener != null ){
            mListener.onDraw(c, parent, state);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state){
        super.onDrawOver(c, parent, state);

        if( mListener != null ){
            mListener.onDrawOver(c, parent, state);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        super.getItemOffsets(outRect, view, parent, state);

        if( mListener != null ){
            mListener.getItemOffsets(outRect, view, parent, state);
        }
    }
}
