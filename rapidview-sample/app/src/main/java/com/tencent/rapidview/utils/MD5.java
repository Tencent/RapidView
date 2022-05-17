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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 重新优化后的md5工具类，统一两个关键点：
 * 1. 源串统一使用UTF-8编码格式
 * 2. 计算出的md5结果串统一为32位大写
 *
 * @author andygzyu
 */

public class MD5 {

    public static final String TAG = "andygzyu-MD5";

    public static char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * 计算源字符串的MD5散列值
     *
     * @param source 统一 UTF-8 编码
     * @return md5 hash value (128bit, 16byte) or null if failed
     */
    public static byte[] toMD5Byte(String source) {
        try {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            byte[] byteArray;
            try {
                byteArray = source.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
            return md5.digest(byteArray);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static byte[] toMD5Byte(byte[] bytes) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("toMD5Byte, MessageDigest.getInstance crash!");
            return null;
        }

        return md5.digest(bytes);
    }

    /**
     * 计算源字符串的MD5
     *
     * @param source 统一 UTF-8 编码
     * @return 32位大写MD5串 or "" if failed
     */
    public static String toMD5(String source) {
        byte[] data = toMD5Byte(source);
        return data == null ? "" : bytesToHexString(data);
    }

    /**
     * 计算输入流的MD5
     *
     * @param is 源输入流，调用方负责close
     * @return 32位大写MD5串 or "" if failed
     */
    public static String getInputStreamMd5(InputStream is) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[4196];
            int len;
            int readSize = 0;
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                if (len > 0) {
                    md5.update(buffer, 0, len);
                    readSize += len;
                }
            }
            if (readSize == 0)
                return "";
            byte[] md5Bytes = md5.digest();
            return bytesToHexString(md5Bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 计算文件的MD5
     *
     * @param sourceFile 要计算的文件.
     * @return 32位大写MD5串 or "" if failed
     */
    public static String getFileMD5(File sourceFile) {
        String ret = "";
        if (sourceFile.exists() && sourceFile.length() > 0) {
            BufferedInputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(sourceFile));
                ret = getInputStreamMd5(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
			} catch (OutOfMemoryError e) {
			} finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ret;
    }

//    /**
//     * 计算文件的MD5
//     *
//     * @param sourceFile 要计算的文件. replace with {@link #getFileMD5(File)}
//     * @return 32位大写MD5串 or "" if failed
//     */
//    public static String getFileMD5_OLD(File sourceFile) {
//
//        FileInputStream in = null;
//        byte[] buffer = new byte[1024];
//        int len;
//        int readSize = 0;
//
//        String s = "";
//        try {
//            in = new FileInputStream(sourceFile);
//
//            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
//
//            while ((len = in.read(buffer, 0, 1024)) != -1) {
//                md.update(buffer, 0, len);
//                readSize += len;
//            }
//
//            if (readSize == 0) {
//                STLogV2.reportDevProcessErrorLog(
//                        DevProcessSTManager.ERROR_ID_GEN_MANIFEST_MD5_EXCEPTION,
//                        "pos 16: " + " file empty");
//                return s;
//            }
//
//            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
//            // 用字节表示就是 16 个字节
//
//            s = bytesToHexString(tmp);
//
//        } catch (FileNotFoundException e) {
//            STLogV2.reportDevProcessErrorLog(
//                    DevProcessSTManager.ERROR_ID_GEN_MANIFEST_MD5_EXCEPTION,
//                    "pos 10: " + e.getMessage());
//        } catch (SecurityException e) {
//            STLogV2.reportDevProcessErrorLog(
//                    DevProcessSTManager.ERROR_ID_GEN_MANIFEST_MD5_EXCEPTION,
//                    "pos 11: " + e.getMessage());
//        } catch (NoSuchAlgorithmException e) {
//            STLogV2.reportDevProcessErrorLog(
//                    DevProcessSTManager.ERROR_ID_GEN_MANIFEST_MD5_EXCEPTION,
//                    "pos 12: " + e.getMessage());
//        } catch (NullPointerException e) {
//            STLogV2.reportDevProcessErrorLog(
//                    DevProcessSTManager.ERROR_ID_GEN_MANIFEST_MD5_EXCEPTION,
//                    "pos 13: " + e.getMessage());
//        } catch (IndexOutOfBoundsException e) {
//            STLogV2.reportDevProcessErrorLog(
//                    DevProcessSTManager.ERROR_ID_GEN_MANIFEST_MD5_EXCEPTION,
//                    "pos 14: " + e.getMessage());
//        } catch (Exception e) {
//            STLogV2.reportDevProcessErrorLog(
//                    DevProcessSTManager.ERROR_ID_GEN_MANIFEST_MD5_EXCEPTION,
//                    "pos 15: " + e.getMessage());
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException e) {
//                if (Global.ASSISTANT_DEBUG)e.printStackTrace();
//            }
//        }
//        return s;
//    }

    /**
     * 将 md5 hash value 转换成 32位大写MD5串
     *
     * @param md5Bytes md5 hash value (128bit, 16byte)
     * @return 32位大写MD5串 or "" if md5Bytes is wrong
     */
    public static String bytesToHexString(byte md5Bytes[]) {
        if (md5Bytes != null && md5Bytes.length == 16) {
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节，转换成 16 进制字符的转换
                byte byte0 = md5Bytes[i]; // 取第 i个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换， >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            return new String(str); // 换后的结果转换为字符串
        } else {
            return "";
        }
    }
    
    /**
     * 获取文件尾部最后50K的MD5值
     * @param filePath
     * @return
     */
    public static String getEndMd5FromFileForPatch(String filePath)
    {
        return getEndMd5FromFile(filePath, 50*1024);
    }
    
    /**
     * 获取文件尾部数据的MD5值
     * @param filePath
     * @param footerLenth 尾部数据长度
     * @return
     */
    public static String getEndMd5FromFile(String filePath, int footerLenth) {
        File file = new File(filePath);
        long filelen = file.length();
        RandomAccessFile ra = null;
        String ret = null;
        InputStream is = null;
        try {
            ra = new RandomAccessFile(file, "r");
            byte[] buf = null;
            if (filelen <= footerLenth) {
                // 当文件小于50K
                buf = new byte[(int) filelen];
                int len = 0;
                while(len<filelen)
                {
                     int readCount = ra.read(buf, len, (int) (filelen-len));
                     len += readCount;
                }
                
            } else {
                long position = filelen - footerLenth;
                ra.seek(position);
                buf = new byte[(int) footerLenth];
                int len = 0;
                while(len<footerLenth)
                {
                     int readCount = ra.read(buf, len, (int) (footerLenth-len));
                     len += readCount;
                }
            }
            is = new ByteArrayInputStream(buf);
            ret = getInputStreamMd5(is);
            buf = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (ra != null) {
                    ra.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
