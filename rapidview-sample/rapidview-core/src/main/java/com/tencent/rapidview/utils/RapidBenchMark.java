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

import com.tencent.rapidview.framework.RapidConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidBenchMark
 * @Desc RapidView性能分析工具
 *
 * @author arlozhang
 * @date 2016.06.28
 */
public class RapidBenchMark {

    class mark_node{
        String desc;
        long   time = 0;
    }

    private Map<String, List<mark_node>> mMapMark = new ConcurrentHashMap<String, List<mark_node>>();

    private static RapidBenchMark msInstance = null;

    public RapidBenchMark(){}

    public synchronized static RapidBenchMark get(){
        if( msInstance == null ){
            msInstance = new RapidBenchMark();
        }

        return msInstance;
    }

    public synchronized void start(String tag, String desc){
        List<mark_node> list;

        if( tag == null || desc == null || !RapidConfig.DEBUG_MODE ){
            return;
        }

        list = new ArrayList<mark_node>();

        list.add(get(desc));

        mMapMark.put(tag, list);
    }

    public synchronized void end(String tag, String desc){
        List<mark_node> list;

        if( tag == null || desc == null || !RapidConfig.DEBUG_MODE ){
            return;
        }

        list = mMapMark.get(tag);
        if( list == null ){
            return;
        }

        mMapMark.remove(tag);

        list.add(get(desc));

        print(tag, list);
    }

    public synchronized void mark(String tag, String desc){
        List<mark_node> list;

        if( tag == null || desc == null || !RapidConfig.DEBUG_MODE ){
            return;
        }

        list = mMapMark.get(tag);
        if( list == null ){
            return;
        }

        list.add(get(desc));
    }

    private mark_node get(String desc){
        mark_node node = new mark_node();

        node.desc = desc;
        node.time = System.currentTimeMillis();

        return node;
    }

    private void print(String tag, List<mark_node> list){
        String record = "";
        long startTime;

        if( list == null || list.size() == 0 ){
            return;
        }

        startTime = list.get(0).time;

        XLog.d(RapidConfig.RAPID_BENCHMARK_TAG, "--------------开始--------------");

        for( int i = 0; i < list.size(); i++ ){
            mark_node node = list.get(i);
            long spendTime = node.time - startTime;

            String log = "[" + Long.toString(spendTime) + "]" + node.desc;

            XLog.d(RapidConfig.RAPID_BENCHMARK_TAG, log);

            record += log;
            record += '\n';

            startTime = node.time;
        }

        XLog.d(RapidConfig.RAPID_BENCHMARK_TAG, "--------------结束--------------");

        FileUtil.write2File(record.getBytes(), FileUtil.getRapidBenchMarkDir() + tag + ".txt");
    }
}
