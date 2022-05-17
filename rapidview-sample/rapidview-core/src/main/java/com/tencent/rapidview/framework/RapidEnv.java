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
package com.tencent.rapidview.framework;

import android.content.Context;

/**
 * @Class RapidEnv
 * @Desc 用于存储外部全局环境数据
 *
 * @author arlozhang
 * @date 2017.06.29
 */
public class RapidEnv {

    private static Context mApplication = null;

    public static void setApplication(Context context){
        mApplication = context;
    }

    public static Context getApplication(){
        return mApplication;
    }
}
