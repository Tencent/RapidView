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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.tencent.rapidview.data.Var;


import java.io.File;
import java.util.Map;

public class PhotoUtils {

    public static final int INTENT_CODE_ALBUM = 1001;    // 打开相册
    public static final int INTENT_CODE_CAMERA = 1002;   // 打开相机
    private static String cameraImageUriFileName;
    private Activity mContext = null;
    private OnPhotoSelectListener mPhotoSelectListener = null;

    public Map<String, Var> mParams = null;

    public void init(Context context,OnPhotoSelectListener photoSelectListener) {
        mContext = (Activity) context;
        if(photoSelectListener!=null) {
            mPhotoSelectListener = photoSelectListener;
        }
    }

    public void openCamera(Context context, Map<String, Var> params) {

        if( ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        else {
            mParams = params;
            reallyCapture();

        }
    }


    public interface OnPhotoSelectListener {
        void onPhotoSelectedSucceed(String fileName);
        void onPhotoSelectedFailed(int errorCode);
    }

    /**
     * 拍照
     */
    private void reallyCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String directory = Environment.getExternalStorageDirectory().getPath() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator;
        File directoryFile = new File(directory);
        if (!directoryFile.exists()) {
            directoryFile.mkdirs();
        }
        cameraImageUriFileName = directory + Long.toString(System.currentTimeMillis()) + ".jpg";
        File tempFile = new File(cameraImageUriFileName);
        Uri picUri = Uri.fromFile(tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);

        if( mParams != null ){
            for( Map.Entry<String, Var> entry : mParams.entrySet() ){
                if( entry.getValue().getType() == Var.TYPE.enum_boolean ){
                    intent.putExtra(entry.getKey(), entry.getValue().getBoolean());
                }
                else if( entry.getValue().getType() == Var.TYPE.enum_float ||
                        entry.getValue().getType() == Var.TYPE.enum_double ){
                    intent.putExtra(entry.getKey(), entry.getValue().getFloat());
                }
                else if( entry.getValue().getType() == Var.TYPE.enum_int  ){
                    intent.putExtra(entry.getKey(), entry.getValue().getInt());
                }
                else if( entry.getValue().getType() == Var.TYPE.enum_long  ){
                    intent.putExtra(entry.getKey(), entry.getValue().getLong());
                }
                else if( entry.getValue().getType() == Var.TYPE.enum_string  ){
                    intent.putExtra(entry.getKey(), entry.getValue().getString());
                }
            }
        }

        mContext.startActivityForResult(intent, INTENT_CODE_CAMERA);
    }


    /**
     * 从Acitivity转发过来的ActivityResult方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean isFromThisView = requestCode == INTENT_CODE_CAMERA || requestCode == INTENT_CODE_ALBUM;
        if (isFromThisView && resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_CODE_ALBUM && data != null) {
                Uri uri = data.getData();

                String path = Uri2Path.getPath(mContext, uri);

                if(!TextUtils.isEmpty(path)) {
                    responsePhotoSelected(path);
                }
                else{
                    responsePhotoFailed(-1);
                }
            }
            else if (requestCode == INTENT_CODE_CAMERA) {
                try {
                    if (cameraImageUriFileName==null){
                        responsePhotoFailed(-2);
                        return isFromThisView;
                    }

                    responsePhotoSelected(cameraImageUriFileName);
                } catch (Exception e) {
                    e.printStackTrace();
                    responsePhotoFailed(-3);
                }

            }
        }
        else{
            responsePhotoFailed(-4);
        }

        return isFromThisView;
    }

    private void responsePhotoSelected(String fileName) {

        if( mPhotoSelectListener == null ){
            return;
        }

        if ( !TextUtils.isEmpty(fileName) ) {
            mPhotoSelectListener.onPhotoSelectedSucceed(fileName);
        } else {
            mPhotoSelectListener.onPhotoSelectedFailed(-5);
        }
    }

    private void responsePhotoFailed(int errorcode) {
        if (mPhotoSelectListener != null && errorcode < 0) {
            mPhotoSelectListener.onPhotoSelectedFailed(errorcode);
        }
    }
}
