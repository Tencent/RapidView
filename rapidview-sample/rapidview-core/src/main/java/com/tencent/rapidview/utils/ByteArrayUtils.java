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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ByteArrayUtils {

    public List<byte[]> mBuffersByLastUse = new LinkedList<byte[]>();
    public List<byte[]> mBuffersBySize = new ArrayList<byte[]>(64);

    public int mCurrentSize = 0;
    public final int mSizeLimit;


    protected static final Comparator<byte[]> BUF_COMPARATOR = new Comparator<byte[]>() {
        @Override
        public int compare(byte[] lhs, byte[] rhs) {
            return lhs.length - rhs.length;
        }
    };

    public static final boolean usePool=true;//用于调试，是否开启池

    public ByteArrayUtils(int sizeLimit) {
        mSizeLimit = sizeLimit;
    }


    public static final int MAX_BYTE_ARRAY_POOL_SIZE=1*1024*1024;//最大允许1M,不代表一定会到1M
    public static ByteArrayUtils byteArrayPool;
    public static final Object lock=new Object();

    /**
     * 获得一个全局使用对象
     * @return
     */
    public static ByteArrayUtils getInstance(){
        synchronized (lock){//coverity id=129627
            if(byteArrayPool==null){
                byteArrayPool=new ByteArrayUtils(MAX_BYTE_ARRAY_POOL_SIZE);
            }
        }

        return byteArrayPool;
    }

    public synchronized byte[] getBuf(int len) {
        if(!usePool){
            return new byte[len];
        }
        for (int i = 0; i < mBuffersBySize.size(); i++) {
            byte[] buf = mBuffersBySize.get(i);
            if (buf.length >= len) {
                mCurrentSize -= buf.length;
                mBuffersBySize.remove(i);
                mBuffersByLastUse.remove(buf);
                return buf;
            }
        }
        return new byte[len];
    }

    public synchronized void returnBuf(byte[] buf) {
        if(!usePool){
            return;
        }
        if (buf == null || buf.length > mSizeLimit) {
            return;
        }

        mBuffersByLastUse.add(buf);
        int pos = Collections.binarySearch(mBuffersBySize, buf, BUF_COMPARATOR);
        if (pos < 0) {
            pos = -pos - 1;
        }
        mBuffersBySize.add(pos, buf);
        mCurrentSize += buf.length;
        trim();
    }

    public synchronized void trim() {
        int deleteSize=0;
        while (mCurrentSize > mSizeLimit) {
            byte[] buf = mBuffersByLastUse.remove(0);
            mBuffersBySize.remove(buf);
            mCurrentSize -= buf.length;
            deleteSize+=buf.length;
        }
    }

}
