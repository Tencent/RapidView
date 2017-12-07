package com.tencent.rapidview.utils;

import android.os.Build;

/**
 * @Class DeviceQualityUtils
 * @Desc 根据系统信息推断手机是低端中端还是高端，差不多隔两年维护一下就可以
 *
 * @author arlozhang
 * @date 2017.11.29
 */

public class DeviceQualityUtils {

    private static DEVICE_QUALITY mDeviceQuality = DEVICE_QUALITY.enum_none;

    public enum DEVICE_QUALITY{
        enum_none,
        enum_low_quality,
        enum_middum_quality,
        enum_high_quality,
    }

    public static DEVICE_QUALITY getDeviceQuality(){
        if( mDeviceQuality != DEVICE_QUALITY.enum_none ){
            return mDeviceQuality;
        }

        if( Build.VERSION.SDK_INT <= 18 ){
            mDeviceQuality = DEVICE_QUALITY.enum_low_quality;
        }
        else if( Build.VERSION.SDK_INT > 18 && Build.VERSION.SDK_INT <= 22 ){
            mDeviceQuality = DEVICE_QUALITY.enum_middum_quality;
        }
        else {
            mDeviceQuality = DEVICE_QUALITY.enum_high_quality;
        }

        return  mDeviceQuality;
    }
}
