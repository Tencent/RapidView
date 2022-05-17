package com.tencent.rapidview.utils;
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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.framework.RapidPool;
import com.tencent.rapidview.framework.RapidResource;

/**
 * @Class RapidImageLoader
 * @Desc 读取图片资源的类，本地图片资源按照先res，后assets的顺序。
 *       本地资源以res@开头，如果没有这个头，则默认以先服务端资源，
 *       后本地资源的顺序读取。
 *
 *       一般apk接入本模块时，图片资源尽量不要放到assets下面，之所
 *       以也把assets列入读取范围，是考虑SDK接入时的情况。
 *
 * @author arlozhang
 * @date 2016.04.22
 */
public class RapidImageLoader {

    public interface ICallback{
        void finish(boolean succeed, String name, Bitmap bmp);
    }

    public static Bitmap get(Context context, String name, String rapidID, boolean limitLevel){
        Bitmap bmp = null;
        boolean isRes = false;
        String realName = name;

        if( context == null || name == null){
            return null;
        }

        if( realName.length() >= 4 && realName.substring(0, 4).compareToIgnoreCase("res@") == 0 ){
            realName = realName.substring(4, realName.length());
            isRes =  true;
        }

        if( RapidConfig.DEBUG_MODE && FileUtil.isFileExists(FileUtil.getRapidDebugDir() + realName) ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从调试目录读取资源：" + realName);
            byte[] bytesBmp = RapidFileLoader.getInstance().getBytes(name, RapidFileLoader.PATH.enum_debug_path);

            bmp = bytesToBmp(bytesBmp);

            if( bmp != null ){
                return bmp;
            }
        }

        if( limitLevel && realName.contains("../") ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "路径有异常：" + realName);
            return bmp;
        }

        if( !RapidStringUtils.isEmpty(rapidID) ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从沙箱目录读取资源：" + realName);
            byte[] bytesBmp = RapidFileLoader.getInstance().getBytes(rapidID + "/" + name, RapidFileLoader.PATH.enum_sandbox_path);

            bmp = bytesToBmp(bytesBmp);

            if( bmp != null ){
                return bmp;
            }
        }

        if( limitLevel ){
            return bmp;
        }

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从RapidView目录读取资源：" + realName);
        bmp = getRapid(context, realName);

        if( bmp != null ){
            return bmp;
        }

        if( isRes ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从res中读取资源：" + realName);
            bmp = getResource(context, realName);

            if( bmp != null ){
                return bmp;
            }
        }

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从assets中读取资源：" + realName);
        bmp = RapidAssetsLoader.getInstance().getBitmap(context, realName);

        return bmp;
    }

    public static void get(final Context context, final String name, final String rapidID, final boolean limitLevel, final ICallback callback){
        if( context == null || name == null || name.compareTo("") == 0 || callback == null ){
            return;
        }

        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {
                String realName = name;
                Bitmap bmp = null;
                boolean isRes = false;


                if( realName.length() >= 4 && realName.substring(0, 4).compareToIgnoreCase("res@") == 0 ){
                    realName = realName.substring(4, realName.length());
                    isRes =  true;
                }

                if( RapidConfig.DEBUG_MODE && FileUtil.isFileExists(FileUtil.getRapidDebugDir() + realName) ){
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从调试目录读取资源：" + realName);
                    byte[] bytesBmp = RapidFileLoader.getInstance().getBytes(name, RapidFileLoader.PATH.enum_debug_path);

                    bmp = bytesToBmp(bytesBmp);

                    if( bmp != null ){
                        postBmp(true, realName, bmp, callback);
                        return;
                    }
                }

                if( limitLevel && realName.contains("../") ){
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "路径有异常：" + realName);
                    postBmp(false, realName, bmp, callback);
                    return;
                }

                if( !RapidStringUtils.isEmpty(rapidID) ){
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从沙箱目录读取资源：" + realName);
                    byte[] bytesBmp = RapidFileLoader.getInstance().getBytes(rapidID + "/" + name, RapidFileLoader.PATH.enum_sandbox_path);

                    bmp = bytesToBmp(bytesBmp);

                    if( bmp != null ){
                        postBmp(true, realName, bmp, callback);
                        return;
                    }
                }

                if( limitLevel ){
                    postBmp(false, realName, bmp, callback);
                    return;
                }

                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从RapidView目录读取资源：" + realName);
                bmp = getRapid(context, realName);

                if( bmp != null ){
                    postBmp(true, realName, bmp, callback);
                    return;
                }

                if( isRes ){
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从res中读取资源：" + realName);
                    bmp = getResource(context, realName);

                    if( bmp != null ){
                        postBmp(true, realName, bmp, callback);
                        return;
                    }
                }

                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从assets中读取资源：" + realName);
                bmp = RapidAssetsLoader.getInstance().getBitmap(context, realName,
                        new RapidAssetsLoader.LoadCallback(){

                            @Override
                            public void loadFinish(boolean succeed, String url, final Bitmap bmp) {
                                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "从assets中读取" + "url" + "资源完毕，结果："
                                        + Boolean.toString(succeed));

                                callback.finish(succeed, url, bmp);
                            }

                        });

                if( bmp != null ){
                    postBmp(true, realName, bmp, callback);
                }
            }
        });
    }

    public static Bitmap get(final Context context, final String name){
        String realName = name;
        Bitmap bmp;

        if( context == null || name == null ){
            return null;
        }

        if( realName.length() <= 4 || realName.substring(0, 4).compareToIgnoreCase("res@") != 0 ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从RapidView目录读取资源：" + realName);
            bmp = getRapid(context, realName);

            if( bmp != null ){
                return bmp;
            }
        }
        else{
            realName = realName.substring(4, realName.length());
        }

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从res中读取资源：" + realName);
        bmp = getResource(context, realName);

        if( bmp != null ){
            return bmp;
        }

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试从assets中读取资源：" + realName);
        bmp = RapidAssetsLoader.getInstance().getBitmap(context, realName);

        return bmp;
    }

    private static void postBmp(final boolean succeed, final String name, final Bitmap bitmap, final ICallback callback){
        if( callback == null ){
            return;
        }

        RapidAssetsLoader.getInstance().getImageHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.finish(succeed, name, bitmap);
            }
        });
    }

    private static Bitmap getResource(Context context, String name){
        int resID;

        if( context == null || name == null ){
            return null;
        }

        if( name.contains(".") ){
            name = name.substring(0, name.indexOf("."));
        }

        try {
            resID = RapidResource.mResourceMap.get(name);

        } catch (Exception e) {
            resID = 0;
        }

        if( resID == 0 ){
            XLog.d(RapidConfig.RAPID_NORMAL_TAG, name + "从res中没有拿到图片资源");
            return null;
        }

        return BitmapFactory.decodeResource(context.getResources(), resID);
    }

    private static Bitmap getRapid(Context context, String name){
        byte[] bytesBitmap = RapidPool.getInstance().getFile(name, false);
        Bitmap bmp;

        if( bytesBitmap == null ){
            return null;
        }

        try {
            bmp = BitmapFactory.decodeByteArray(bytesBitmap, 0, bytesBitmap.length);
        }
        catch (Exception e) {
            e.printStackTrace();
            bmp = null;
        }

        return bmp;
    }

    private static Bitmap bytesToBmp(byte[] bytes){
        Bitmap bmp = null;

        if( bytes == null ){
            return null;
        }

        try {
            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        catch (Exception e) {
            e.printStackTrace();
            bmp = null;
        }

        return bmp;
    }
}
