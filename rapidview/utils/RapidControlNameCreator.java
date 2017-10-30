package com.tencent.rapidview.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Class RapidControlNameCreator
 * @Desc 匿名控件唯一名称生成器
 *
 * @author arlozhang
 * @date 2015.10.09
 */
public class RapidControlNameCreator {

    public static String get(){
        return Integer.toString(ViewIdGenerator.generateViewId());
    }

    private static class ViewIdGenerator {
        private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

        @SuppressLint("NewApi")
        public static int generateViewId() {

            if (Build.VERSION.SDK_INT < 17) {
                for (;;) {
                    final int result = sNextGeneratedId.get();
                    // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                    int newValue = result + 1;
                    if (newValue > 0x00FFFFFF)
                        newValue = 1; // Roll over to 1, not 0.
                    if (sNextGeneratedId.compareAndSet(result, newValue)) {
                        return result;
                    }
                }
            } else {
                return View.generateViewId();
            }

        }
    }
}