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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Class RapidThreadPool
 * @Desc RapidView快速线程池，加载界面需要特别及时响应，公用线程一旦被占满就有阻塞界面的风险，此处开辟特别绿色通道。
 *       该处线程池不应对外使用。
 *
 * @author arlozhang
 * @date 2016.06.28
 */
public class RapidThreadPool {

    public static RapidThreadPool msInstance = null;

    public ExecutorService mExecutor;

    public RapidThreadPool(){

        try{
            mExecutor = Executors.newCachedThreadPool(new CommonThreadFactory("rapidview_thread_pool"));
        }
        catch (Throwable t){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "线程池创建失败，尝试重新创建");
            t.printStackTrace();
            mExecutor = Executors.newCachedThreadPool(new CommonThreadFactory("rapidview_thread_pool_exp"));

        }
    }

    public synchronized static RapidThreadPool get(){
        if( msInstance == null ){
            msInstance = new RapidThreadPool();
        }

        return msInstance;
    }

    public void execute(Runnable runnable){
        try{
            mExecutor.submit(runnable);
        }
        catch (Throwable t){
            XLog.d("RAPID_ENGINE", "RapidView线程抛出异常，详细请查看异常信息");
            t.printStackTrace();
        }
    }

    public void execute(final Runnable runnable, final long delayMillis){
        execute(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(delayMillis);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                execute(runnable);
            }
        });
    }
}
