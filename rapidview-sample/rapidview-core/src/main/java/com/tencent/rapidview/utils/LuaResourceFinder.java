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
import com.tencent.rapidview.framework.RapidPool;

import org.luaj.vm2.lib.ResourceFinder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @Class LuaResourceFinder
 * @Desc 根据不同的实际状态决定Lua资源从哪里读取
 *
 * @author arlozhang
 * @date 2017.02.14
 */
public class LuaResourceFinder implements ResourceFinder{

    private String mRapidID = null;

    private boolean mLimitLevel = false;

    public void setRapidID(String rapidID){
        mRapidID = rapidID;
    }

    public void setLimitLevel(boolean limitLevel){
        mLimitLevel = limitLevel;
    }

    @Override
    public InputStream findResource(String filename){
        InputStream binaryFile = null;
        byte[] content = null;

        if( RapidStringUtils.isEmpty(filename) ){
            return binaryFile;
        }

        if(RapidConfig.DEBUG_MODE){
            content = RapidFileLoader.getInstance().getBytes(filename, RapidFileLoader.PATH.enum_debug_path);
            if( content != null ){
                binaryFile = new ByteArrayInputStream(content);

                return binaryFile;
            }
        }

        if( !RapidStringUtils.isEmpty(mRapidID) ){
            if( filename.contains("../") && mLimitLevel ){
                return null;
            }

            content = RapidFileLoader.getInstance().getBytes(mRapidID + "/" + filename, RapidFileLoader.PATH.enum_sandbox_path);

            if( content != null ){

                binaryFile = new ByteArrayInputStream(content);
                return binaryFile;
            }
        }

        if( mLimitLevel ){
            return binaryFile;
        }

        content = RapidPool.getInstance().getFile(filename, true);
        if( content != null ){
            binaryFile = new ByteArrayInputStream(content);

            return binaryFile;
        }

        content = RapidAssetsLoader.getInstance().get(RapidPool.getInstance().getContext(), filename);
        if( content != null ){
            binaryFile = new ByteArrayInputStream(content);
        }

        return binaryFile;
    }
}
