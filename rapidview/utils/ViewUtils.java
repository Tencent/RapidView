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
package com.tencent.rapidview.utils;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.rapidview.framework.RapidEnv;

public class ViewUtils
{

	public static float DEVICE_DENSITY = 0;

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue)
	{
        if( context == null ){
            return (int) (dpValue * 2.75 + 0.5f);
        }

		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 根据某个密度从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue, float density) {
		return (int) (pxValue / density + 0.5f);
	}

	/*
	 * 根据某个像素和对应的密度，转换为dp，再转换为本机的px
	 * */
	public static int pxDensity2LocalPx(float pxValue, float density) {
		int dp = px2dip(pxValue, density);
		return dip2px(RapidEnv.getApplication(), dp);
	}
	public static float getSpValue(float value)
	{
		if (DEVICE_DENSITY == 0)
		{
			DEVICE_DENSITY = RapidEnv.getApplication().getResources().getDisplayMetrics().densityDpi;
		}
		return value * DEVICE_DENSITY / 160;
	}

	public static int getSpValueInt(float value)
	{
		return (int) getSpValue(value);
	}
	
	/**
	 * 动态设置字体
	 * 考虑用户设置了放大字体的时候
	 * @param value
	 * @return
	 */
	public static float getSpValueForFont(float value) {
		float scale = RapidEnv.getApplication().getResources().getDisplayMetrics().scaledDensity;
		return value * scale + 0.5f;
	}
	
	/**
	 * 动态设置字体
	 * 考虑用户设置了放大字体的时候
	 * @param value
	 * @return
	 */
	public static int getSpValueForFontInt(float value)
	{
		return (int) getSpValueForFont(value);
	}

	public static float getPxValue(float dpValue)
	{
		if (DEVICE_DENSITY == 0)
		{
			DEVICE_DENSITY = RapidEnv.getApplication().getResources().getDisplayMetrics().densityDpi;
		}
		return dpValue * 160 / DEVICE_DENSITY;
	}

	public static int getPxValueInt(float dpValue)
	{
		return (int) getPxValue(dpValue);
	}

	public static boolean isChildOf(View c, View p)
	{
		if (c == p)
		{
			return true;
		}
		else if (p instanceof ViewGroup)
		{
			int count = ((ViewGroup) p).getChildCount();
			for (int i = 0; i < count; i++)
			{
				View ci = ((ViewGroup) p).getChildAt(i);
				if (isChildOf(c, ci))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static Rect getRectBlock(View child)
	{
		int[] posContainer = new int[2];
		getChildPos(child, null, posContainer);
		return new Rect(posContainer[0], posContainer[1], posContainer[0] + child.getWidth(), posContainer[1] + child.getHeight());
	}

	/**
	 * @param child
	 * @param parent
	 *            if null, return child position to view tree root.
	 * @param posContainer
	 */
	public static void getChildPos(View child, View parent, int[] posContainer)
	{
		if (posContainer == null || posContainer.length < 2)
		{
			return;
		}
		int x = 0;
		int y = 0;
		View vc = child;
		while (vc.getParent() != null)
		{
			x += vc.getLeft();
			y += vc.getTop();
			if (vc.getParent() == parent)
			{
				posContainer[0] = x;
				posContainer[1] = y;
				if (posContainer.length >= 4)
				{
					posContainer[2] = vc.getMeasuredWidth();
					posContainer[3] = vc.getMeasuredHeight();
				}
				break;
			}
			try
			{
				vc = (View) vc.getParent();
				if (posContainer.length >= 4)
				{
					posContainer[2] = vc.getMeasuredWidth();
					posContainer[3] = vc.getMeasuredHeight();
				}
			}
			catch (ClassCastException e)
			{
				break;
			}
		}
		if (parent == null)
		{
			posContainer[0] = x;
			posContainer[1] = y;
		}
	}

	public static String getActivityName(Context ctx)
	{
		Context c = ctx;
		if (!(ctx instanceof Activity) && (ctx instanceof ContextWrapper))
		{
			c = ((ContextWrapper) ctx).getBaseContext();
		}
		return c.getClass().getName();
	}

	protected int[] getPicBounds(View v)
	{
		int[] posContainer = new int[4];
		ViewUtils.getChildPos(v, null, posContainer);
		posContainer[0] += v.getPaddingLeft();
		posContainer[1] += v.getPaddingTop();
		posContainer[2] = v.getWidth() - v.getPaddingLeft() - v.getPaddingRight();
		posContainer[3] = v.getHeight() - v.getPaddingTop() - v.getPaddingBottom();
		return posContainer;
	}

	public static int getStatusBarHeight()
	{
		int value = 0, statusBarHeight = 0;
		try
		{
			Class<?> classR = Class.forName("com.android.internal.R$dimen");
			Object obj = classR.newInstance();
			Field field = classR.getField("status_bar_height");
			value = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = RapidEnv.getApplication().getResources().getDimensionPixelSize(value);
			return statusBarHeight;
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			return 0;
		}
	}

	/*
	 * 获取屏幕宽度
	 */
	public static int getScreenWidth()
	{
		return RapidEnv.getApplication().getResources().getDisplayMetrics().widthPixels;
	}
	/*
	 * 获取屏幕高度
	 */
	public static int getScreenHeight()
	{
		return RapidEnv.getApplication().getResources().getDisplayMetrics().heightPixels;
	}
	
	public static int getNavigationBarHeight() {
	    Resources resources = RapidEnv.getApplication().getResources();
	    try {
    	    int id = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    	    if (id > 0) {
    	        return resources.getDimensionPixelSize(id);
    	    }
	    } catch (Throwable tr) {
	        // do nothing
	    }
	    return 0;
	}
	
	public static boolean isScreen640x960() {
		int width = RapidEnv.getApplication().getResources().getDisplayMetrics().widthPixels;
		int height = RapidEnv.getApplication().getResources().getDisplayMetrics().heightPixels;
		if (width == 640 && height == 960) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isScreen480x854() {
		int width = RapidEnv.getApplication().getResources().getDisplayMetrics().widthPixels;
		int height = RapidEnv.getApplication().getResources().getDisplayMetrics().heightPixels;
		if (width == 480 && height == 854) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取一个View的截图Bitmap
	 */
	public static Bitmap loadBitmapFromView(View v) {
		if (v == null) {
			return null;
		}
		Bitmap screenshot;
		screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(screenshot);
		c.translate(-v.getScrollX(), -v.getScrollY());
		v.draw(c);
		return screenshot;
	}
	

}
