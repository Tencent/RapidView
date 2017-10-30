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
package com.tencent.rapidview.control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidParser;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.runtime.RuntimeView;
import com.tencent.rapidview.utils.RapidStringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RuntimeInnerActivity
 * @Desc 实时activity内部跳转容器，这里删除了原来的loading和失败的view，需要根据需要定制添加进来。
 *
 * @author arlozhang
 * @date 2017.04.17
 */
public class RuntimeInnerActivity extends Activity {

    private IRapidView mRapidView = null;

    private RelativeLayout mContainer = null;

    private RuntimeView mView = null;

    private String mRapidID = "";

    private String mXmlName = "";

    private String mParams = "";

    private int mLimitLevel = -1;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        initParams();

        super.onCreate(saveInstanceState);

        initView();
        loadView();
    }

    private void initParams(){
        Intent intent = getIntent();

        try{
            mRapidID = intent.getStringExtra("rid");

            mXmlName = intent.getStringExtra("xml");

            mLimitLevel = Integer.parseInt(intent.getStringExtra("limitlevel"));

            if( intent.hasExtra("params") ){
                mParams = intent.getStringExtra("params");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if( mRapidID == null ){
            mRapidID = "";
        }

        if( mXmlName == null ){
            mXmlName = "";
        }
    }

    private void initView(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer = new RelativeLayout(this);

        setContentView(mContainer, params);
    }

    private void loadView(){
        mView = new RuntimeView(this);

        if( mParams.compareTo("") != 0 ){
            Map<String, String> paramsMap = RapidStringUtils.stringToMap(mParams);

            for( Map.Entry<String, String> entry : paramsMap.entrySet() ){
                mView.setParam(entry.getKey(), new Var(entry.getValue()));
            }
        }

        mView.loadDirect(mRapidID, mXmlName, mLimitLevel, new ConcurrentHashMap<String, Var>(), new RuntimeView.IListener() {
            @Override
            public void onFailed() {
                failed();
            }

            @Override
            public void onSucceed(IRapidView rapidView) {
                mRapidView = rapidView;

                succeed();
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mContainer.addView(mView, params);
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

    @Override
    protected void onResume() {
        super.onResume();

        if( mRapidView == null ){
            return;
        }

        mRapidView.getParser().notify(RapidParserObject.EVENT.enum_resume, null);
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
    public void onBackPressed() {
        StringBuilder ret = new StringBuilder();

        if( mRapidView == null ){
            super.onBackPressed();
            return;
        }

        mRapidView.getParser().notify(IRapidParser.EVENT.enum_key_back, ret);

        if( ret.toString().contains("true") ){
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }


    private void failed(){
        mContainer.setVisibility(View.INVISIBLE);
    }

    private void succeed(){
        mContainer.setVisibility(View.VISIBLE);
    }
}
