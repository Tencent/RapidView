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
package com.tencent.rapidview.data;

import android.os.Handler;

import com.tencent.rapidview.deobfuscated.IDataBinder;
import com.tencent.rapidview.deobfuscated.IRapidNode;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.utils.RapidDataUtils;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Class RapidDataBinder
 * @Desc 数据绑定
 *
 * @author arlozhang
 * @date 2016.03.15
 */
public class RapidDataBinder implements IDataBinder{

    private class RegisterNode{
        String controlID;
        String AttributeKey;

        RegisterNode(String id, String key){
            controlID = id;
            AttributeKey = key;
        }
    }

    private Lock mLoadedLock = new ReentrantLock();

    private volatile boolean mIsLoaded = false;

    private Lock mDestroyLock = new ReentrantLock();

    private volatile boolean mIsDestroy = false;

    private Handler mMainHandler = null;

    private List<IRapidView> mViewList = new ArrayList<IRapidView>();

    private Map<String, Var> mMapData = new ConcurrentHashMap<String, Var>();

    private Map<String, Var> mMapWaitUpdateData = new ConcurrentHashMap<String, Var>();

    private Map<String, List<RegisterNode>> mMapRegister = new ConcurrentHashMap<String, List<RegisterNode>>();

    private Map<String, Var> mMapContext = new ConcurrentHashMap<String, Var>();

    public RapidDataBinder(Map<String, Var> mapData){
        if( mapData == null ){
            return;
        }

        for( Map.Entry<String, Var> entry : mapData.entrySet()  ){
            mMapData.put(entry.getKey(), entry.getValue());
        }
    }

    public void setUiHandler(Handler handler){
        mMainHandler = handler;
    }

    @Override
    public Handler getUiHandler(){
        return mMainHandler;
    }

    @Override
    public void addView(IRapidView view){
        mViewList.add(view);
    }

