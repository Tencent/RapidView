package com.tencent.rapidview.deobfuscated.control;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @Class IItemDecorationListener
 * @Desc 给Lua代理的ItemDecoration接口
 *
 * @author arlozhang
 * @date 2017.09.20
 */
public interface IItemDecorationListener {

    void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state);

    void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state);

    void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state);
}
