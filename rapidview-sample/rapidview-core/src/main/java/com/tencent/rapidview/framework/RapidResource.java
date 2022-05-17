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


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidResource
 * @Desc 由于res里面的资源经过混淆后无法反射读到，因此这里将需要的资源配置成静态ID，供配置读取
 *
 * @author arlozhang
 * @date 2016.05.07
 */
public class RapidResource {

    public static final Map<String, Integer> mResourceMap = new ConcurrentHashMap<String, Integer>();

    static {
        //mResourceMap.put("pic_defaule", R.drawable.pic_defaule);
    }
}
