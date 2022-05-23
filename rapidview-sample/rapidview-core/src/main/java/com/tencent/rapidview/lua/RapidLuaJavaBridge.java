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
package com.tencent.rapidview.lua;

import android.graphics.Bitmap;

import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.deobfuscated.IBytes;
import com.tencent.rapidview.deobfuscated.ILuaJavaInterface;
import com.tencent.rapidview.deobfuscated.IRapidParser;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.deobfuscated.utils.IRapidFeedsCacheQueue;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaNetwork;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaNetworkState;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaSystem;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaTestEngine;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaUIImpl;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaBase64;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaCreate;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaMd5;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaPicture;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaRequest;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaShare;
import com.tencent.rapidview.lua.interfaceimpl.LuaJavaViewWrapper;
import com.tencent.rapidview.lua.interfaceimpl.RapidLuaJavaObject;
import com.tencent.rapidview.utils.RapidFeedsCacheQueue;
import com.tencent.rapidview.utils.XLog;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Class RapidLuaJavaBridge
 * @Desc 提供给lua调用的接口实现。java不让实现多继承挺烦人的，一大堆接口只能实现到一起。在这种情况下，只能勉为其
 *       难的使用委托的方式了，注意：实现代码不要写在这个类里。
 *
 * @author arlozhang
 * @date 2016.12.06
 */
public class RapidLuaJavaBridge implements ILuaJavaInterface {

    private String mRapidID;

    private IRapidView mRapidView = null;

    private List<RapidLuaJavaObject> mListNotify = new ArrayList<RapidLuaJavaObject>();

    public RapidLuaJavaBridge(String rapidID){
        mRapidID = rapidID;
    }

    public void setRapidView(IRapidView rapidView){
        mRapidView = rapidView;
    }

    public void notify(IRapidParser.EVENT event, StringBuilder ret, Object... args){
        Iterator<RapidLuaJavaObject> iter = mListNotify.iterator();

        for( int i = 0; i < mListNotify.size(); i++ ){
            RapidLuaJavaObject obj = mListNotify.get(i);
            if( obj == null || obj.isUnRegister() ){
                continue;
            }

            obj.notify(event, ret, args);
        }

        while( iter.hasNext() ){
            RapidLuaJavaObject obj = iter.next();

            if( obj == null || obj.isUnRegister() ){
                iter.remove();
            }
        }
    }

    private void register(RapidLuaJavaObject listener){
        mListNotify.add(listener);
    }

    @Override
    public LuaValue create(String objName){
        LuaJavaCreate obj = new LuaJavaCreate(mRapidID, mRapidView);

        return  CoerceJavaToLua.coerce(obj.create(objName));
    }

    @Override
    public LuaValue create(String objName, Object args0){
        LuaJavaCreate obj = new LuaJavaCreate(mRapidID, mRapidView);

        return CoerceJavaToLua.coerce(obj.create(objName, args0));
    }

    @Override
    public LuaValue create(String objName, Object args0, Object args1){
        LuaJavaCreate obj = new LuaJavaCreate(mRapidID, mRapidView);

        return CoerceJavaToLua.coerce(obj.create(objName, args0, args1));
    }

    @Override
    public LuaValue create(String objName, Object args0, Object args1, Object args2){
        LuaJavaCreate obj = new LuaJavaCreate(mRapidID, mRapidView);

        return CoerceJavaToLua.coerce(obj.create(objName, args0, args1, args2));
    }

    @Override
    public LuaValue create(String objName, Object args0, Object args1, Object args2, Object args3){
        LuaJavaCreate obj = new LuaJavaCreate(mRapidID, mRapidView);

        return CoerceJavaToLua.coerce(obj.create(objName, args0, args1, args2, args3));
    }

    @Override
    public LuaValue create(String objName, Object args0, Object args1, Object args2, Object args3, Object args4){
        LuaJavaCreate obj = new LuaJavaCreate(mRapidID, mRapidView);

        return CoerceJavaToLua.coerce(obj.create(objName, args0, args1, args2, args3, args4));
    }

    @Override
    public LuaValue create(String objName, Object args0, Object args1, Object args2, Object args3, Object args4, Object args5){
        LuaJavaCreate obj = new LuaJavaCreate(mRapidID, mRapidView);

        return CoerceJavaToLua.coerce(obj.create(objName, args0, args1, args2, args3, args4, args5));
    }

