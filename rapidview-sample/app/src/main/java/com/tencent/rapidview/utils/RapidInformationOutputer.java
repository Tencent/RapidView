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

import com.tencent.rapidview.RapidVersion;
import com.tencent.rapidview.framework.RapidConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidInformationOutputer
 * @Desc 常规APK信息输出，由RapidView Studio解析
 *
 * @author arlozhang
 * @date 2016.07.06
 */
public class RapidInformationOutputer {

    private Map<String, String> mmapUser = new ConcurrentHashMap<String, String>();

    private static RapidInformationOutputer mInstance = null;

    public static RapidInformationOutputer get(){
        if( mInstance == null ){
            mInstance = new RapidInformationOutputer();
        }

        return mInstance;
    }

    public void output(){
        output(false);
    }

    public synchronized void output(boolean forceOutput){

        if( !RapidConfig.OUTPUT_INFORMATION_MODE && !forceOutput ){
            return;
        }

        _output();
    }

    public synchronized void addUserOutput(String key, String value){
        mmapUser.put(key, value);
    }

    private void _output(){
        JSONObject obj = new JSONObject();
        JSONArray arrayNormal = new JSONArray();
        JSONArray arrayUser = new JSONArray();

        _addUser(arrayUser);
        _addRapidVersion(arrayNormal);
        _addRapidGrayID(arrayNormal);

        try{
            obj.put("normal_config", arrayNormal);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        try{
            obj.put("user_config", arrayUser);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        FileUtil.write2File(obj.toString().getBytes(),
                FileUtil.getRapidDebugDir() + "rapid_apk_information_output.json");
    }

    private void _addUser(JSONArray array){

    }

    private void _addRapidVersion(JSONArray array){
        JSONObject obj = new JSONObject();

        try{
            obj.put("key", "Rapid Version");
            obj.put("value", Integer.toString(RapidVersion.RAPID_ENGINE_VERSION));
        }
        catch ( JSONException e ){
            e.printStackTrace();
        }

        array.put(obj);
    }

    private void _addRapidGrayID(JSONArray array){
        JSONObject obj = new JSONObject();

        try{
            obj.put("key", "Rapid Gray ID");
            obj.put("value", RapidVersion.RAPID_GRAY_ID);
        }
        catch ( JSONException e ){
            e.printStackTrace();
        }

        array.put(obj);
    }
}
