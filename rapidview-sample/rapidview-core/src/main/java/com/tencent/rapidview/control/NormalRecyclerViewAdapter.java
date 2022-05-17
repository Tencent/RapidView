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
package com.tencent.rapidview.control;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tencent.rapidview.RapidLoader;
import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidNode;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidObject;
import com.tencent.rapidview.framework.RapidRuntimeCachePool;
import com.tencent.rapidview.param.RecyclerViewLayoutParams;
import com.tencent.rapidview.utils.HandlerUtils;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class NormalRecyclerViewAdapter
 * @Desc RapidView 使用的RecyclerViewAdapter
 *
 * @author arlozhang
 * @date 2017.09.14
 */
public class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewHolder>{

    private static Map<String, Integer> msViewToIntMap = new ConcurrentHashMap<String, Integer>();
    private static Map<Integer, String> msIntToViewMap = new ConcurrentHashMap<Integer, String>();

    private List<Map<String, Var>> mListData = new ArrayList<>();

    private List<String> mListViewName = new ArrayList<String>();

    private Map<Integer, Boolean> mViewBindMap = new ConcurrentHashMap<Integer, Boolean>();

    private String mFooterViewName = null;

    private Map<String, Var> mFooterData = null;

    private IRapidActionListener mListener = null;

    private boolean mShowFooter = false;

    private boolean mLimitLevel = false;

    private String mRapidID = null;

    private int mRapidIDLength = 0;

    public NormalRecyclerViewAdapter(){

    }

    public void setActionListener(IRapidActionListener listener){
        mListener = listener;
    }

    public void setRapidID(String rapidID){
        if( rapidID == null || rapidID.compareTo("") == 0 ){
            return;
        }

        mRapidID = rapidID;
        mRapidIDLength = rapidID.length();
    }

    public void setLimitLevel(boolean limitLevel){
        mLimitLevel = limitLevel;
    }

    public void clear(){
        mListData.clear();
        mListViewName.clear();
        mViewBindMap.clear();

        notifyDataSetChanged();
    }

    public void updateData(String view, Map<String, Var> data){
        mListViewName.add(view);
        mListData.add(data);

        notifyDataSetChanged();
    }

    public void updateData(List<Map<String, Var>> dataList, List<String> viewList, boolean clear){
        if( clear ){
            mListData.clear();
            mListViewName.clear();
            mViewBindMap.clear();
        }

        if(dataList == null || viewList == null || dataList.size() != viewList.size()){
            if( clear ){
                notifyDataSetChanged();
            }

            return;
        }

        mListData.addAll(dataList);
        mListViewName.addAll(viewList);

        notifyDataSetChanged();
    }

    public void updateData(String view, LuaTable data, Boolean clear){

        if( clear ){
            mListData.clear();
            mListViewName.clear();
            mViewBindMap.clear();
        }

        if( view == null || data == null || !data.istable() ){
            return;
        }

        addData(view, data);

        notifyDataSetChanged();
    }

    public void updateData(LuaTable viewList, LuaTable dataList){
        LuaValue dataKey = LuaValue.NIL;
        LuaValue dataValue = LuaValue.NIL;
        LuaValue viewKey = LuaValue.NIL;
        LuaValue viewValue = LuaValue.NIL;

        if( viewList == null || dataList == null || !viewList.istable() || !dataList.istable() ){
            return;
        }

        while(true){
            Varargs argsView = viewList.next(dataKey);
            Varargs argsData = dataList.next(viewKey);

            viewKey = argsView.arg1();
            dataKey = argsData.arg1();

            if( dataKey.isnil() || viewKey.isnil() ){
                break;
            }

            viewValue = argsView.arg(2);
            dataValue = argsData.arg(2);

            if( !viewValue.isstring() ){
                continue;
            }

            if( dataValue.istable() ){
                addData(viewValue.toString(), dataValue.checktable());
            }

            if( dataValue.isuserdata() ){
                Object obj = dataValue.checkuserdata();
                mListViewName.add(viewValue.toString());

                if( obj instanceof Var ){
                    Map<String, Var> map = (Map<String, Var>)((Var) obj).getObject();
                    mListData.add(map);
                }
                else{
                    mListData.add((Map<String, Var>) obj);
                }

            }
        }


        notifyDataSetChanged();
    }

    public void updateItemData(int index, String key, Object value){
        Map<String, Var> map = mListData.get(index);

        if( map == null || key == null || value == null ){
            return;
        }

        map.put(key, new Var(value));
    }

    public void setFooter(String viewName, Map<String, Var> mapData){

        if( viewName == null ){
            return;
        }

        if( mapData == null ){
            mapData = new ConcurrentHashMap<String, Var>();
        }

        mFooterViewName = viewName;
        mFooterData = mapData;
        mShowFooter = true;

        notifyDataSetChanged();
    }

    public void hideFooter(){
        mShowFooter = false;
        notifyDataSetChanged();
    }

    public void showFooter(){
        mShowFooter = true;
        notifyDataSetChanged();
    }

