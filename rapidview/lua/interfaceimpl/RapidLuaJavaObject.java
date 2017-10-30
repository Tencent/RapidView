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

import com.tencent.rapidview.deobfuscated.IRapidParser;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.parser.RapidParserObject;

/**
 * @Class RapidLuaJavaObject
 * @Desc 实现一个接口基类
 *
 * @author arlozhang
 * @date 2017.02.20
 */
public abstract class RapidLuaJavaObject {

    protected String mRapidID = "";

    protected IRapidView mRapidView = null;

    protected boolean mUnRegister = false;

    protected RapidLuaJavaObject(String rapidID, IRapidView rapidView){
        mRapidID = rapidID;
        mRapidView = rapidView;
    }

    public void notify(IRapidParser.EVENT event, StringBuilder ret, Object... args){}

    public boolean isUnRegister(){
        return mUnRegister;
    }

    protected RapidParserObject getParser(){
        if( mRapidView == null ){
            return null;
        }

        return mRapidView.getParser();
    }
}
