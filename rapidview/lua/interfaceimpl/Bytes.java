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

import com.tencent.rapidview.deobfuscated.IBytes;

/**
 * @Class Bytes
 * @Desc 用于在lua中做字节流传输的对象
 *
 * @author arlozhang
 * @date 2017.03.01
 */
public class Bytes implements IBytes {

    private byte[] mArrayByte = null;

    Bytes(byte[] arrayByte){
        mArrayByte = arrayByte;
    }

    @Override
    public boolean isNil(){
        return mArrayByte == null;
    }
    @Override
    public byte[] getArrayByte(){
        return mArrayByte;
    }

    @Override
    public long getLength(){
        return mArrayByte.length;
    }

    @Override
    public String getString(){
        if( mArrayByte == null ){
            return "";
        }

        return new String(mArrayByte);
    }
}
