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

import android.graphics.Bitmap;

import com.tencent.rapidview.deobfuscated.IRapidView;

import org.luaj.vm2.LuaFunction;

/**
 * @Class LuaJavaShare
 * @Desc 分享类实现
 *
 * @author arlozhang
 * @date 2017.03.08
 */
public class LuaJavaShare extends RapidLuaJavaObject {

    public LuaJavaShare(String rapidID, IRapidView rapidView) {
        super(rapidID, rapidView);
    }

    public void shareImageToWX(Bitmap bmp, String scene, final LuaFunction succeedListener, final LuaFunction failedListener){
        int nScene;

        if( scene == null || scene.compareToIgnoreCase("session") == 0 ){
            nScene = 0;
        }
        else if( scene.compareToIgnoreCase("timeline") == 0 ){
            nScene = 1;
        }
        else if( scene.compareToIgnoreCase("favorite") == 0 ){
            nScene = 2;
        }
        else{
            nScene = 0;
        }

//        WXShareUtil.shareImageToWX(bmp, nScene, new IWXShareCallback() {
//            @Override
//            public void onWXShareFinshed(int errCode, String errMsg) {
//                if( errCode == 0 ){
//                    if( succeedListener != null ){
//                        RapidLuaCaller.getInstance().call(succeedListener);
//                    }
//                }
//                else{
//                    if( failedListener != null ){
//                        RapidLuaCaller.getInstance().call(failedListener, errCode, errMsg);
//                    }
//                }
//            }
//        });
    }


    public void shareTextToWX(String text, String scene, final LuaFunction succeedListener, final LuaFunction failedListener){
        int nScene;

        if( scene == null || scene.compareToIgnoreCase("session") == 0 ){
            nScene = 0;
        }
        else if( scene.compareToIgnoreCase("timeline") == 0 ){
            nScene = 1;
        }
        else if( scene.compareToIgnoreCase("favorite") == 0 ){
            nScene = 2;
        }
        else{
            nScene = 0;
        }

//        WXShareUtil.shareTextToWX(text, nScene, new IWXShareCallback() {
//            @Override
//            public void onWXShareFinshed(int errCode, String errMsg) {
//                if( errCode == 0 ){
//                    if( succeedListener != null ){
//                        RapidLuaCaller.getInstance().call(succeedListener);
//                    }
//                }
//                else{
//                    if( failedListener != null ){
//                        RapidLuaCaller.getInstance().call(failedListener, errCode, errMsg);
//                    }
//                }
//            }
//        });
    }
}
