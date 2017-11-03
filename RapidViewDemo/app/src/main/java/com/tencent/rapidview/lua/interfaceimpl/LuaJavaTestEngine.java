package com.tencent.rapidview.lua.interfaceimpl;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.lua.RapidLuaCaller;
import com.tencent.rapidview.server.TestServer;
import com.tencent.rapidview.utils.HandlerUtils;

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

    private TestServer mEngine = new TestServer();

    public LuaJavaTestEngine(String photonID, IRapidView photonView) {
        super(photonID, photonView);
    }

    public boolean request(int cmdID, LuaTable data, LuaFunction listener){

        mListener = listener;

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
