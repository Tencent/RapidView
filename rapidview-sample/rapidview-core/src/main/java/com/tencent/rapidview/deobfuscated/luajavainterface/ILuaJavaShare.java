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

import org.luaj.vm2.LuaFunction;

/**
 * @Class ILuaJavaShare
 * @Desc Lua调用java的分享类接口
 *
 * @author arlozhang
 * @date 2017.03.08
 */
public interface ILuaJavaShare {

    /**
     * 分享图片到微信
     * @param bmp 准备分享的图片
     * @param scene 分享的场景，默认聊天界面， 聊天界面：session/朋友圈：timeline/添加到微信收藏：favorite
     * @param succeedListener 成功的回调参数列表:()
     * @param failedListener  失败的回调：参数列表:(errorCode, errorMessage)
     *                                int ERR_COMM         = -1;
     *                                int ERR_USER_CANCEL  = -2;
     *                                int ERR_SENT_FAILED  = -3;
     *                                int ERR_AUTH_DENIED  = -4;
     *                                int ERR_UNSUPPORT    = -5;
     *                                int ERR_UNINSTALL_WX = -6;
     *                                int ERR_UNKNOWN      = -7;
     */
    void shareImageToWX(Bitmap bmp, String scene, LuaFunction succeedListener, LuaFunction failedListener);


    /**
     * 分享文本到微信
     * @param text 准备分享的文本
     * @param scene 分享的场景，默认朋友圈， 聊天界面：session/朋友圈：timeline/添加到微信收藏：favorite
     * @param succeedListener 成功的回调参数列表:()
     * @param failedListener  失败的回调：参数列表:(errorCode)
     *                                int ERR_COMM         = -1;
     *                                int ERR_USER_CANCEL  = -2;
     *                                int ERR_SENT_FAILED  = -3;
     *                                int ERR_AUTH_DENIED  = -4;
     *                                int ERR_UNSUPPORT    = -5;
     *                                int ERR_UNINSTALL_WX = -6;
     *                                int ERR_UNKNOWN      = -7;
     */
    void shareTextToWX(String text, String scene, LuaFunction succeedListener, LuaFunction failedListener);
}
