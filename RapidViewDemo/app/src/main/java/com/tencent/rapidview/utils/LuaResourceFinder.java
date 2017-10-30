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
