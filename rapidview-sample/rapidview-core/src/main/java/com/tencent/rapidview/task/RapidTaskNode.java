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

import android.os.Looper;

import com.tencent.rapidview.action.ActionChooser;
import com.tencent.rapidview.action.ActionObject;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.filter.FilterChooser;
import com.tencent.rapidview.filter.FilterObject;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.framework.RapidNodeImpl;
import com.tencent.rapidview.utils.XLog;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Class RapidTaskNode
 * @Desc RapidView任务元类
 *
 * @author arlozhang
 * @date 2016.03.16
 */
public class RapidTaskNode extends RapidNodeImpl {

    private List<FilterObject> mListFilters = new ArrayList<FilterObject>();

    private List<ActionObject> mListActions = new ArrayList<ActionObject>();

    private boolean mInitialize = false;

    private TASK_TYPE mTaskType = TASK_TYPE.enum_continue;

    private final boolean mLimitLevel;

    public enum TASK_TYPE{
        enum_continue,   //当成功执行后继续执行
        enum_interrupt,  //成功执行后打断执行
    }

    public RapidTaskNode(IRapidView photonView, Element element, Map<String, String> mapEnv, boolean limitLevel){
        mRapidView = photonView;
        mMapEnvironment = mapEnv;
        mLimitLevel = limitLevel;
        mElement = element;

        analyzeTaskType();
        analyzeID();
        analyzeValue();
        analzyeHookType();

        if( Looper.myLooper() != Looper.getMainLooper() ){
            initialize(element);
        }
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

    public void notify(HOOK_TYPE type, String value){
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

        if( mInitialize == false ){
            initialize(mElement);
            setRapidView(mRapidView);
        }

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
        NodeList listChilds;

        if( element == null ){
            return;
        }

        listChilds = element.getChildNodes();

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

        mInitialize = true;
    }

    private void analyzeTaskType(){
        String taskType;
        Node node = mElement.getAttributes().getNamedItem("type");

        if( node == null ){
            return;
        }

        taskType = node.getNodeValue();
        taskType = getTransValue(taskType);

        mTaskType = TASK_TYPE.enum_continue;
        if( taskType.compareToIgnoreCase("interrupt") == 0 ){
            mTaskType = TASK_TYPE.enum_interrupt;
        }
    }
}
