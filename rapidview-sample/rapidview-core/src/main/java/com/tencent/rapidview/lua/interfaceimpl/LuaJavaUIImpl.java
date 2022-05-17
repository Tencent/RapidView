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
package com.tencent.rapidview.lua.interfaceimpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tencent.rapidview.control.RuntimeInnerActivity;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.lua.RapidLuaCaller;
import com.tencent.rapidview.utils.HandlerUtils;
import com.tencent.rapidview.utils.ViewUtils;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * @Class LuaJavaUIImpl
 * @Desc 销毁activity的动作
 *
 * @author arlozhang
 * @date 2017.03.21
 */
public class LuaJavaUIImpl extends RapidLuaJavaObject {

    public LuaJavaUIImpl(String rapidID, IRapidView rapidView){
        super(rapidID, rapidView);
    }

    public void startActivity(String xml, LuaTable params){
        String rapidID = getParser().getRapidID();
        String limitLevel = getParser().isLimitLevel() ? "1" : "0";
        LuaValue key = LuaValue.NIL;
        LuaValue value = LuaValue.NIL;
        String strParams = "";

        Intent intent = new Intent(getParser().getContext(), RuntimeInnerActivity.class);

        if( params != null && params.istable() ){

            while(true){
                Varargs argsItem = params.next(key);
                key = argsItem.arg1();

                if( key.isnil() ){
                    break;
                }

                value = argsItem.arg(2);

                if( key.isstring() && value != null ){
                    if( strParams.compareTo("") != 0 ){
                        strParams += ",";
                    }

                    strParams += key.tostring() + ":" + value.toString();
                }
            }
        }

        if( rapidID == null ){
            rapidID = "";
        }

        if( xml == null ){
            xml = "";
        }

        intent.putExtra("rid", rapidID);
        intent.putExtra("xml", xml);
        intent.putExtra("limitlevel", limitLevel);

        if( strParams.compareTo("") != 0 ){
            intent.putExtra("params", strParams);
        }

        if(!(getParser().getContext() instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        getParser().getContext().startActivity(intent);
    }

    public void finish(){
        if( mRapidView == null ){
            return;
        }

        Context context = mRapidView.getParser().getContext();

        if( context instanceof Activity ){
            ((Activity) context).finish();
        }
    }

    public void delayRun(long milliSec, final LuaFunction function){
        if( function == null ){
            return;
        }

        HandlerUtils.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RapidLuaCaller.getInstance().call(function);
            }
        }, milliSec);
    }

    public void postRun(final LuaFunction function){
        if( function == null ){
            return;
        }

        HandlerUtils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                RapidLuaCaller.getInstance().call(function);
            }
        });
    }


    public int dip2px(int dip){
        return ViewUtils.dip2px(mRapidView.getParser().getContext(), dip);
    }


    public int px2dip(int px){
        return ViewUtils.px2dip(mRapidView.getParser().getContext(), px);
    }
}
