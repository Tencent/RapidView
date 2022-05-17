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

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @Class LuaJavaNetwork
 * @Desc 网络相关操作
 *
 * @author arlozhang
 * @date 2017.04.27
 */
public class LuaJavaNetwork extends RapidLuaJavaObject {

    public LuaJavaNetwork(String rapidID, IRapidView rapidView){
        super(rapidID, rapidView);
    }


    public static String urlDecode(String url){
        try{
            return URLDecoder.decode(url, "UTF-8");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return url;
    }


    public static String urlEncode(String url){
        try{
            return URLEncoder.encode(url, "UTF-8");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return url;
    }
}
