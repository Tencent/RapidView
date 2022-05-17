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
package com.tencent.rapidview.lua.interfaceimpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IBytes;
import com.tencent.rapidview.deobfuscated.IRapidParser;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.lua.RapidLuaCaller;
import com.tencent.rapidview.utils.BitmapUtil;
import com.tencent.rapidview.utils.PhotoUtils;
import com.tencent.rapidview.utils.RapidThreadPool;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class LuaJavaPicture
 * @Desc 图片选取类
 *
 * @author arlozhang
 * @date 2017.03.04
 */
public class LuaJavaPicture extends RapidLuaJavaObject implements PhotoUtils.OnPhotoSelectListener {

    private PhotoUtils mPhotoUtils = new PhotoUtils();

    private LuaFunction mSucceedListener = null;

    private LuaFunction mFailedListener = null;

    private IRapidParser parser = null;

    public LuaJavaPicture(String rapidID, IRapidView rapidView){
        super(rapidID, rapidView);
        parser = getParser();
        if(parser == null) {
            return;
        }

        Context activityContext = parser.getContext();
        if(activityContext instanceof Activity) {
            mPhotoUtils.init(activityContext,this);
        }
    }

    public void takePicture(LuaTable params, LuaFunction succeedListener, LuaFunction failedListener){
        Map<String, Var> map = null;
        LuaValue key = LuaValue.NIL;
        LuaValue value = LuaValue.NIL;

        mSucceedListener = succeedListener;
        mFailedListener = failedListener;

        if( params != null && params.istable() ){
            map = new ConcurrentHashMap<String, Var>();

            while(true){
                Varargs argsItem = params.next(key);
                key = argsItem.arg1();

                if( key.isnil() ){
                    break;
                }

                value = argsItem.arg(2);

                if( key.isstring() && value != null ){
                    if( value.isboolean() ){
                        map.put(key.toString(), new Var(value.toboolean()));
                    }
                    else if( value.isint() ){
                        map.put(key.toString(), new Var(value.toint()));
                    }
                    else if( value.islong() ){
                        map.put(key.toString(), new Var(value.tolong()));
                    }
                    else{
                        map.put(key.toString(), new Var(value.tostring()));
                    }
                }
            }
        }

        if(mPhotoUtils != null) {
            mPhotoUtils.openCamera(parser.getContext(), map);
        }

    }

    public void choosePicture(LuaTable params, LuaFunction succeedListener, LuaFunction failedListener){
        mSucceedListener = succeedListener;
        mFailedListener = failedListener;

    }

    public void savePicture(Bitmap bitmap){
        if(bitmap != null) {
            //BitmapUtil.saveImagetoLocalAndToast(bitmap);
        }
    }

    public Bitmap getBitmapFromBytes(IBytes bytes){
        if( bytes == null || bytes.getArrayByte() == null ){
            return null;
        }

        return BitmapFactory.decodeByteArray(bytes.getArrayByte(), 0, bytes.getArrayByte().length);
    }


    public IBytes getBytesFromBitmap(Bitmap bitmap){
        if( bitmap == null ){
            return  null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return new Bytes(baos.toByteArray());

    }

    @Override
    public void notify(IRapidParser.EVENT event, StringBuilder ret, Object... args) {
        super.notify(event, ret, args);
        switch (event){
            case enum_onactivityresult:
                mPhotoUtils.onActivityResult((int)args[0],(int)args[1],(Intent)args[2]);
                mUnRegister = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onPhotoSelectedSucceed(final String fileName) {
        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {
                Bitmap thumb = BitmapFactory.decodeFile(fileName);

                // 如果图片被旋转，则需要转回来
                Bitmap rotateBm = rotateBitmapByDegree(thumb,getBitmapDegree(fileName));
                //做缩放，然后压缩，维持在32KB标准内
                int WX_THUMB_SIZE = rotateBm.getWidth() >= 650 ? 650 : rotateBm.getWidth();
                int WX_THUMB_HEIGHT = WX_THUMB_SIZE * rotateBm.getHeight() / rotateBm.getWidth();
                Bitmap cropThumb = Bitmap.createScaledBitmap(rotateBm, WX_THUMB_SIZE, WX_THUMB_HEIGHT, true);

                if(rotateBm != null) {
                    final IBytes imageBytes = new Bytes(BitmapUtil.compressImageAndGetBytes(cropThumb, 65));
                    if( mSucceedListener != null ){
                        parser.getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                RapidLuaCaller.getInstance().call(mSucceedListener, CoerceJavaToLua.coerce(imageBytes));
                            }
                        });

                    }
                }
            }
        });

    }

    public void onPhotoSelectedFailed(final int errorCode) {
        if( mFailedListener != null ){
            RapidLuaCaller.getInstance().call(mFailedListener, errorCode);
        }
    }


    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm
     *            需要旋转的图片
     * @param degree
     *            旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }
}
