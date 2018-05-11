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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Class RapidConfigWrapper
 * @Desc 包装RapidView文件记录配置的类
 *
 * @author arlozhang
 * @date 2016.05.24
 */
public class RapidConfigWrapper {

    private static final String CONFIG_NAME = "rapid_config.json";

    private static final String ATTR_FILE_LIST = "file_list";

    private static final String ATTR_VIEW_LIST = "view_list";

    private static final String ATTR_NAME = "name";

    private static final String ATTR_VERSION = "version";

    private static final String ATTR_MD5 = "md5";

    private List<RAPID_FILE> mCacheFileList = null;

    private List<RAPID_FILE> mCacheViewList = null;

    private Map<String, RAPID_VIEW> mCacheViewMap = null;

    private Lock mLock = new ReentrantLock();

    private static RapidConfigWrapper mSelf;

    public class RAPID_FILE {
        public String name;
        public String version;
        public String md5;
    }

    public static class RAPID_VIEW {
        public String name;
        public String version;
        public String mainFile;
        public List<String> relyFileList = new ArrayList<String>();
    }

    public static RapidConfigWrapper getInstance(){

        if( mSelf == null ){
            mSelf = new RapidConfigWrapper();
        }

        return mSelf;
    }

    /*获取不包括View的文件列表*/
    public void getFile(List<RAPID_FILE> list){
        mLock.lock();

        try{
            if( list == null ){
                return;
            }

            list.clear();

            if( mCacheFileList != null ){
                list.addAll(mCacheFileList);
                return;
            }

            readAll();

            list.addAll(mCacheFileList);
        }
        finally {
            mLock.unlock();
        }
    }

    public void getView(List<RAPID_FILE> list){
        mLock.lock();

        try{
            if( list == null ){
                return;
            }

            list.clear();

            if( mCacheViewList != null ){
                list.addAll(mCacheViewList);
                return;
            }

            readAll();

            list.addAll(mCacheViewList);
        }
        finally {
            mLock.unlock();
        }
    }

    public void clearCache(){
        mLock.lock();

        try{
            mCacheFileList = null;
            mCacheViewList = null;
            mCacheViewMap = null;
        }
        finally {
            mLock.unlock();
        }
    }

    public RAPID_VIEW getExistView(String viewName){
        mLock.lock();

        try{
            RAPID_VIEW retView = null;

            if( mCacheViewMap != null ){
                return mCacheViewMap.get(viewName);
            }

            retView = readView(viewName);

            if( !isViewExist(retView) ){
                return null;
            }

            return retView;
        }
        finally {
            mLock.unlock();
        }
    }

    public void getAllExistView(Map<String, RAPID_VIEW> map){
        mLock.lock();

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "获取ExistView");
        try{
            if( map == null ){
                return;
            }

            map.clear();

            if( mCacheViewMap != null ){
                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "发现缓存ExistView，返回缓存数据");
                map.putAll(mCacheViewMap);
                return;
            }

            readAll();

