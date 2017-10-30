package com.tencent.rapidview.lua.interfaceimpl;

import com.tencent.rapidview.deobfuscated.IBytes;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.MD5;
import com.tencent.rapidview.utils.RapidStringUtils;

/**
 * @Class LuaJavaMd5
 * @Desc MD5计算
 *
 * @author arlozhang
 * @date 2017.02.20
 */
public class LuaJavaMd5 extends RapidLuaJavaObject {

    public LuaJavaMd5(String rapidID, IRapidView rapidView){
        super(rapidID, rapidView);
    }

    public IBytes toMD5Bytes(String source){
        if( RapidStringUtils.isEmpty(source) ){
            return null;
        }

        return new Bytes(MD5.toMD5Byte(source));
    }

    public IBytes toMD5Bytes(IBytes source){
        if( source == null || source.getArrayByte() == null ){
            return null;
        }

        return new Bytes(MD5.toMD5Byte(source.getArrayByte()));
    }

    public String toMD5(String source){
        if( RapidStringUtils.isEmpty(source) ){
            return null;
        }

        return MD5.toMD5(source);
    }
}