    public void updateFooterData(String key, Object value){
        if( key == null || value == null ){
            return;
        }

        if( mFooterData == null ){
            return;
        }

        if( value instanceof String ){
            mFooterData.put(key, new Var((String)value));
            return;
        }

        if( value instanceof Long ){
            mFooterData.put(key, new Var((Long)value));
            return;
        }

        if( value instanceof Integer ){
            mFooterData.put(key, new Var((Integer) value));
            return;
        }

        if( value instanceof Double ){
            mFooterData.put(key, new Var((Double)value));
            return;
        }

        if( value instanceof Boolean ){
            mFooterData.put(key, new Var((Boolean)value));
            return;
        }

        mFooterData.put(key, new Var(value));

        notifyDataSetChanged();
    }

    public int getTypeByName(String name){
        return msViewToIntMap.get(name);
    }

    public String getNameByType(int type){
        return msIntToViewMap.get(type);
    }

    public int getViewType(String viewName){
        Integer ret = null;

        ret = msViewToIntMap.get(mergeViewName(viewName));
        if( ret != null ){
            return ret;
        }

        ret = msViewToIntMap.size();
        msViewToIntMap.put(mergeViewName(viewName), ret);
        msIntToViewMap.put(ret, mergeViewName(viewName));

        return ret;
    }

    @Override
    public int getItemViewType(int position){
        String viewName;

        if( position == mListViewName.size() ){
            viewName = mFooterViewName;
        }
        else {
            viewName = mListViewName.get(position);
        }

        return getViewType(viewName);
    }

    @Override
    public NormalRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        String                   viewName = splitViewName(msIntToViewMap.get(viewType));
        NormalRecyclerViewHolder holder   = null;
        IRapidView               loadView = null;

        if( viewName.length() > 4 && viewName.substring(viewName.length() - 4, viewName.length()).compareToIgnoreCase(".xml") == 0 ){
            RapidObject object = RapidRuntimeCachePool.getInstance().get(mRapidID, viewName, mLimitLevel);

            loadView = object.load(HandlerUtils.getMainHandler(),
                    parent.getContext(),
                    RecyclerViewLayoutParams.class,
                    null,
                    mListener);

        }
        else{
            loadView = RapidLoader.load(viewName, HandlerUtils.getMainHandler(), parent.getContext(), RecyclerViewLayoutParams.class, null, mListener);
        }

        if( loadView == null ){
            return new NormalRecyclerViewHolder(parent.getContext(), new ImageView(parent.getContext()));
        }

        loadView.getView().setLayoutParams(loadView.getParser().getParams().getLayoutParams());

        loadView.getView().setTag(loadView);

        holder = new NormalRecyclerViewHolder(parent.getContext(), loadView);

        return holder;
    }

    @Override
    public void onBindViewHolder(NormalRecyclerViewHolder holder, int position){
        Map<String, Var> map = null;
        IRapidView view = holder.getView();
        if( view == null ){
            return;
        }

        if( position == mListViewName.size() ){
            map = mFooterData;
        }
        else{
            map = mListData.get(position);
        }

        view.getParser().getTaskCenter().notify(IRapidNode.HOOK_TYPE.enum_data_start, "");

        updateCommonData(view, position);

        for( Map.Entry<String, Var> entry : map.entrySet() ){
            view.getParser().getBinder().update(entry.getKey(), entry.getValue());
        }

        view.getParser().onUpdateFinish();
        view.getParser().getTaskCenter().notify(IRapidNode.HOOK_TYPE.enum_data_end, "");

        if( mViewBindMap.get(position) == null ){
            view.getParser().getTaskCenter().notify(IRapidNode.HOOK_TYPE.enum_view_show, "");
            mViewBindMap.put(position, true);
        }
    }

    @Override
    public int getItemCount(){
        if( !mShowFooter || mFooterViewName == null ){
            return mListViewName.size();
        }

        return mListViewName.size() + 1;
    }

    private void addData(String view, LuaTable data){
        LuaValue key = LuaValue.NIL;
        LuaValue value = LuaValue.NIL;
        Map<String, Var> map = new ConcurrentHashMap<String, Var>();

        while(true){
            Varargs argsItem = data.next(key);
            key = argsItem.arg1();
            Var var = null;

            if( key.isnil() ){
                break;
            }

            value = argsItem.arg(2);

            if( !value.isuserdata() || !(value.touserdata() instanceof Var) ){
                var = new Var(value);
            }
            else{
                var = (Var)value.touserdata();
            }

            if( key.isstring() ){
                map.put(key.toString(), var);
            }
        }

        mListViewName.add(view);
        mListData.add(map);
    }

    private void updateCommonData(IRapidView view, int position){
        view.getParser().getBinder().update("index", new Var(position));
    }

    private String mergeViewName(String name){
        if( name == null ){
            return null;
        }

        if( mRapidID == null ){
            return name;
        }


        return mRapidID + name;
    }

    private String splitViewName(String name){
        if( name == null ){
            return null;
        }

        if( mRapidID == null ){
            return name;
        }

        return name.substring(mRapidIDLength);
    }
}
