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
import com.tencent.rapidview.RapidVersion;
import com.tencent.rapidview.runtime.RapidSandboxWrapper;
import com.tencent.rapidview.utils.FileUtil;
import com.tencent.rapidview.utils.RapidAssetsLoader;
import com.tencent.rapidview.utils.RapidConfigWrapper;
import com.tencent.rapidview.utils.RapidFileLoader;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.RapidThreadPool;
import com.tencent.rapidview.utils.RapidXmlLoader;
import com.tencent.rapidview.utils.XLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Class RapidPool
 * @Desc RapidView资源池，对象池，用于针对网络下拉的数据进行本地文件存储及内存缓存。同时对界面对象进行预加载
 *
 * @author arlozhang
 * @date 2015.10.16
 */
public class RapidPool {

    private Map<String, byte[]> mmapMemoryCache = new ConcurrentHashMap<String, byte[]>();

    private Lock mLock = new ReentrantLock();

    private volatile boolean mInitialized = false;

    private static RapidPool mSelf;

    private static String VERSION_FILE_NAME = "rapid_version";

    private Context mContext = null;

    Preloader mPreloader = new Preloader();

    public static class RapidFile {
        public String  name;
        public String  version;
        public byte[]  content;
        public String  md5;
        public boolean isView = false;
    }

    public static RapidPool getInstance(){

        if( mSelf == null ){
            mSelf = new RapidPool();
        }

        return mSelf;
    }

    public interface IInitializeCallback{

        void onFinish();
    }

    public Context getContext(){
        return mContext;
    }

    public boolean isInitialize(){
        return mInitialized;
    }

