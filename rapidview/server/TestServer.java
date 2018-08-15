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

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.utils.RapidThreadPool;

import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class TestServer
 * @Desc  本地构造一个假的网络请求，提供假数据
 *
 * @author arlozhang
 * @date 2017.11.02
 */
public class TestServer {

    public interface IListener {

        void onFinish(boolean bSucc, List<String> viewNameList, List<Map<String, Var>> dataList);
    }

    public TestServer(){
        initTestData();
    }

    public void request(final int cmdID, final LuaTable data, final IListener listener){

        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {
                onRequestSucceed(cmdID, data, listener);
            }

        }, 150);
    }

    private void onRequestSucceed(int cmdID, LuaTable data, IListener listener){
        switch (cmdID){
            case 1:
                listener.onFinish(true, mTestReqList1, mTestReqMap1);
                break;
            case 2:
                listener.onFinish(true, mTestReqList2, mTestReqMap2);
                break;
            case 3:
                listener.onFinish(true, mTestReqList3, mTestReqMap3);
                break;
            case 4:
                listener.onFinish(true, mTestReqList4, mTestReqMap4);
                break;
            case 5:
                listener.onFinish(true, mTestReqList5, mTestReqMap5);
                break;
            default:
                listener.onFinish(true, mTestReqList5, mTestReqMap5);
        }
    }

    private void initTestData(){
        mTestReqList1.add("tab_data");
        mTestReqList1.add("demo_card_1");
        mTestReqList1.add("demo_card_2");
        mTestReqList1.add("demo_card_3");
        mTestReqList1.add("demo_card_2");
        mTestReqList1.add("demo_card_1");
        mTestReqList1.add("demo_card_2");
        mTestReqList1.add("demo_card_2");
        mTestReqList1.add("demo_card_3");
        mTestReqList1.add("demo_card_2");
        mTestReqList1.add("demo_card_2");
        mTestReqList1.add("demo_card_1");


        mTestReqMap1.add(getTabData());
        mTestReqMap1.add(getReqMap1());
        mTestReqMap1.add(getReqMap2());
        mTestReqMap1.add(getReqMap3());
        mTestReqMap1.add(getReqMap2());
        mTestReqMap1.add(getReqMap1());
        mTestReqMap1.add(getReqMap2());
        mTestReqMap1.add(getReqMap2());
        mTestReqMap1.add(getReqMap3());
        mTestReqMap1.add(getReqMap2());
        mTestReqMap1.add(getReqMap2());
        mTestReqMap1.add(getReqMap1());


        mTestReqList2.add("demo_card_1");
        mTestReqList2.add("demo_card_2");
        mTestReqList2.add("demo_card_3");
        mTestReqList2.add("demo_card_3");
        mTestReqList2.add("demo_card_2");
        mTestReqList2.add("demo_card_1");
        mTestReqList2.add("demo_card_1");
        mTestReqList2.add("demo_card_2");
        mTestReqList2.add("demo_card_3");
        mTestReqList2.add("demo_card_3");
        mTestReqList2.add("demo_card_2");

        mTestReqMap2.add(getReqMap1());
        mTestReqMap2.add(getReqMap2());
        mTestReqMap2.add(getReqMap3());
        mTestReqMap2.add(getReqMap3());
        mTestReqMap2.add(getReqMap2());
        mTestReqMap2.add(getReqMap1());
        mTestReqMap2.add(getReqMap1());
        mTestReqMap2.add(getReqMap2());
        mTestReqMap2.add(getReqMap3());
        mTestReqMap2.add(getReqMap3());
        mTestReqMap2.add(getReqMap2());

        mTestReqList3.add("demo_card_3");
        mTestReqList3.add("demo_card_2");
        mTestReqList3.add("demo_card_1");
        mTestReqList3.add("demo_card_2");
        mTestReqList3.add("demo_card_3");
        mTestReqList3.add("demo_card_1");
        mTestReqList3.add("demo_card_3");
        mTestReqList3.add("demo_card_1");
        mTestReqList3.add("demo_card_2");
        mTestReqList3.add("demo_card_2");
        mTestReqList3.add("demo_card_1");

        mTestReqMap3.add(getReqMap3());
        mTestReqMap3.add(getReqMap2());
        mTestReqMap3.add(getReqMap1());
        mTestReqMap3.add(getReqMap2());
        mTestReqMap3.add(getReqMap3());
        mTestReqMap3.add(getReqMap1());
        mTestReqMap3.add(getReqMap3());
        mTestReqMap3.add(getReqMap1());
        mTestReqMap3.add(getReqMap2());
        mTestReqMap3.add(getReqMap2());
        mTestReqMap3.add(getReqMap1());

        mTestReqList4.add("demo_card_1");
        mTestReqList4.add("demo_card_1");
        mTestReqList4.add("demo_card_2");
        mTestReqList4.add("demo_card_3");
        mTestReqList4.add("demo_card_3");
        mTestReqList4.add("demo_card_2");
        mTestReqList4.add("demo_card_2");
        mTestReqList4.add("demo_card_3");
        mTestReqList4.add("demo_card_3");
        mTestReqList4.add("demo_card_2");
        mTestReqList4.add("demo_card_1");

        mTestReqMap4.add(getReqMap1());
        mTestReqMap4.add(getReqMap1());
        mTestReqMap4.add(getReqMap2());
        mTestReqMap4.add(getReqMap3());
        mTestReqMap4.add(getReqMap3());
        mTestReqMap4.add(getReqMap2());
        mTestReqMap4.add(getReqMap2());
        mTestReqMap4.add(getReqMap3());
        mTestReqMap4.add(getReqMap3());
        mTestReqMap4.add(getReqMap2());
        mTestReqMap4.add(getReqMap1());

        mTestReqList5.add("demo_card_3");
        mTestReqList5.add("demo_card_2");
        mTestReqList5.add("demo_card_3");
        mTestReqList5.add("demo_card_2");
        mTestReqList5.add("demo_card_1");
        mTestReqList5.add("demo_card_2");
        mTestReqList5.add("demo_card_3");
        mTestReqList5.add("demo_card_2");
        mTestReqList5.add("demo_card_3");
        mTestReqList5.add("demo_card_1");
        mTestReqList5.add("demo_card_2");

        mTestReqMap5.add(getReqMap3());
        mTestReqMap5.add(getReqMap2());
        mTestReqMap5.add(getReqMap3());
        mTestReqMap5.add(getReqMap2());
        mTestReqMap5.add(getReqMap1());
        mTestReqMap5.add(getReqMap2());
        mTestReqMap5.add(getReqMap3());
        mTestReqMap5.add(getReqMap2());
        mTestReqMap5.add(getReqMap3());
        mTestReqMap5.add(getReqMap1());
        mTestReqMap5.add(getReqMap2());

    }

    private Map<String, Var> getReqMap1(){
        Map<String, Var> map = new ConcurrentHashMap<String, Var>();

        map.put("text_1", new Var("这里是第一行文字，数据来自TestServer.java"));
        map.put("text_2", new Var("这里是第二行文字，数据来自TestServer.java"));

        return map;
    }

    private Map<String, Var> getReqMap2(){
        Map<String, Var> map = new ConcurrentHashMap<String, Var>();
        map.put("color_1", new Var("ff123fba"));
        map.put("color_2", new Var("ffcbaacb"));

        return map;
    }

    private Map<String, Var> getReqMap3(){
        Map<String, Var> map = new ConcurrentHashMap<String, Var>();
        map.put("btn_text_1", new Var("按钮一"));
        map.put("btn_text_2", new Var("按钮二"));

        return map;
    }

    private Map<String, Var> getTabData(){

        Map<String, Var> map = new ConcurrentHashMap<String, Var>();

        map.put("tab_count", new Var("13"));
        map.put("tab_name_1", new Var("要闻"));
        map.put("tab_name_2", new Var("游戏"));
        map.put("tab_name_3", new Var("娱乐"));
        map.put("tab_name_4", new Var("生活"));
        map.put("tab_name_5", new Var("图片"));
        map.put("tab_name_6", new Var("视频"));
        map.put("tab_name_7", new Var("国际"));
        map.put("tab_name_8", new Var("财经"));
        map.put("tab_name_9", new Var("科技"));
        map.put("tab_name_10", new Var("房产"));
        map.put("tab_name_11", new Var("时尚"));
        map.put("tab_name_12", new Var("读书"));
        map.put("tab_name_13", new Var("军事"));
        map.put("tab_id_1", new Var("1"));
        map.put("tab_id_2", new Var("2"));
        map.put("tab_id_3", new Var("3"));
        map.put("tab_id_4", new Var("4"));
        map.put("tab_id_5", new Var("5"));
        map.put("tab_id_6", new Var("6"));
        map.put("tab_id_7", new Var("7"));
        map.put("tab_id_8", new Var("8"));
        map.put("tab_id_9", new Var("9"));
        map.put("tab_id_10", new Var("10"));
        map.put("tab_id_11", new Var("11"));
        map.put("tab_id_12", new Var("12"));
        map.put("tab_id_13", new Var("13"));
        map.put("tab_index_1", new Var("1"));
        map.put("tab_index_2", new Var("2"));
        map.put("tab_index_3", new Var("3"));
        map.put("tab_index_4", new Var("4"));
        map.put("tab_index_5", new Var("5"));
        map.put("tab_index_6", new Var("6"));
        map.put("tab_index_7", new Var("7"));
        map.put("tab_index_8", new Var("8"));
        map.put("tab_index_9", new Var("9"));
        map.put("tab_index_10", new Var("10"));
        map.put("tab_index_11", new Var("11"));
        map.put("tab_index_12", new Var("12"));
        map.put("tab_index_13", new Var("13"));

        return map;
    }

    private List<String> mTestReqList1 = new ArrayList<String>();
    private List<String> mTestReqList2 = new ArrayList<String>();
    private List<String> mTestReqList3 = new ArrayList<String>();
    private List<String> mTestReqList4 = new ArrayList<String>();
    private List<String> mTestReqList5 = new ArrayList<String>();

    private List<Map<String, Var>> mTestReqMap1 = new ArrayList<Map<String, Var>>();
    private List<Map<String, Var>> mTestReqMap2 = new ArrayList<Map<String, Var>>();
    private List<Map<String, Var>> mTestReqMap3 = new ArrayList<Map<String, Var>>();
    private List<Map<String, Var>> mTestReqMap4 = new ArrayList<Map<String, Var>>();
    private List<Map<String, Var>> mTestReqMap5 = new ArrayList<Map<String, Var>>();
}
