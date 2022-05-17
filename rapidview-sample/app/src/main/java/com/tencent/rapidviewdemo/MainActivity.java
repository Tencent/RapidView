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
package com.tencent.rapidviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.rapidview.RapidLoader;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.param.RelativeLayoutParams;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.utils.HandlerUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/11/22.
 */

public class MainActivity extends Activity {

    private IRapidView mRapidView = null;

    private static MainActivity msInstance;

    public static MainActivity getInstance(){
        return msInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        msInstance = this;

        Map<String, Var> map = new ConcurrentHashMap<String, Var>();

        map.put("text_1", new Var("init_text"));

        mRapidView = RapidLoader.load(
                RapidConfig.VIEW.native_demo_view.toString(),
                HandlerUtils.getMainHandler(),
                this,
                RelativeLayoutParams.class,
                map,
                null);

        setContentView(mRapidView.getView(), mRapidView.getParser().getParams().getLayoutParams());

        mRapidView.getParser().getBinder().update("text_2", "update_text");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( mRapidView == null ){
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        mRapidView.getParser().notify(RapidParserObject.EVENT.enum_onactivityresult, null, requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if( mRapidView == null ){
            return;
        }

        mRapidView.getParser().notify(RapidParserObject.EVENT.enum_pause, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( mRapidView == null ){
            return;
        }

        mRapidView.getParser().notify(RapidParserObject.EVENT.enum_destroy, null);
    }
}
