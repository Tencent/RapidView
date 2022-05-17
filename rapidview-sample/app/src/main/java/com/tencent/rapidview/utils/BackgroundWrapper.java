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

import android.graphics.Bitmap;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;

import java.util.concurrent.ConcurrentHashMap;


public class BackgroundWrapper {

	private static BackgroundWrapper sInstance = null;
	
	private ConcurrentHashMap<String, Drawable> mDrawableMap = new ConcurrentHashMap<String, Drawable>();
	
	public static synchronized BackgroundWrapper getInstance(){
		if(sInstance == null){
			sInstance = new BackgroundWrapper();
		}
		return sInstance;
	}
	
	public void setBackground(final View view, String assetUrl, String rapidID, boolean isLimitLevel){
        if( view == null || assetUrl == null ){
            return;
        }

		if(mDrawableMap.containsKey(assetUrl)){
			view.setBackgroundDrawable(mDrawableMap.get(assetUrl));
		}
        else
        {
			//first ,load asset bitmap
			RapidImageLoader.get(view.getContext(), assetUrl, rapidID, isLimitLevel, new RapidImageLoader.ICallback(){

				@Override
				public void finish(boolean succeed, String name, Bitmap bmp) {
                    if( !succeed ){

                        return;
                    }

					setBacground(view, name, bmp);
				}
				
			});
		}
	}
	
	private void setBacground(View view, String url, Bitmap bitmap){

		if(bitmap != null && !bitmap.isRecycled()){

			byte[] chunk = bitmap.getNinePatchChunk();

			if(chunk != null && NinePatch.isNinePatchChunk(chunk)){

				Drawable nineDrawable = new NinePatchDrawable(view.getContext().getResources(), bitmap, chunk, new Rect(), null);

				view.setBackgroundDrawable(nineDrawable);

				if( !mDrawableMap.containsKey(url) ||
                    !mDrawableMap.get(url).equals(nineDrawable) ){
					mDrawableMap.put(url,nineDrawable);
				}
			}
            else
            {
				Drawable drawable = new BitmapDrawable(bitmap);

				view.setBackgroundDrawable(drawable);

				if( !mDrawableMap.containsKey(url) ||
                    !mDrawableMap.get(url).equals(drawable) ){
					mDrawableMap.put(url,drawable);
				}
			}

		}
	}
}