    @Override
    public void removeView(IRapidView view){
        for( int i = 0; i < mViewList.size(); i++ ){
            if( mViewList.get(i) != view ){
                continue;
            }

            try {
                mViewList.remove(i);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            break;
        }
    }

    public void onDestroy(){

        mDestroyLock.lock();

        try{
            if( mIsDestroy ){
                return;
            }

            mIsDestroy = true;
        }
        finally{
            mDestroyLock.unlock();
        }
    }

    public void setLoaded(){

        mLoadedLock.lock();

        try{
            if( mIsLoaded == true ){
                return;
            }

            mIsLoaded = true;
        }
        finally {
            mLoadedLock.unlock();
        }

        for( Map.Entry<String, Var> entry : mMapWaitUpdateData.entrySet() ){
            update(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void update(LuaTable table){
        Map<String, Var> map = RapidDataUtils.table2Map(table);

        if( table == null ){
            return;
        }

        update(map);
    }

    @Override
    public void update(String key, Object value){
        if( key == null || value == null ){
            return;
        }

        if( value instanceof String ){
            update(key, new Var((String)value));
            return;
        }

        if( value instanceof Long ){
            update(key, new Var((Long)value));
            return;
        }

        if( value instanceof Integer ){
            update(key, new Var((Integer) value));
            return;
        }

        if( value instanceof Double ){
            update(key, new Var((Double)value));
            return;
        }

        if( value instanceof Boolean ){
            update(key, new Var((Boolean)value));
            return;
        }

        update(key, new Var(value));
    }

    @Deprecated
    public void update(String key, String value){
        update(key, new Var(value));
    }

    public void setContext(Map<String, Var> map){
        if( map == null ){
            return;
        }

        mMapContext = map;
    }

    public Var getContextData(String key){
        Var var = null;

        if( mMapContext == null ){
            return new Var();
        }

        var = mMapContext.get(key);
        if( var == null ){
            var = new Var();
        }

        return var;
    }

    public Map<String, Var> getContextMap(){
        if( mMapContext == null ){
            return new ConcurrentHashMap<String, Var>();
        }
        return mMapContext;
    }

    public void update(Map<String, Var> map){

        if( map == null || map.isEmpty() ){
            return;
        }

        for( Map.Entry<String, Var> entry : map.entrySet() ){
            update(entry.getKey(), entry.getValue());
        }

        /*只有以map形式更新才会通知更新完毕*/
        for( int i = 0; i < mViewList.size(); i++ ){
            IRapidView view = mViewList.get(i);
            if( view == null ){
                continue;
            }

            view.getParser().onUpdateFinish();
        }
    }


    public void update(String key, Var value){
        List<RegisterNode> list;
        mLoadedLock.lock();

        try{
            if( !mIsLoaded ){
                mMapWaitUpdateData.put(key, value);
                return;
            }
        }
        finally {
            mLoadedLock.unlock();
        }

        mMapData.put(key, value);

        if( mViewList.size() == 0 ){
            return;
        }

        list = mMapRegister.get(key);
        if( list != null ) {
            for( RegisterNode node : list ){
                RegisterNode fNode = node;

                if( node == null ){
                    continue;
                }

                mDestroyLock.lock();

                try{

                    if( mIsDestroy ){
                        return;
                    }

                    for( int i = 0; i < mViewList.size(); i++ ){
                        IRapidView view = mViewList.get(i);
                        IRapidView childView;

                        if( view == null ){
                            continue;
                        }

                        childView = view.getParser().getChildView(fNode.controlID);
                        if( childView == null ){
                            continue;
                        }

                        RapidParserObject obj = childView.getParser();

                        obj.update(fNode.AttributeKey, value);

                        break;
                    }

                }
                finally{
                    mDestroyLock.unlock();
                }
            }
        }

        for( int i = 0; i < mViewList.size(); i++ ){
            IRapidView view;

            view = mViewList.get(i);
            if( view == null ){
                continue;
            }

            mDestroyLock.lock();

            try{
                if( mIsDestroy ){
                    return;
                }

                view.getParser().notify(IRapidNode.HOOK_TYPE.enum_datachange, key);
            }
            finally{
                mDestroyLock.unlock();
            }

            break;
        }
    }

    @Override
    public LuaValue bind(String dataKey, String id, String attrKey){
        Var var = getAndBind(dataKey, id, attrKey);

        if( var == null ){
            return null;
        }
        return var.getLuaValue();
    }

    public Var getAndBind(String dataKey, String id, String attrKey){
        List<RegisterNode> list = mMapRegister.get(dataKey);
        Var ret = mMapData.get(dataKey);

        if( ret == null || ret.isNull()){
            ret = new Var();
        }

        if( list == null ){
            list = new ArrayList<RegisterNode>();
            mMapRegister.put(dataKey, list);
        }

        //综合评估性能和风险，这里不检查是否已经存在，直接加进去比较好
        list.add(new RegisterNode(id, attrKey));

        return ret;
    }

    @Override
    public boolean unbind(String dataKey, String id, String attrKey){
        boolean ret = false;
        List<RegisterNode> list = mMapRegister.get(dataKey);

        if( list == null ){
            return ret;
        }

        for( int i = 0; i < list.size(); i++ ){
            RegisterNode node = list.get(i);

            if( node.controlID.compareTo(id) != 0 ||
                node.AttributeKey.compareTo(attrKey) != 0 ){
                continue;
            }

            list.remove(i);

            ret = true;

            break;
        }

        return ret;
    }

    @Override
    public LuaValue get(String key){
        Var var = getData(key);

        return var.getLuaValue();
    }

    public Var getData(String key){
        Var ret = null;

        if( key == null ){
            return new Var();
        }

        mLoadedLock.lock();

        try{
            if( !mIsLoaded ){

                ret = mMapWaitUpdateData.get(key);

                if( ret != null ){
                    return ret;
                }
            }
        }
        finally {
            mLoadedLock.unlock();
        }

        ret = mMapData.get(key);

        if( ret == null ){
            ret = new Var();
        }

        return ret;
    }

    @Override
    public void removeData(String key){
        mMapWaitUpdateData.remove(key);
        mMapData.remove(key);
    }

    public Map<String, Var> getDataMap(){
        return mMapData;
    }
}
