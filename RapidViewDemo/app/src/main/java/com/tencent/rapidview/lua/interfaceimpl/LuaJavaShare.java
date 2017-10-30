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
