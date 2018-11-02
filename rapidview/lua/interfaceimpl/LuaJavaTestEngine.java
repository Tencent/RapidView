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

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.lua.RapidLuaCaller;
import com.tencent.rapidview.server.TestServer;
import com.tencent.rapidview.utils.HandlerUtils;
import com.tencent.rapidview.utils.RapidDataUtils;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;

import java.util.List;
import java.util.Map;

/**
 * @Class LuaJavaTestEngine
 * @Desc 假的网络请求
 *
 * @author arlozhang
 * @date 2017.11.02
 */
public class LuaJavaTestEngine extends RapidLuaJavaObject {

    private LuaFunction mListener = null;

    private Map<String, Var> mParams = null;

    private TestServer mEngine = new TestServer();

    public LuaJavaTestEngine(String photonID, IRapidView photonView) {
        super(photonID, photonView);
    }

    public void setParams(Map<String, Var> params){
        mParams = params;
    }


    public boolean request(int cmdID, LuaTable data, LuaTable params, LuaFunction listener){

        mListener = listener;

        mParams = RapidDataUtils.table2Map(params);

        mEngine.request(cmdID, data, new TestServer.IListener() {

            @Override
            public void onFinish(final boolean bSucc, final List<String> viewNameList, final List<Map<String, Var>> dataList) {
                HandlerUtils.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if( mListener == null ){
                            return;
                        }

                        RapidLuaCaller.getInstance().call(mListener, bSucc, viewNameList, dataList);
                    }
                });
            }
        });

        return true;
    }
}
