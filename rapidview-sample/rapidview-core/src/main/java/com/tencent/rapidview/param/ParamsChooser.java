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
package com.tencent.rapidview.param;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class ParamsChooser
 * @Desc 字符串与params类Class转换
 *
 * @author arlozhang
 * @date 2017.07.07
 */

public class ParamsChooser {

    private static Map<String, Class> mParamsClassMap = new ConcurrentHashMap<String, Class>();

    static {
        try{
            mParamsClassMap.put("abslistviewlayoutparams", AbsListViewLayoutParams.class);
            mParamsClassMap.put("absolutelayoutparams", AbsoluteLayoutParams.class);
            mParamsClassMap.put("framelayoutparams", FrameLayoutParams.class);
            mParamsClassMap.put("linearlayoutparams", LinearLayoutParams.class);
            mParamsClassMap.put("marginparams", MarginParams.class);
            mParamsClassMap.put("relativelayoutparams", RelativeLayoutParams.class);
            mParamsClassMap.put("viewgroupparams", ViewGroupParams.class);
            mParamsClassMap.put("viewpagerparams", ViewPagerParams.class);
            mParamsClassMap.put("recyclerviewlayoutparams", RecyclerViewLayoutParams.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Class getParamsClass(String clazzName){
        Class clz = null;

        if( clazzName == null ){
            return RelativeLayoutParams.class;
        }

        clazzName.toLowerCase();

        clz = mParamsClassMap.get(clazzName);
        if( clz == null ){
            clz = RelativeLayoutParams.class;
        }

        return clz;
    }
}
