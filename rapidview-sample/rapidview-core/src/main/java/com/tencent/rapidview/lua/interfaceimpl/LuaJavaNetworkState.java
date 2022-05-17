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

import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.NetworkUtil;

/**
 * @Class LuaJavaNetworkState
 * @Desc 网络状态判断
 *
 * @author arlozhang
 * @date 2017.03.31
 */
public class LuaJavaNetworkState extends RapidLuaJavaObject {

    public LuaJavaNetworkState(String rapidID, IRapidView rapidView){
        super(rapidID, rapidView);
    }

    public boolean isNetworkActive(){
        return NetworkUtil.isNetworkActive();
    }

    public boolean isWap(){
        return NetworkUtil.isWap();
    }

    public boolean isWifi(){
        return NetworkUtil.isWifi();
    }

    public boolean is2G(){
        return NetworkUtil.is2G();
    }

    public boolean is3G(){
        return NetworkUtil.is3G();
    }

    public boolean is4G(){
        return NetworkUtil.is4G();
    }
}
