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
