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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Created by yanhuizhang on 2015/10/22.
 */
public class ZipFileUtils {
    public final static String TAG = "CkZipUtils";
    
    /**
     * 解压zip包到指定目录
     * */
    public static boolean upzip2Dir(String srcZip, String destDir) {
        File destDirFile = new File(destDir);
        if (!destDirFile.exists() && !destDirFile.mkdirs()) {
            return false;
        }
        boolean result = true;
        File zipFile = new File(srcZip);
        ZipFile zf = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            zf = new ZipFile(zipFile);
            for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String szName = entry.getName();
                // 
                /*if (Global.ASSISTANT_DEBUG) {
                    XLog.d(TAG, "replace before:" + szName);
                }*/
                if (szName != null) {
                    szName = szName.replace("\\", File.separator);
                }
                /*if (Global.ASSISTANT_DEBUG) {
                    XLog.d(TAG, "replace end:" + szName);
                }*/
                if (entry.isDirectory()) {
                    File entryFile = new File(destDir + File.separator + szName);
                    if (!entryFile.exists()) {
                        entryFile.mkdirs();
                    }

                } else {
                    is = zf.getInputStream(entry);
                    CheckedInputStream csumi = new CheckedInputStream(is, new CRC32());
                    String str = destDir + File.separator + szName;
                    File destFile = new File(new String(str.getBytes("8859_1"), "GB2312"));
                    if (!destFile.exists()) {
                        File fileParentDir = destFile.getParentFile();
                        if (!fileParentDir.exists()) {
                            fileParentDir.mkdirs();
                        }
                        destFile.createNewFile();
                    }
                    os = new FileOutputStream(destFile);
                    byte[] b = new byte[4096];//(int) entry.getSize()];
                    long size = entry.getSize();
                    int l;
                    while (size > 0) {
                        l = csumi.read(b, 0, 4096);
                        os.write(b, 0, l);
                        size -= l;
                    }
                    if (entry.getCrc() != csumi.getChecksum().getValue()) {
                        //isFail=true;
                        result = false;
                        Closer.close(os);
                        Closer.close(is);
                        break;
                    }
                    Closer.close(os);
                    Closer.close(is);
                }
            }
        } catch (Exception e) {
            result = false;
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            Closer.close(os);
            Closer.close(is);
        }
        return result;
    }
}
