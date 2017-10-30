package com.tencent.rapidview.lua.interfaceimpl;

import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.Base64;
import com.tencent.rapidview.deobfuscated.IBytes;

/**
 * @Class LuaJavaBase64
 * @Desc base64工具类
 *
 * @author arlozhang
 * @date 2017.02.20
 */
public class LuaJavaBase64 extends RapidLuaJavaObject {

    public LuaJavaBase64(String rapidID, IRapidView rapidView){
        super(rapidID, rapidView);
    }

    public IBytes decode(String str, String flags){
        IBytes bytes = null;
        try{
            if( str == null ){
                return bytes;
            }

            if( flags != null && flags.compareToIgnoreCase("url_safe") == 0 ){
                bytes = new Bytes(Base64.decode(str, Base64.NO_PADDING));
            }
            else{
                bytes = new Bytes(Base64.decode(str, Base64.DEFAULT));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return bytes;
    }

    public String encode(IBytes bytes, String flags){
        String ret = "";

        if( bytes == null || bytes.getArrayByte() == null ){
            return ret;
        }

        try{
            if( flags == null || flags.compareToIgnoreCase("default") == 0 ){
                ret = Base64.encodeToString(bytes.getArrayByte(), Base64.DEFAULT);
            }
            else if( flags.compareToIgnoreCase("no_padding") == 0 ){
                ret = Base64.encodeToString(bytes.getArrayByte(), Base64.NO_PADDING);
            }
            else if( flags.compareToIgnoreCase("no_wrap") == 0 ){
                ret = Base64.encodeToString(bytes.getArrayByte(), Base64.NO_WRAP);
            }
            else if( flags.compareToIgnoreCase("crlf") == 0 ){
                ret = Base64.encodeToString(bytes.getArrayByte(), Base64.CRLF);
            }
            else if( flags.compareToIgnoreCase("url_safe") == 0 ){
                ret = Base64.encodeToString(bytes.getArrayByte(), Base64.URL_SAFE);
            }
            else {
                ret = Base64.encodeToString(bytes.getArrayByte(), Base64.DEFAULT);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return ret;
    }
}
