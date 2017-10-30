package com.tencent.rapidview.deobfuscated;

import android.content.Context;

import com.tencent.rapidview.param.ParamsObject;

/**
 * @Class IRapidViewGroup
 * @Desc RapidView界面ViewGroup接口
 *
 * @author arlozhang
 * @date 2015.09.22
 */
public interface IRapidViewGroup extends IRapidView {

    ParamsObject createParams(Context context);

}
