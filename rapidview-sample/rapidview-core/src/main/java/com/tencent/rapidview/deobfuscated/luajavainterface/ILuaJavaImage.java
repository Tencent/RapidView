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
package com.tencent.rapidview.deobfuscated.luajavainterface;

import android.graphics.Bitmap;

import com.tencent.rapidview.deobfuscated.IBytes;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;

/**
 * @Class ILuaJavaImage
 * @Desc Lua调用java的图像类的能力接口
 *
 * @author arlozhang
 * @date 2017.03.03
 */
public interface ILuaJavaImage {

    /**
     * 拍照接口
     *
     * @param params           参数接口:size_type(图片尺寸模式 默认为压缩图，original/原图;compressed/压缩图);
     * @param succeedListener  成功的回调，参数(IBytes pic)
     * @param failedListener   失败回调，参数()
     */
    void takePicture(LuaTable params, LuaFunction succeedListener, LuaFunction failedListener);

    /**
     * 选择图片接口
     *
     * @param params             (count:图片数量|size_type:图片尺寸模式 默认为压缩图，original/原图;compressed/压缩图)
     * @param succeedListener    成功的回调，参数(table arrayPic)
     * @param failedListener     失败的回调，参数()
     */
    void choosePicture(LuaTable params, LuaFunction succeedListener, LuaFunction failedListener);

    /**
     * 字节流转换为图片
     * @param bytes 字节流
     * @return
     */
    Bitmap getBitmapFromBytes(IBytes bytes);

    /**
     * 图片转换为字节流
     * @param bitmap 图片
     * @return
     */
    IBytes getBytesFromBitmap(Bitmap bitmap);

    /**
     * 保存本地图片
     * @param bitmap
     */
    void savePicture(Bitmap bitmap);
}
