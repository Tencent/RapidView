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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.tencent.rapidview.framework.RapidConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Assets资源管理
 * 
 * */
public class RapidAssetsLoader {

    private static String RAPID_DIR = "rapidview/";
	
	private static RapidAssetsLoader sInstance;
	
	private ConcurrentHashMap<String, LoadCallback> mCallbackMap = new ConcurrentHashMap<String, LoadCallback>();
	private static Handler mImageHandler = null;
	private static Object lock = new Object();
	

	public interface LoadCallback{
		void loadFinish(boolean succeed, String url, Bitmap bmp);
	}

	public RapidAssetsLoader(){
	}
	
	public static synchronized RapidAssetsLoader getInstance(){
		if(sInstance == null){
			sInstance = new RapidAssetsLoader();
		}
		return sInstance;
	}

    public byte[] get(Context context, String url){
        byte[] content = null;
        InputStream is = null;

        if( context == null ){
            return null;
        }

        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            is = context.getAssets().open(RAPID_DIR + url);
            byte[] buff = new byte[100];
            int rc = 0;

            while ((rc = is.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }

            content = swapStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            Closer.close(is);
            is = null;
            content = null;
        }
        finally {
            try {
                if( is != null ){
                    is.close();
                }
            } catch ( Exception e){
                e.printStackTrace();
            }
        }

        return content;
    }

    public boolean isFileExist(Context context, String url){
        InputStream is = null;

        if( context == null ){
            return false;
        }

        try {
            is = context.getAssets().open(RAPID_DIR + url);

        } catch (IOException e) {
            e.printStackTrace();
            Closer.close(is);
            is = null;
        }
        finally {
            try {
                if( is != null ){
                    is.close();
                }
            } catch ( Exception e){
                e.printStackTrace();
            }
        }

        return is == null ? false : true;
    }

	public Bitmap getBitmap(final Context context, final String imageUrl, LoadCallback callback){
        Handler handler;
        Bitmap bitmap = getThumbnailByAsset(context, imageUrl);

		if(bitmap != null && !bitmap.isRecycled()){
			return bitmap;
		}

        handler = getImageHandler();
        if(handler == null){
            return null;
        }

        mCallbackMap.put(imageUrl, callback);

        handler.post(new Runnable() {
            @Override
            public void run() {
                final LoadCallback callback;
                final Bitmap bitmap = getThumbnailByAsset(context, imageUrl);

                callback = mCallbackMap.remove(imageUrl);
                if(callback == null){
                    return;
                }

                HandlerUtils.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        boolean succeed = true;

                        if(bitmap == null || bitmap.isRecycled()){
                            succeed = false;
                        }

                        callback.loadFinish(succeed, imageUrl,bitmap);
                    }
                });
            }
        });

		return null;
	}

    public Bitmap getBitmap(final Context context, final String imageUrl){
        Handler handler;
        Bitmap bitmap = getThumbnailByAsset(context, imageUrl);

        if(bitmap != null && !bitmap.isRecycled()){
            return bitmap;
        }

        handler = getImageHandler();
        if(handler == null){
            return null;
        }

        bitmap = getThumbnailByAsset(context, imageUrl);

        return bitmap;
    }

    private Bitmap getThumbnailByAsset(Context context, String url){
        Bitmap b = null;
        InputStream is = null;

        if( context == null ){
            return null;
        }

        if( url == null ){
            return null;
        }

        try {
            is = context.getAssets().open(RAPID_DIR + url);
            b = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "读取文件失败：" + url);
            e.printStackTrace();
            Closer.close(is);
            return b;
        }
        return b;
    }
	
	public Drawable getDrawable(Context context, final String imageUrl){
		Drawable drawable = null;
		InputStream is = null;

        if( context == null ){
            return null;
        }

		try {
			is = context.getAssets().open(RAPID_DIR + imageUrl);
			drawable = new BitmapDrawable(BitmapFactory.decodeStream(is));
			is.close();
		} catch (IOException e) {
            e.printStackTrace();
			Closer.close(is);
			return drawable;
		}
		return drawable;
	}
	
	public synchronized Handler getImageHandler() {
		synchronized (lock) {
			if (mImageHandler == null) {
				mImageHandler = HandlerUtils.getMainHandler();
			}
			return mImageHandler;
		}
	}
}
