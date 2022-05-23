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
 * @Class RapidConfig
 * @Desc RapidView配置文件，配置皮肤引擎的各项参数。
 *
 * @author arlozhang
 * @date 2016.04.28
 */
public class RapidConfig {

    public final static Map<String, String> msMapViewNaitve = new ConcurrentHashMap<String, String>();

    public final static Map<String, UserViewConfig.IFunction> msMapUserView = new ConcurrentHashMap<String, UserViewConfig.IFunction>();

    public final static String RAPID_NORMAL_TAG = "RAPID_ENGINE_NORMAL";

    public final static String RAPID_ERROR_TAG = "RAPID_ENGINE_ERROR";

    public final static String RAPID_BENCHMARK_TAG = "RAPID_ENGINE_BENCHMARK";

    public final static String RAPID_TASK_TAG = "RAPID_ENGINE_TASK";

/**************************************************************************************************/

    /**DEBUG_MODE
     * 调试模式是否开启，一般发布时需要关闭。开启调试模式后，可以在rapiddebug目录中配置调试文件。调试文件实
     * 时，并以最高优先级生效。强烈推荐使用调试小工具RapidStudio Studio调试界面，调试时需要安装开启调试模
     * 式的包。**/
    public final static boolean DEBUG_MODE = false;

    /**
     * 测试开关，开启测试开关，具有打印log信息的作用
     */
    public final static boolean TEST_MODE = false;

    /**OUTPUT_INFORMATION_MODE
     * 该参数开启时，可通过Rapid Studio获取APK包相关信息。
     */
    public final static boolean OUTPUT_INFORMATION_MODE = DEBUG_MODE;

    /**VIEW列表，此处配置仅为防止重名，便于索引。**/
    public enum VIEW{
        native_demo_view, //Demo视图
        demo_card_1,
        demo_card_2,
        demo_card_3,
    }

    /** VIEW和NaitveXML的映射关系，当View不存在服务端下发的XML时，寻找本地XML作为默认布局 **/
    static{
        try{
            msMapViewNaitve.put(RapidConfig.VIEW.native_demo_view.toString(), "demo_view.xml");
            msMapViewNaitve.put(RapidConfig.VIEW.demo_card_1.toString(), "demo_card_1.xml");
            msMapViewNaitve.put(RapidConfig.VIEW.demo_card_2.toString(), "demo_card_2.xml");
            msMapViewNaitve.put(RapidConfig.VIEW.demo_card_3.toString(), "demo_card_3.xml");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    /**userview使用的native视图。Native类需要实现参数为（Context context）的构造函数.**/
    static{
        try{
            //msMapUserView.put("normalerrorrecommendpage", UserViewConfig.NormalErrorRecommendPageGeter.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
