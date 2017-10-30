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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Class RapidFileLoader
 * @Desc 读取文件数据的类
 *
 * @author arlozhang
 * @date 2015.05.24
 */
public class RapidFileLoader {

    private static RapidFileLoader mSelf;

    public enum PATH{
        enum_normal_path,
        enum_debug_path,
        enum_config_path,
        enum_sandbox_path,
    }

    public static RapidFileLoader getInstance(){

        if( mSelf == null ){
            mSelf = new RapidFileLoader();
        }

        return mSelf;
    }

    public String getString(String name){
        return getString(name , PATH.enum_normal_path);
    }

    public String getString(String name, PATH path){
        byte[] bytes = getBytes(name, path);
        String ret = "";

        if( bytes == null ){
            return "";
        }

        try{
            ret = new String(bytes, "UTF-8");
        }
        catch ( Exception e){
            e.printStackTrace();
        }

        return ret;
    }

    public byte[] getBytes(String name){
        return getBytes(name, PATH.enum_normal_path);
    }

    public byte[] getBytes(String name, PATH path){
        byte[] content = null;
        ByteArrayOutputStream stream = null;
        String filePath;

        if( name == null ){
            return null;
        }

        try {
            switch (path) {
                case enum_normal_path:
                    filePath = FileUtil.getRapidDir() + name;
                    break;
                case enum_config_path:
                    filePath = FileUtil.getRapidConfigDir() + name;
                    break;
                case enum_debug_path:
                    filePath = FileUtil.getRapidDebugDir() + name;
                    break;
                case enum_sandbox_path:
                    filePath = FileUtil.getRapidSandBoxDir() + name;
                    break;
                default:
                    filePath = FileUtil.getRapidDir() + name;
            }

            stream = new ByteArrayOutputStream();
            if( !FileUtil.readFile(filePath, stream) ){
                return null;
            }

            content = stream.toByteArray();
        }
        catch (Exception e){
            e.printStackTrace();
            content = null;
        }
        finally {
            if( stream != null ){
                try {
                    stream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content;
    }


}
