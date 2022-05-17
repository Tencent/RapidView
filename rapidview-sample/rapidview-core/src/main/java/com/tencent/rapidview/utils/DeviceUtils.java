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


import java.lang.reflect.Method;

import android.os.Build;

public class DeviceUtils {

	public static boolean hasSmartBar() {
		try {
			// 新型号可用反射调用Build.hasSmartBar()
			Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
			return ((Boolean) method.invoke(null)).booleanValue();
		} catch (Exception e) {
		}
		// 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
		if (Build.DEVICE.equals("mx2")) {
			return true;
		} else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
			return false;
		}
		return false;
	}
}
