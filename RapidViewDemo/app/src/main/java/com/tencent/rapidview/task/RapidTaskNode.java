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

import com.tencent.rapidview.action.ActionChooser;
import com.tencent.rapidview.action.ActionObject;
import com.tencent.rapidview.data.DataExpressionsParser;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidTask;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.filter.FilterChooser;
import com.tencent.rapidview.filter.FilterObject;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.utils.RapidControlNameCreator;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.XLog;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidTaskNode
 * @Desc RapidView任务元类
 *
 * @author arlozhang
 * @date 2016.03.16
 */
public class RapidTaskNode {

    private Map<String, String> mMapAttribute = new ConcurrentHashMap<String, String>();

    private List<FilterObject> mListFilters = new ArrayList<FilterObject>();

    private List<ActionObject> mListActions = new ArrayList<ActionObject>();

    private IRapidView mRapidView;

    Map<String, String> mMapEnvironment;

    private Map<IRapidTask.HOOK_TYPE, Boolean> mMapHookType = new ConcurrentHashMap<IRapidTask.HOOK_TYPE, Boolean>();

    private TASK_TYPE mTaskType = TASK_TYPE.enum_continue;

    private String mID = null;

    private String mValue;

    private final boolean mLimitLevel;

    public enum TASK_TYPE{
        enum_continue,   //当成功执行后继续执行
        enum_interrupt,  //成功执行后打断执行
    }

    public RapidTaskNode(IRapidView rapidView, Element element, Map<String, String> mapEnv, boolean limitLevel){
        mRapidView = rapidView;
        mMapEnvironment = mapEnv;
        mLimitLevel = limitLevel;
        initialize(element);
        analyzeAttribute();
    }

    public void setRapidView(IRapidView rapidView){
        mRapidView = rapidView;

        for( int i = 0; i < mListActions.size(); i++ ){
            ActionObject obj = mListActions.get(i);
            if( obj == null ){
                continue;
            }

            obj.setRapidView(rapidView);
        }

        for( int i = 0; i < mListFilters.size(); i++ ){
            FilterObject obj = mListFilters.get(i);
            if( obj == null ){
                continue;
            }

            obj.setRapidView(rapidView);
        }
    }

    public void notify(IRapidTask.HOOK_TYPE type, String value){
        if( mMapHookType.get(type) == null ){
            return;
        }

        switch ( type ){
            case enum_datachange:
            case enum_view_scroll_exposure:
                notifyValue(value);
                break;
            case enum_load_finish:
            case enum_data_initialize:
            case enum_view_show:
            case enum_data_start:
            case enum_data_end:
                run();
                break;
        }
    }

    public boolean run(){
        boolean bRet = false;

        try{
            XLog.d(RapidConfig.RAPID_TASK_TAG, "开始执行任务：" + getID());

            if( !isPass() ){
                XLog.d(RapidConfig.RAPID_TASK_TAG, "任务条件未获通过：" + getID());
                return bRet;
            }

            runAction();

            bRet = true;
        }
        catch (Exception e){
            e.printStackTrace();
            bRet = false;
        }

        return bRet;
    }

    public TASK_TYPE getTaskType(){
        return mTaskType;
    }

    public String getID(){
        if( RapidStringUtils.isEmpty(mID) ){
            mID = RapidControlNameCreator.get();
            mID = mID.toLowerCase();
        }

        return mID;
    }

    private boolean isPass(){

        for( int i = 0; i < mListFilters.size(); i++ ){
            FilterObject filter = mListFilters.get(i);
            if( filter == null ){
                XLog.d(RapidConfig.RAPID_TASK_TAG, "无当前条目过滤器：" + Integer.toString(i + 1));
                return false;
            }

            if( !filter.isPass() ){
                XLog.d(RapidConfig.RAPID_TASK_TAG, "过滤器未获通过，序号：" + Integer.toString(i + 1));
                return false;
            }
        }

        return true;
    }

    private void notifyValue(String value){
        if( value.compareToIgnoreCase(mValue) != 0 ){
            return;
        }

        run();
    }