            map.putAll(mCacheViewMap);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            mLock.unlock();
        }
    }


    public boolean update(List<RAPID_FILE> addFileList,
                       List<RAPID_FILE> addViewList,
                       List<String> deleteList){

        mLock.lock();

        try{
            JSONArray newFileArray = new JSONArray();
            JSONArray newViewArray = new JSONArray();
            JSONObject obj = getNativeConfig();
            JSONObject newObj = new JSONObject();
            Map<String, String> mapDelete = new ConcurrentHashMap<String, String>();

            mCacheFileList = null;
            mCacheViewList = null;
            mCacheViewMap = null;

            if( obj == null ){
                obj = new JSONObject();
            }

            for( int i = 0; i < deleteList.size(); i++ ){
                mapDelete.put(deleteList.get(i), "delete");
            }

            for( int i = 0; i < addFileList.size(); i++ ){
                mapDelete.put(addFileList.get(i).name, "delete");
            }

            for( int i = 0; i < addViewList.size(); i++ ){
                mapDelete.put(addViewList.get(i).name, "delete");
            }

            updateExistFile(obj, newFileArray, mapDelete);
            updateExistView(obj, newViewArray, mapDelete);
            updateNewFile(newFileArray, addFileList);
            updateNewView(newViewArray, addViewList);

            try{
                newObj.put(ATTR_FILE_LIST, newFileArray);
                newObj.put(ATTR_VIEW_LIST, newViewArray);
            }
            catch (JSONException e){
                e.printStackTrace();
                return false;
            }

            return putConfig(newObj);
        }
        finally {
            mLock.unlock();
        }
    }

    public boolean isViewExist(RAPID_VIEW view){

        if( view == null ){
            return false;
        }

        for( int i = 0; i < view.relyFileList.size(); i++ ){

            if( !isFileExist( view.relyFileList.get(i) ) ){
                return false;
            }

        }

        return true;
    }

    public RAPID_VIEW readView(String name){
        String content;
        JSONObject viewObj;
        RAPID_VIEW rapidView = new RAPID_VIEW();

        if( name == null ){
            return null;
        }

        content = RapidFileLoader.getInstance().getString(name);
        if( content == null || content.compareTo("") == 0 ){
            return null;
        }

        try{
            viewObj = new JSONObject(content);
            JSONArray arrayList;
            String grayID;

            rapidView.name = viewObj.getString("viewName");
            rapidView.version = viewObj.getString("viewVer");
            rapidView.mainFile = viewObj.getString("mainFile");
            grayID = viewObj.isNull("grayCode") ? "" : viewObj.getString("grayCode");
            if( grayID.compareTo("") != 0 ){
                rapidView.version += ".";
                rapidView.version += grayID;
            }

            arrayList = viewObj.getJSONArray("fileList");

            for( int i = 0; i < arrayList.length(); i++ ){
                JSONObject obj = arrayList.getJSONObject(i);

                String file = obj.getString("fileName");
                rapidView.relyFileList.add(file);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
            rapidView = null;
        }

        return rapidView;
    }

    private void readAll(){
        JSONArray fileArray;
        JSONArray viewArray;
        JSONObject obj;

        mCacheFileList = new ArrayList<RAPID_FILE>();
        mCacheViewList = new ArrayList<RAPID_FILE>();
        mCacheViewMap = new ConcurrentHashMap<String, RAPID_VIEW>();

        obj = getNativeConfig();
        if( obj == null ){
            return;
        }

        fileArray = getJSONArray(obj, ATTR_FILE_LIST);
        if( fileArray == null ){
            fileArray = new JSONArray();
        }

        viewArray = getJSONArray(obj, ATTR_VIEW_LIST);
        if( viewArray == null ){
            viewArray = new JSONArray();
        }

        addExistFileToList(mCacheFileList, fileArray);
        addExistFileToList(mCacheViewList, viewArray);

        createCacheViewMap(mCacheFileList, mCacheViewList);
    }

    private void createCacheViewMap(List<RAPID_FILE> listFile, List<RAPID_FILE> listView){

        if( mCacheViewMap == null ){
            mCacheViewMap = new ConcurrentHashMap<String, RAPID_VIEW>();
        }

        mCacheViewMap.clear();

        if( listView == null || listFile == null ){
            return;
        }

        for( int i = 0; i < listView.size(); i++ ){

            RAPID_VIEW view = readView(listView.get(i).name);

            if( view == null || !isViewExist(view) ){
                if( view == null ){
                    XLog.d(RapidConfig.RAPID_ERROR_TAG, "VIEW为空" );
                }
                else{
                    XLog.d(RapidConfig.RAPID_ERROR_TAG, "VIEW不存在：" + view.name == null ? "null" : view.name);
                }

            }

            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "添加视图到已鉴定存在的视图列表：" + view.name);
            mCacheViewMap.put(view.name, view);
        }
    }

    private boolean isFileExist(String name){
        return FileUtil.isFileExists(FileUtil.getRapidDir() + name);
    }

    private void addExistFileToList(List<RAPID_FILE> list, JSONArray array){
        if( list == null || array == null ){
            return;
        }

        for( int i = 0; i < array.length(); i++ ){
            try{
                JSONObject itemObj = array.getJSONObject(i);
                RAPID_FILE fileObj = new RAPID_FILE();

                fileObj.name = itemObj.getString(ATTR_NAME);
                fileObj.version = itemObj.getString(ATTR_VERSION);
                fileObj.md5 = itemObj.getString(ATTR_MD5);

                if( !FileUtil.isFileExists(FileUtil.getRapidDir() + fileObj.name) ){
                    continue;
                }

                list.add(fileObj);
            }
            catch (JSONException e){
                continue;
            }
        }
    }

    private boolean putConfig(JSONObject obj){
        boolean bRet;

        if( obj == null ){
            return false;
        }

        bRet = FileUtil.write2File(obj.toString().getBytes(),
                FileUtil.getRapidConfigDir() + CONFIG_NAME);


        return bRet;
    }

    private JSONObject getNativeConfig(){
        JSONObject obj = null;
        byte[] content;

        ByteArrayOutputStream stream = null;

        try {
            stream = new ByteArrayOutputStream();

            if( !FileUtil.readFile(FileUtil.getRapidConfigDir() + CONFIG_NAME, stream) ){
                return null;
            }

            content = stream.toByteArray();
            if( content == null ){
                return null;
            }

            obj = new JSONObject(new String(content, "UTF-8"));
        }
        catch (Exception e){
            e.printStackTrace();
            obj = null;
        }
        finally {
            if( stream != null ){
                try {
                    stream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return obj;
    }


    private void updateExistFile(JSONObject nativeObj,
                                 JSONArray newFileArray,
                                 Map<String, String> deleteFileMap){
        JSONArray oldFileArray;

        try{
            oldFileArray = nativeObj.getJSONArray(ATTR_FILE_LIST);
        }
        catch (JSONException e){
            return;
        }

        for( int i = 0; i < oldFileArray.length(); i++ ){
            try{
                JSONObject itemObj = oldFileArray.getJSONObject(i);
                String name = itemObj.getString(ATTR_NAME);

                if( deleteFileMap.get(name) != null ||
                    !isFileExist(name) ){
                    continue;
                }

                newFileArray.put(itemObj);
            }
            catch (JSONException e){
                continue;
            }
        }
    }

    private void updateExistView(JSONObject nativeObj,
                                 JSONArray newViewArray,
                                 Map<String, String> deleteViewMap){
        JSONArray oldViewArray;

        try{
            oldViewArray = nativeObj.getJSONArray(ATTR_VIEW_LIST);
        }
        catch (JSONException e){
            return;
        }

        for( int i = 0; i < oldViewArray.length(); i++ ){
            try{
                JSONObject itemObj = oldViewArray.getJSONObject(i);
                String name = itemObj.getString(ATTR_NAME);

                if( deleteViewMap.get(name) != null ||
                    !isFileExist(name) ){
                    continue;
                }

                newViewArray.put(itemObj);
            }
            catch (JSONException e){
                continue;
            }
        }
    }

    private void updateNewFile(JSONArray newFileArray,
                               List<RAPID_FILE> addFileList){

        for( int i = 0; i < addFileList.size(); i++ ){
            JSONObject itemObj = new JSONObject();

            try{
                itemObj.put(ATTR_NAME, addFileList.get(i).name);
                itemObj.put(ATTR_VERSION, addFileList.get(i).version);
                itemObj.put(ATTR_MD5, addFileList.get(i).md5);
            }
            catch (JSONException e){
                continue;
            }

            newFileArray.put(itemObj);
        }
    }

    private void updateNewView(JSONArray newViewArray,
                               List<RAPID_FILE> addViewList){

        for( int i = 0; i < addViewList.size(); i++ ){
            JSONObject itemObj = new JSONObject();

            try{
                itemObj.put(ATTR_NAME, addViewList.get(i).name);
                itemObj.put(ATTR_VERSION, addViewList.get(i).version);
                itemObj.put(ATTR_MD5, addViewList.get(i).md5);
            }
            catch (JSONException e){
                continue;
            }

            newViewArray.put(itemObj);
        }
    }

    private JSONArray getJSONArray(JSONObject obj, String NAME){
        JSONArray array;

        try{
            array = obj.getJSONArray(NAME);
        }
        catch (JSONException e){
            array = null;
        }

        return array;
    }
}
