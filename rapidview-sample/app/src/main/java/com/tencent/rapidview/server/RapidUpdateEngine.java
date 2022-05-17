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
package com.tencent.rapidview.server;

import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.utils.XLog;

/**
 * @Class RapidUpdateEngine
 * @Desc 更新文件后台接口。
 *
 * @author arlozhang
 * @date 2015.10.14
 */
public class RapidUpdateEngine {

    public static RapidUpdateEngine msInstance;

    public static synchronized RapidUpdateEngine getInstance(){
        if(msInstance == null){
            msInstance = new RapidUpdateEngine();
        }
        return msInstance;
    }


    public synchronized int sendRequest(){
        return -1;
    }


    protected void onRequestFailed(final int seq, final int errorCode, Object request, Object response) {
        XLog.d(RapidConfig.RAPID_ERROR_TAG, "协议请求失败，errorcode：" + Integer.toString(errorCode));
    }


    protected synchronized void onRequestSuccessed(int seq, Object request, Object response) {
    }
}