    private void runAction(){
        for( int i = 0; i < mListActions.size(); i++ ){

            ActionObject action = mListActions.get(i);
            if( action == null ){
                continue;
            }

            if( !action.callRun() ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "执行动作失败：" + action.getActionName());
            }
        }
    }

    private void initialize(Element element){
        NamedNodeMap mapAttrs;
        NodeList listChilds;

        DataExpressionsParser parser = new DataExpressionsParser();

        if( element == null ){
            return;
        }

        mapAttrs = element.getAttributes();
        listChilds = element.getChildNodes();

        mMapAttribute.clear();
        for( int i = 0; i < mapAttrs.getLength(); i++ ){
            String value = mapAttrs.item(i).getNodeValue();

            if( parser.isDataExpression(value) ){
                Var var = parser.get(null, mMapEnvironment, null, null, value);
                if( var != null ){
                    value = var.getString();
                }
            }

            mMapAttribute.put(mapAttrs.item(i).getNodeName().toLowerCase(), value);
        }

        mListFilters.clear();
        mListActions.clear();
        for( int i = 0; i < listChilds.getLength(); i++ ){
            FilterObject filter;
            ActionObject action;

            if( listChilds.item(i).getNodeType() != Node.ELEMENT_NODE ){
                continue;
            }

            Element item = (Element)listChilds.item(i);


            filter = FilterChooser.get(item, mMapEnvironment, mLimitLevel);
            if( filter != null ){
                mListFilters.add(filter);
                continue;
            }

            action = ActionChooser.get(item, mMapEnvironment, mLimitLevel);
            if( action != null ){
                mListActions.add(action);
            }
        }
    }

    private void analyzeAttribute(){
        analyzeTaskType();
        analzyeHookType();
        analyzeID();
        analyzeValue();
    }

    private void analyzeValue(){
        mValue = mMapAttribute.get("value");
        if( mValue == null ){
            mValue = "";
        }
    }

    private void analyzeID(){
        mID = mMapAttribute.get("id");
        if( mID == null ){
            mID = "";
        }

        mID = mID.toLowerCase();
    }

    private void analyzeTaskType(){
        String taskType = mMapAttribute.get("type");

        if( taskType == null ){
            taskType = "";
        }

        mTaskType = TASK_TYPE.enum_continue;
        if( taskType.compareToIgnoreCase("interrupt") == 0 ){
            mTaskType = TASK_TYPE.enum_interrupt;
        }
    }

    private void analzyeHookType(){
        String hookType = mMapAttribute.get("hook");
        List<String> listType = null;

        if( hookType == null ){
            hookType = "";
        }

        listType = RapidStringUtils.stringToList(hookType);

        for( int i = 0; i < listType.size(); i++ ){
            String type = listType.get(i);

            if( type.compareToIgnoreCase("datachange") == 0 ||
                type.compareToIgnoreCase("data_change") == 0 ){
                mMapHookType.put(IRapidTask.HOOK_TYPE.enum_datachange, true);
            }
            else if( hookType.compareToIgnoreCase("loadfinish") == 0 ||
                    hookType.compareToIgnoreCase("load_finish") == 0 ){
                mMapHookType.put(IRapidTask.HOOK_TYPE.enum_load_finish, true);
            }
            else if( hookType.compareToIgnoreCase("datainitialize") == 0 ||
                     hookType.compareToIgnoreCase("data_initialize") == 0){
                mMapHookType.put(IRapidTask.HOOK_TYPE.enum_data_initialize, true);
            }
            else if( hookType.compareToIgnoreCase("viewshow") == 0 ||
                     hookType.compareToIgnoreCase("view_show") == 0 ){
                mMapHookType.put(IRapidTask.HOOK_TYPE.enum_view_show, true);
            }
            else if( hookType.compareToIgnoreCase("viewscrollexposure") == 0 ||
                    hookType.compareToIgnoreCase("view_scroll_exposure") == 0 ){
                mMapHookType.put(IRapidTask.HOOK_TYPE.enum_view_scroll_exposure, true);
            }
            else if( hookType.compareToIgnoreCase("data_start") == 0 ||
                    hookType.compareToIgnoreCase("datastart") == 0 ){
                mMapHookType.put(IRapidTask.HOOK_TYPE.enum_data_start, true);
            }
            else if( hookType.compareToIgnoreCase("data_end") == 0 ||
                    hookType.compareToIgnoreCase("dataend") == 0 ){
                mMapHookType.put(IRapidTask.HOOK_TYPE.enum_data_end, true);
            }
        }

    }

}