    @Override
    public boolean request(String url, IBytes data, LuaTable header, String method, LuaFunction succeedListener, LuaFunction failedListener){
        LuaJavaRequest request = new LuaJavaRequest(mRapidID, mRapidView, url, data.getArrayByte(), header, method, succeedListener, failedListener);

        return request.request();
    }

    @Override
    public boolean request(String url, String data, LuaTable header, String method, LuaFunction succeedListener, LuaFunction failedListener){
        LuaJavaRequest request = new LuaJavaRequest(mRapidID, mRapidView, url, data, header, method, succeedListener, failedListener);

        return request.request();
    }

    @Override
    public boolean request(String url, LuaTable data, LuaTable header, String method, LuaFunction succeedListener, LuaFunction failedListener){
        LuaJavaRequest request = new LuaJavaRequest(mRapidID, mRapidView, url, data, header, method, succeedListener, failedListener);

        return request.request();
    }

    @Override
    public boolean request(int cmdID, LuaTable data, LuaTable params, LuaFunction listener){
        LuaJavaTestEngine engine = new LuaJavaTestEngine(mRapidID, mRapidView);

        return engine.request(cmdID, data, params, listener);
    }

    @Override
    public IRapidFeedsCacheQueue createFeedsCacheQueue(int cacheCount, Object reqStub){
        return new RapidFeedsCacheQueue(cacheCount, reqStub);
    }


    @Override
    public void Log(String tag, String value){
         XLog.d(tag, value);
    }

    @Override
    public LuaValue addView(String xml, String parentID, String above, RapidDataBinder binder, Object data){

        LuaJavaViewWrapper viewWrapper = new LuaJavaViewWrapper(mRapidID, mRapidView);


        return viewWrapper.addView(xml, parentID, above, binder, data, null);
    }

    @Override
    public LuaValue addView(String xml, String parentID, String above, RapidDataBinder binder, Object data, IRapidActionListener listener){

        LuaJavaViewWrapper viewWrapper = new LuaJavaViewWrapper(mRapidID, mRapidView);


        return viewWrapper.addView(xml, parentID, above, binder, data, listener);
    }

    @Override
    public LuaValue loadView(String name, String params, Object data){
        LuaJavaViewWrapper viewWrapper = new LuaJavaViewWrapper(mRapidID, mRapidView);

        if( name == null || ( !name.contains(".") && mRapidView.getParser().isLimitLevel() ) ){
            return null;
        }

        return viewWrapper.loadView(name, params, data, null);
    }

    @Override
    public LuaValue loadView(String name, String params, Object data, IRapidActionListener listener){
        LuaJavaViewWrapper viewWrapper = new LuaJavaViewWrapper(mRapidID, mRapidView);

        if( name == null || ( !name.contains(".") && mRapidView.getParser().isLimitLevel() ) ){
            return null;
        }

        return viewWrapper.loadView(name, params, data, listener);
    }

    @Override
    public LuaValue removeView(String id){
        LuaJavaViewWrapper viewWrapper = new LuaJavaViewWrapper(mRapidID, mRapidView);

        return viewWrapper.removeView(id);
    }

    @Override
    public IBytes decode(String str, String flags){
        LuaJavaBase64 base64 = new LuaJavaBase64(mRapidID, mRapidView);

        return base64.decode(str, flags);
    }

    @Override
    public String encode(IBytes bytes, String flags){
        LuaJavaBase64 base64 = new LuaJavaBase64(mRapidID, mRapidView);

        return base64.encode(bytes, flags);
    }

    @Override
    public void takePicture(LuaTable params, LuaFunction succeedListener, LuaFunction failedListener){
        LuaJavaPicture pictureChooser = new LuaJavaPicture(mRapidID, mRapidView);
        register(pictureChooser);
        pictureChooser.takePicture(params, succeedListener, failedListener);
    }

    @Override
    public void choosePicture(LuaTable params, LuaFunction succeedListener, LuaFunction failedListener){
        LuaJavaPicture pictureChooser = new LuaJavaPicture(mRapidID, mRapidView);
        register(pictureChooser);
        pictureChooser.choosePicture(params, succeedListener, failedListener);

    }


    @Override
    public Bitmap getBitmapFromBytes(IBytes bytes){
        LuaJavaPicture pictureChooser = new LuaJavaPicture(mRapidID, mRapidView);

        return pictureChooser.getBitmapFromBytes(bytes);
    }


