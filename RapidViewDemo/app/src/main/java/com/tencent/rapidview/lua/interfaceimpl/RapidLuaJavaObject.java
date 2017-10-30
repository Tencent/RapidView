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
