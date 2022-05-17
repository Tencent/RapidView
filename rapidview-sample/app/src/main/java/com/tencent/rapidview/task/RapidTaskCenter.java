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
package com.tencent.rapidview.task;

import com.tencent.rapidview.action.ActionRunner;
import com.tencent.rapidview.deobfuscated.IActionRunner;
import com.tencent.rapidview.deobfuscated.IFilterRunner;
import com.tencent.rapidview.deobfuscated.IRapidNode;
import com.tencent.rapidview.deobfuscated.IRapidTask;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.filter.FilterRunner;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidTaskCenter
 * @Desc RapidView任务调度中心
 *
 * @author arlozhang
 * @date 2016.03.16
 */
public class RapidTaskCenter implements IRapidTask {

    private IRapidView mRapidView;

    private Map<String, String> mMapEnvironment;

    private Map<String, RapidTaskNode> mMapDataTask = new ConcurrentHashMap<String, RapidTaskNode>();

    private IActionRunner mActionRunner = null;

    private IFilterRunner mFilterRunner = null;

    private final boolean mLimitLevel;

    public RapidTaskCenter(IRapidView rapidView, boolean limitLevel){
        mRapidView = rapidView;
        mLimitLevel = limitLevel;
        mMapDataTask.clear();
    }

    @Override
    public void setRapidView(IRapidView rapidView){
        mRapidView = rapidView;

        for( Map.Entry<String, RapidTaskNode> entry : mMapDataTask.entrySet() ){
            RapidTaskNode node = entry.getValue();
            if( node == null ){
                continue;
            }

            node.setRapidView(mRapidView);
        }
    }

    @Override
    public IRapidView getRapidView(){
        return mRapidView;
    }


    @Override
    public void setEnvironment(Map<String, String> mapEnv){
        mMapEnvironment = mapEnv;
    }

    @Override
    public LuaTable getEnv(){
        LuaTable table = new LuaTable();

        for( Map.Entry<String, String> entry : mMapEnvironment.entrySet() ){
            table.set(LuaString.valueOf(entry.getKey()), LuaString.valueOf(entry.getValue()));
        }

        return table;
    }

    public Map<String, String> getEnvironment(){
        return  mMapEnvironment;
    }

    @Override
    public void add(Element element){
        if( element == null ){
            return;
        }

        RapidTaskNode node = new RapidTaskNode(mRapidView, element, mMapEnvironment, mLimitLevel);

        mMapDataTask.put(node.getID(), node);
    }

    @Override
    public void run(List<String> listKey) {
        if( listKey == null ){
            return;
        }

        for( int i = 0; i < listKey.size(); i++ ){
            RapidTaskNode node;
            String key = listKey.get(i);

            node = getNode(key);
            if( node == null ){
                continue;
            }

            if( node.run() && node.getTaskType().equals(RapidTaskNode.TASK_TYPE.enum_interrupt ) ){
                break;
            }
        }
    }

    @Override
    public void run(String key){
        RapidTaskNode node;

        if( key == null ){
            return;
        }

        node = getNode(key);
        if( node == null ){
            return;
        }

        node.run();
    }

    @Override
    public IActionRunner getActionRunner(){
        if( mActionRunner == null ){
            mActionRunner = new ActionRunner(mRapidView, mLimitLevel);
        }

        return mActionRunner;
    }

    @Override
    public IFilterRunner getFilterRunner(){
        if( mFilterRunner == null ){
            mFilterRunner = new FilterRunner(mRapidView, mLimitLevel);
        }

        return mFilterRunner;
    }

    public void notify(IRapidNode.HOOK_TYPE type, String value) {

        for( Map.Entry<String, RapidTaskNode> entry : mMapDataTask.entrySet() ){

            RapidTaskNode task = entry.getValue();

            if (task == null) {
                continue;
            }

            task.notify(type, value);
        }

    }

    private RapidTaskNode getNode(String id){
        if( id == null ){
            return null;
        }

        return mMapDataTask.get(id.toLowerCase());
    }

}