    @Override
    public IBytes getBytesFromBitmap(Bitmap bitmap){
        LuaJavaPicture pictureChooser = new LuaJavaPicture(mRapidID, mRapidView);

        return pictureChooser.getBytesFromBitmap(bitmap);
    }


    @Override
    public void shareImageToWX(Bitmap bmp, String scene, LuaFunction succeedListener, LuaFunction failedListener){
        LuaJavaShare shareWraper = new LuaJavaShare(mRapidID, mRapidView);

        shareWraper.shareImageToWX(bmp, scene, succeedListener, failedListener);
    }


    @Override
    public void shareTextToWX(String text, String scene, LuaFunction succeedListener, LuaFunction failedListener){
        LuaJavaShare shareWraper = new LuaJavaShare(mRapidID, mRapidView);

        shareWraper.shareTextToWX(text, scene, succeedListener, failedListener);
    }

    @Override
    public void savePicture(Bitmap bitmap) {
        LuaJavaPicture pictureChooser = new LuaJavaPicture(mRapidID, mRapidView);

        pictureChooser.savePicture(bitmap);
    }

    @Override
    public IBytes toMD5Bytes(String source){
        LuaJavaMd5 md5 = new LuaJavaMd5(mRapidID, mRapidView);

        return md5.toMD5Bytes(source);
    }

    @Override
    public IBytes toMD5Bytes(IBytes source){
        LuaJavaMd5 md5 = new LuaJavaMd5(mRapidID, mRapidView);

        return md5.toMD5Bytes(source);
    }

    @Override
    public String toMD5(String source){
        LuaJavaMd5 md5 = new LuaJavaMd5(mRapidID, mRapidView);

        return md5.toMD5(source);
    }

    @Override
    public void finish(){
        LuaJavaUIImpl finish = new LuaJavaUIImpl(mRapidID, mRapidView);

        finish.finish();
    }

    @Override
    public void startActivity(String xml, LuaTable params){
        LuaJavaUIImpl activity = new LuaJavaUIImpl(mRapidID, mRapidView);

        activity.startActivity(xml, params);
    }

    @Override
    public void delayRun(long milliSec, LuaFunction function){
        LuaJavaUIImpl runner = new LuaJavaUIImpl(mRapidID, mRapidView);

        runner.delayRun(milliSec, function);
    }

    @Override
    public void postRun( LuaFunction function){
        LuaJavaUIImpl runner = new LuaJavaUIImpl(mRapidID, mRapidView);

        runner.postRun(function);
    }

    @Override
    public int dip2px(int dip){
        LuaJavaUIImpl uiImpl = new LuaJavaUIImpl(mRapidID, mRapidView);

        return uiImpl.dip2px(dip);
    }

    @Override
    public int px2dip(int px){
        LuaJavaUIImpl uiImpl = new LuaJavaUIImpl(mRapidID, mRapidView);

        return uiImpl.px2dip(px);
    }

    @Override
    public boolean isNetworkActive(){
        LuaJavaNetworkState state = new LuaJavaNetworkState(mRapidID, mRapidView);

        return state.isNetworkActive();
    }

    @Override
    public boolean isWap(){
        LuaJavaNetworkState state = new LuaJavaNetworkState(mRapidID, mRapidView);

        return state.isNetworkActive();
    }

    @Override
    public boolean isWifi(){
        LuaJavaNetworkState state = new LuaJavaNetworkState(mRapidID, mRapidView);

        return state.isNetworkActive();
    }

    @Override
    public boolean is2G(){
        LuaJavaNetworkState state = new LuaJavaNetworkState(mRapidID, mRapidView);

        return state.isNetworkActive();
    }

    @Override
    public boolean is3G(){
        LuaJavaNetworkState state = new LuaJavaNetworkState(mRapidID, mRapidView);

        return state.isNetworkActive();
    }

    @Override
    public boolean is4G(){
        LuaJavaNetworkState state = new LuaJavaNetworkState(mRapidID, mRapidView);

        return state.isNetworkActive();
    }

    @Override
    public String getServerTime(){
        LuaJavaSystem system = new LuaJavaSystem(mRapidID, mRapidView);

        return system.getServerTime();
    }

    @Override
    public String urlDecode(String url){
        return LuaJavaNetwork.urlDecode(url);
    }

    @Override
    public String urlEncode(String url){
        return LuaJavaNetwork.urlEncode(url);
    }
}
