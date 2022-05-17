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

import com.tencent.rapidview.lua.RapidLuaLoader;
import com.tencent.rapidview.parser.ImageViewParser;
import com.tencent.rapidview.parser.LinearLayoutParser;
import com.tencent.rapidview.parser.RelativeLayoutParser;
import com.tencent.rapidview.parser.TextViewParser;
import com.tencent.rapidview.parser.ViewParser;
import com.tencent.rapidview.utils.RapidThreadPool;
import com.tencent.rapidview.utils.RapidXmlViewer;
import com.tencent.rapidviewdemo.DemoApplication;

/**
 * @Class RapidInitializer
 * @Desc 初始化操作
 *
 * @author arlozhang
 * @date 2018.04.22
 */
public class RapidInitializer {

    private static boolean msIsInitialize = false;
    public static void initialize(Context context){

        if( msIsInitialize ){
            return;
        }

        msIsInitialize = true;

        RapidPool.getInstance().initialize(context, null);
        staticInitialize();
        customerInitialize();
    }

    private static void staticInitialize(){
        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {
                RapidLuaLoader.getInstance();
                RapidChooser.getInstance();
                RapidXmlViewer.getInstance().initialize(DemoApplication.getInstance());

                new ViewParser();
                new RelativeLayoutParser();
                new LinearLayoutParser();
                new TextViewParser();
                new ImageViewParser();
            }
        });
    }
    private static void customerInitialize(){
        }
}
