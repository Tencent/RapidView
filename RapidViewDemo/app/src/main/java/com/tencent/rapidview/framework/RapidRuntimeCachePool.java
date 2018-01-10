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

import com.tencent.rapidview.utils.FileUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidRuntimeCachePool
 * @Desc 光子实时加载布局的并发加载缓存池，
 *
 * @author arlozhang
 * @date 2017.12.27
 */
public class RapidRuntimeCachePool {

    private Map<String, Map<String, RapidObject>> mCacheMap = new ConcurrentHashMap<String, Map<String, RapidObject>>();

    private Context mContext = null;

    public static RapidRuntimeCachePool msInstance = null;

    public static RapidRuntimeCachePool getInstance(){

        if( msInstance == null ){
            msInstance = new RapidRuntimeCachePool();
        }

        return msInstance;
    }

    public void setContext(Context context){
        mContext = context;
    }

    public RapidObject get(String photonID, String xml, boolean limitLevel){
        Map<String, RapidObject> mapRuntime = null;
        RapidObject newObj = new RapidObject();
        RapidObject oldObj = null;

        if( xml == null ){
            return null;
        }

        if( photonID == null ){
            photonID = "";
        }

        if( RapidConfig.DEBUG_MODE && FileUtil.isFileExists(FileUtil.getRapidDebugDir() + xml) ){
            oldObj = new RapidObject();
            oldObj.initialize(null, mContext, photonID, null, limitLevel, xml, null);

            return oldObj;
        }

        mapRuntime = mCacheMap.get(photonID);

        if( mapRuntime == null ){
            mapRuntime = new ConcurrentHashMap<String, RapidObject>();
            mCacheMap.put(photonID, mapRuntime);
        }

        oldObj = mapRuntime.get(xml);

        if( oldObj == null ){
            oldObj = new RapidObject();
            oldObj.initialize(null, mContext, photonID, null, limitLevel, xml, null);
        }

        newObj.initialize(null, mContext, photonID, null, limitLevel, xml, null);
        mapRuntime.put(xml, newObj);

        return oldObj;
    }

    public boolean set(String photonID, String xml, boolean limitLevel){
        Map<String, RapidObject> mapRuntime = null;
        RapidObject object = new RapidObject();

        if( xml == null ){
            return false;
        }

        if( photonID == null ){
            photonID = "";
        }

        if( RapidConfig.DEBUG_MODE ){
            return true;
        }

        mapRuntime = mCacheMap.get(photonID);

        if( mapRuntime == null ){
            mapRuntime = new ConcurrentHashMap<String, RapidObject>();
            mCacheMap.put(photonID, mapRuntime);
        }

        object.initialize(null, mContext, photonID, null, limitLevel, xml, null);
        mapRuntime.put(xml, object);

        return true;
    }
}
