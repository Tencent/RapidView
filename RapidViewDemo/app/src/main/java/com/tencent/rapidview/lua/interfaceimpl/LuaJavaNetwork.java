package com.tencent.rapidview.lua.interfaceimpl;

import com.tencent.rapidview.deobfuscated.IRapidView;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @Class LuaJavaNetwork
 * @Desc 网络相关操作
 *
 * @author arlozhang
 * @date 2017.04.27
 */
public class LuaJavaNetwork extends RapidLuaJavaObject {

    public LuaJavaNetwork(String rapidID, IRapidView rapidView){
        super(rapidID, rapidView);
    }


    public static String urlDecode(String url){
        try{
            return URLDecoder.decode(url, "UTF-8");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return url;
    }


    public static String urlEncode(String url){
        try{
            return URLEncoder.encode(url, "UTF-8");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return url;
    }
}