    public synchronized void initialize(final Context context, final IInitializeCallback callback){
        if( mInitialized ){
            return;
        }

        mInitialized = true;

        cleanResidual();

        RapidEnv.setApplication(context);

        mContext = context;

        mPreloader.setContext(context);

        RapidRuntimeCachePool.getInstance().setContext(context);

        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {
                Map<String, RapidConfigWrapper.RAPID_VIEW> viewMap = new ConcurrentHashMap<String, RapidConfigWrapper.RAPID_VIEW>();

                RapidSandboxWrapper.getInstance().initSandbox();
                RapidConfigWrapper.getInstance().clearCache();
                RapidConfigWrapper.getInstance().getAllExistView(viewMap);

                for( Map.Entry<String, RapidConfigWrapper.RAPID_VIEW> entry : viewMap.entrySet() ){

                    addExistView(entry.getValue());
                }

                RapidThreadPool.get().execute(new Runnable() {
                    @Override
                    public void run() {
                        mPreloader.initialize();
                    }
                }, 3500);

                if( callback != null ){
                    callback.onFinish();
                }
            }
        });
    }

    public synchronized RapidObject get(String viewName, String nativeXml){
        String mainXml = null;

        if( RapidConfig.DEBUG_MODE && viewName.compareTo("") != 0 ){
            mainXml = _getDebugXml(viewName);
        }

        if( mainXml == null || mainXml.compareTo("") == 0 ){
            mainXml = _getMainXml(viewName);
        }

        return mPreloader.get( RapidStringUtils.isEmpty(mainXml) ? nativeXml : mainXml);
    }

    public void update(Context context,
                       List<RapidFile> listUpdateFile,
                       List<String> listDeleteFile){

        mLock.lock();

        XLog.d(RapidConfig.RAPID_NORMAL_TAG, "开始更新本地数据");

        try{
            List<RapidConfigWrapper.RAPID_FILE> listFile = new ArrayList<RapidConfigWrapper.RAPID_FILE>();
            List<RapidConfigWrapper.RAPID_FILE> listView = new ArrayList<RapidConfigWrapper.RAPID_FILE>();

            for( int i = 0; i < listUpdateFile.size(); i++ ){
                RapidConfigWrapper.RAPID_FILE item = RapidConfigWrapper.getInstance().new RAPID_FILE();

                /* 禁止使用assets同名文件，保证随包文件随时可用，不受污染 */
                if( RapidAssetsLoader.getInstance().isFileExist(context, listUpdateFile.get(i).name) ){
                    continue;
                }

                item.name = listUpdateFile.get(i).name;
                item.version = listUpdateFile.get(i).version;
                item.md5 = listUpdateFile.get(i).md5
;
                if( listUpdateFile.get(i).isView ){
                    listView.add(item);
                }
                else{
                    listFile.add(item);
                }
            }

            if( !RapidConfigWrapper.getInstance().update(listFile, listView, listDeleteFile) ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "更新配置文件失败，中断更新");
                return;
            }

            deleteFile(listDeleteFile);
            updateFile(context, listUpdateFile);

            cacheViewList(listView);

            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "更新本地数据完毕");
        }
        catch (Exception e){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "更新本地数据异常");
            e.printStackTrace();
        }
        finally {
            mLock.unlock();
        }
    }

    public void updateLock(){
        mLock.lock();
    }

    public void updateUnlock(){
        mLock.unlock();
    }

    public byte[] getFile(String name, boolean cache){
        byte[] content = null;
        ByteArrayOutputStream stream = null;

        try {
            content = mmapMemoryCache.get(name);
            if( content != null ) {
                return content;
            }

            stream = new ByteArrayOutputStream();
            if( !FileUtil.readFile(FileUtil.getRapidDir() + name, stream) ){
                return content;
            }

            content = stream.toByteArray();
            if( content != null && cache ){
                mmapMemoryCache.put(name, content);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            content = null;
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

        return content;
    }

    private void cacheViewList(List<RapidConfigWrapper.RAPID_FILE> list){
        for( int i = 0; i < list.size(); i++ ){
            RapidConfigWrapper.RAPID_VIEW view = RapidConfigWrapper.getInstance().readView(list.get(i).name);

            if( view == null || !RapidConfigWrapper.getInstance().isViewExist(view) ){
                if( view != null ){
                    mPreloader.delete(view.mainFile);
                }
                
                continue;
            }

            addExistView(view);
        }
    }

    private void addExistView(RapidConfigWrapper.RAPID_VIEW view){
        String mainXml = view.mainFile;

        if( mainXml != null && mainXml.compareTo("") != 0 ){
            mPreloader.add(mainXml);
        }
    }

    private void deleteFile(List<String> listFile){
        RapidConfigWrapper.RAPID_FILE node = RapidConfigWrapper.getInstance().new RAPID_FILE();

        if( listFile == null || listFile.isEmpty() ) {
            return;
        }

        for( int i = 0; i < listFile.size(); i++ ){
            RapidConfigWrapper.RAPID_VIEW view;
            String name = listFile.get(i);

            if( name.contains(".") ){
                mPreloader.delete(listFile.get(i));
            }
            else{
                node.name = name;
                view = RapidConfigWrapper.getInstance().readView(node.name);

                if( view != null ){
                    mPreloader.delete(view.mainFile);
                }
            }

            deleteFile(name);

            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "已删除文件：" + listFile.get(i));
        }
    }

    private void updateFile(Context context, List<RapidFile> listFile){
        if( listFile == null || listFile.isEmpty() ) {
            return;
        }

        for( int i = 0; i < listFile.size(); i++ ){
            RapidFile file = listFile.get(i);

            /* 禁止使用assets同名文件，保证随包文件随时可用，不受污染 */
            if( RapidAssetsLoader.getInstance().isFileExist(context, file.name) ){
                continue;
            }

            updateFile(file.name, file.content, file.version);

            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "已更新文件：" + file.name + "：" + file.version);
        }
    }

    private String _getDebugXml(String viewName){
        String configString;
        JSONObject rootObject;
        String mainXml = "";

        if( viewName == null || viewName.compareTo("") == 0 ){
            return "";
        }

        configString = RapidFileLoader.getInstance().getString("rapid_debug_config.json", RapidFileLoader.PATH.enum_debug_path);
        if( configString.compareTo("") == 0 ){
            return "";
        }

        try{
            JSONArray arrayView = null;


            rootObject = new JSONObject(configString);
            arrayView = rootObject.getJSONArray("view_config");

            for( int i = 0; i < arrayView.length(); i++ ){
                JSONObject obj = arrayView.getJSONObject(i);

                if( obj.getString("name").compareToIgnoreCase(viewName) != 0 ){
                    continue;
                }

                mainXml = obj.getString("mainfile");
                break;
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return mainXml;
    }

    private String _getMainXml(String viewName){
        RapidConfigWrapper.RAPID_VIEW view = null;
        String mainXml;

        if( viewName == null || viewName.compareTo("") == 0 ){
            return "";
        }

        view = RapidConfigWrapper.getInstance().getExistView(viewName);
        if( view == null ){
            return "";
        }

        mainXml = view.mainFile;

        return mainXml;
    }

    private void updateFile(String name, byte[] content, String version){
        if( name == null || content == null || version == null ){
            return;
        }

        if( mmapMemoryCache.get(name) != null ){
            mmapMemoryCache.put(name, content);
        }

        FileUtil.deleteFile(FileUtil.getRapidDir() + name);
        FileUtil.write2File(content, FileUtil.getRapidDir() + name);

        RapidXmlLoader.self().deleteDocument(name);
    }

    private void deleteFile(String name){

        if( name == null){
            return;
        }

        FileUtil.deleteFile(FileUtil.getRapidDir() + name);

        RapidXmlLoader.self().deleteDocument(name);
    }

    private void cleanResidual(){
        String version = RapidFileLoader.getInstance().getString(VERSION_FILE_NAME, RapidFileLoader.PATH.enum_config_path);

        if( version.compareTo("") == 0 ){
            createVersionFile();
            return;
        }

        try{
            int nVer = Integer.parseInt(version);

            if( nVer == RapidVersion.RAPID_ENGINE_VERSION){
                return;
            }

            if( nVer < RapidVersion.RAPID_ENGINE_VERSION){
                createVersionFile();
            }
            else if( nVer > RapidVersion.RAPID_ENGINE_VERSION){
                deleteRapidDir();
                createVersionFile();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            deleteRapidDir();
            createVersionFile();
        }
    }

    private void deleteRapidDir(){
        FileUtil.deleteFileOrDir(FileUtil.getRapidDir());
        FileUtil.deleteFileOrDir(FileUtil.getRapidConfigDir());
    }

    private void createVersionFile(){
        String version = Integer.toString(RapidVersion.RAPID_ENGINE_VERSION);
        FileUtil.write2File(version.getBytes(), FileUtil.getRapidConfigDir() + VERSION_FILE_NAME);
    }

    private class Preloader {

        private Map<String, RapidObject> mRapidProloadMap = new ConcurrentHashMap<String, RapidObject>();

        private Lock mLock = new ReentrantLock();

        private Map<String, Boolean> mWaitLoadMap = new ConcurrentHashMap<String, Boolean>();

        private boolean mIsInitialized = false;

        public Context mContext = null;

        public void setContext(Context context){
            mContext = context;
        }

        public synchronized void initialize() {

            mLock.lock();

            try{
                EnumSet<RapidConfig.VIEW> nativeSet = EnumSet.allOf(RapidConfig.VIEW.class);

                for (RapidConfig.VIEW view : nativeSet) {
                    RapidObject newCenter = new RapidObject();
                    String xml = RapidConfig.msMapViewNaitve.get(view.toString());

                    if( RapidStringUtils.isEmpty(xml) ){
                        XLog.d(RapidConfig.RAPID_ERROR_TAG, "初始化时未发现视图默认XML：" + view.toString());
                        continue;
                    }

                    if( mRapidProloadMap.get(xml) != null ){
                        continue;
                    }

                    newCenter.initialize(mContext, "", null, false, xml, null, null, false);

                    mRapidProloadMap.put(xml, newCenter);

                    try{
                        Thread.sleep(50);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                for( Map.Entry<String, Boolean> entry : mWaitLoadMap.entrySet() ){
                    RapidObject newCenter = new RapidObject();
                    String xml = entry.getKey();

                    newCenter.initialize(mContext, "", null, false, xml, null, null, false);

                    mRapidProloadMap.put(xml, newCenter);
                    XLog.d(RapidConfig.RAPID_NORMAL_TAG, "已添加视图到缓存池：" + xml);

                    try{
                        Thread.sleep(50);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                mWaitLoadMap.clear();

                mIsInitialized = true;
            }
            finally {
                mLock.unlock();
            }
        }

        private RapidObject get(final String rapidXml) {
            RapidObject oldCenter = null;
            RapidObject newCenter = null;

            XLog.d(RapidConfig.RAPID_NORMAL_TAG, "读取视图：" + rapidXml);

            if( RapidConfig.DEBUG_MODE &&
                rapidXml.compareTo("") != 0 &&
                FileUtil.isFileExists(FileUtil.getRapidDebugDir() + rapidXml) ){
                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "尝试读取调试模式文件");

                newCenter = new RapidObject();

                newCenter.initialize(mContext, null, null, false, rapidXml, null, null, false);

                return newCenter;
            }

            if (rapidXml != null && rapidXml.compareTo("") != 0) {
                oldCenter = mRapidProloadMap.remove(rapidXml);
            }

            if( oldCenter != null && !oldCenter.isInitialized() ) {
                newCenter = oldCenter;
                mRapidProloadMap.put(rapidXml, newCenter);
                oldCenter = null;
            }

            if (oldCenter == null) {
                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "未发现缓存，准备全新加载视图：" + rapidXml);
                oldCenter = new RapidObject();
                oldCenter.initialize(mContext, null, null, false, rapidXml, null, null, true);
            }

            if( mIsInitialized && newCenter == null){
                add(rapidXml);
            }

            return oldCenter;
        }

        public synchronized void delete(String xml) {
            if( xml == null ){
                return;
            }

            mRapidProloadMap.remove(xml);
        }

        public synchronized void add(final String xml) {

            mLock.lock();

            try{
                if( mIsInitialized == false ){
                    mWaitLoadMap.put(xml, true);
                    return;
                }

                if( mContext == null){
                    return;
                }

                RapidObject newCenter = new RapidObject();

                newCenter.initialize(mContext, null, null, false, xml, null, null, false);

                mRapidProloadMap.put(xml, newCenter);
                XLog.d(RapidConfig.RAPID_NORMAL_TAG, "已添加视图到缓存池：" + xml);
            }
            finally {
                mLock.unlock();
            }
        }
    }
}
