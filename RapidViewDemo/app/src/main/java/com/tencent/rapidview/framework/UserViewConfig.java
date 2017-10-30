package com.tencent.rapidview.framework;

import android.content.Context;
import android.view.View;

/**
 * @Class UserViewConfig
 * @Desc  获取用户自定义类方法
 *
 * @author arlozhang
 * @date 2016.07.20
 */
public class UserViewConfig {

    public interface IFunction{
        View get(Context context);
    }

//    public static class NormalErrorRecommendPageGeter implements IFunction{
//        @Override
//        public View get(Context context){
//            return new NormalErrorRecommendPage(context);
//        }
//    }

}
