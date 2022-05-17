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
package com.tencent.rapidview.runtime;

import com.tencent.rapidview.utils.FileUtil;
import com.tencent.rapidview.utils.MD5;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.RapidThreadPool;
import com.tencent.rapidview.utils.ZipFileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @Class RapidSandboxWrapper
 * @Desc 对实时沙箱做校验，每个文件夹每次启动进行一次zip解压
 *
 * @author arlozhang
 * @date 2017.02.13
 */
public class RapidSandboxWrapper {

    private static RapidSandboxWrapper msInstance = null;

    public interface IMD5Listener{

        void onFinish(String md5);
    }

    public interface IExtraListener{

        void onFinish(boolean succeed);
    }

    private RapidSandboxWrapper(){}

    public static RapidSandboxWrapper getInstance(){
        if( msInstance == null ){
            msInstance = new RapidSandboxWrapper();
        }

        return msInstance;
    }

    public void getPackageMD5(final String rapidID, final IMD5Listener listener){

        if( listener == null ){
            return;
        }

        if( RapidStringUtils.isEmpty(rapidID) ){
            listener.onFinish("");
            return;
        }

        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {
                String md5;
                String filePath = FileUtil.getRapidSandBoxDir() + rapidID + "/" + rapidID + ".zip";

                if( !FileUtil.isFileExists(filePath) ){
                    listener.onFinish("");
                    return;
                }

                md5 = MD5.getFileMD5(new File(filePath));
                listener.onFinish(md5);
            }
        });
    }

    public void extraPackage(final String rapidID, final IExtraListener listener){


        if( listener == null ){
            return;
        }

        if( RapidStringUtils.isEmpty(rapidID) ){
            listener.onFinish(false);
            return;
        }

        RapidThreadPool.get().execute(new Runnable() {
            @Override
            public void run() {
                boolean bRet = false;
                String filePath  = FileUtil.getRapidSandBoxDir() + rapidID + "/" + rapidID + ".zip";
                String extraPath = FileUtil.getRapidSandBoxDir() + rapidID + "/";

                bRet = ZipFileUtils.upzip2Dir(filePath, extraPath);

                listener.onFinish(bRet);
            }
        });

    }

    public void initSandbox(){
        String noPicFilePath = FileUtil.getRapidSandBoxDir() + ".nomedia";
        File file = null;

        if( FileUtil.isFileExists(noPicFilePath) ){
            return;
        }

        file = new File(noPicFilePath);

        try {
            file.createNewFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
