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
import com.tencent.rapidview.deobfuscated.IRapidTask;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidObject;
import com.tencent.rapidview.param.RecyclerViewLayoutParams;
import com.tencent.rapidview.utils.HandlerUtils;

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

    private String mRapidID = "";

    public NormalRecyclerViewAdapter(){

    }

    public void setActionListener(IRapidActionListener listener){
        mListener = listener;
    }

    public void setPhotonID(String photonID){
        mRapidID = photonID;
    }

    public void setLimitLevel(boolean limitLevel){
        mLimitLevel = limitLevel;
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

        if( value instanceof String){
            mFooterData.put(key, new Var((String)value));
            return;
        }

        if( value instanceof Long){
            mFooterData.put(key, new Var((Long)value));
            return;
        }

        if( value instanceof Integer){
            mFooterData.put(key, new Var((Integer) value));
            return;
        }

        if( value instanceof Double){
            mFooterData.put(key, new Var((Double)value));
            return;
        }

        if( value instanceof Boolean){
            mFooterData.put(key, new Var((Boolean)value));
            return;
        }

        mFooterData.put(key, new Var(value));

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
        String viewName;
        Integer ret = null;

        if( position == mListViewName.size() ){
            viewName = mFooterViewName;
        }
        else {
            viewName = mListViewName.get(position);
        }

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
    public NormalRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        String viewName = splitViewName(msIntToViewMap.get(viewType));
        IRapidView loadView = null;

        if( viewName.length() > 4 && viewName.substring(viewName.length() - 4, viewName.length()).compareToIgnoreCase(".xml") == 0 ){
            RapidObject object = new RapidObject();

            object.initialize(null, parent.getContext(), mRapidID, null, mLimitLevel, viewName, null);

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

        return new NormalRecyclerViewHolder(parent.getContext(), loadView);
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

        view.getParser().getTaskCenter().notify(IRapidTask.HOOK_TYPE.enum_data_start, "");

        view.getParser().getBinder().update(map);

        view.getParser().getTaskCenter().notify(IRapidTask.HOOK_TYPE.enum_data_end, "");

        if( mViewBindMap.get(position) == null ){
            view.getParser().getTaskCenter().notify(IRapidTask.HOOK_TYPE.enum_view_show, "");
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

    private String mergeViewName(String name){
        if( name == null ){
            return null;
        }

        if( name.length() > 4 && name.substring(name.length() - 4, name.length()).compareToIgnoreCase(".xml") == 0 ){
            return name + "|" + mRapidID;
        }

        return name;
    }

    private String splitViewName(String name){
        if( name == null ){
            return null;
        }

        if( name.contains(".xml|") ){
            return name.substring(0, name.lastIndexOf(".xml|") + 4);
        }

        return name;
    }
}
