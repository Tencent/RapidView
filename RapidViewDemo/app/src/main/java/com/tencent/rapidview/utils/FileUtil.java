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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.tencent.rapidview.framework.RapidEnv;
import com.tencent.rapidviewdemo.DemoApplication;


public class FileUtil
{
	public static final String APP_SDCARD_AMOUNT_ROOT_PATH = "/rapidview/rapid_demo";

	public static final String APP_SDCARD_UNAMOUNT_ROOT_PATH = "/rapid_demo";

	public static final String APP_SDCARD_AMOUNT_TMP_ROOT_PATH = "/tmp";

    public static final String RAPID_DIR_PATH = "/rapidview";

    public static final String RAPID_CONFIG_DIR_PATH ="/rapidcfg";

    public static final String RAPID_DEBUG_DIR_PATH ="/rapiddebug";

    public static final String RAPID_BENCH_MARK_DIR_PATH ="/rapidbenchmark";

	public static final String RAPID_SANDBOX_PATH = "/rapidsandbox";

	public static final String RAPID_UPDATE_TEMPORARY = "/rapidtemporary";


	public static boolean isSDCardExistAndCanWrite()
	{
	    boolean result = false;
	    try{
	    	String s1 = Environment.getExternalStorageState();
			Boolean b1 = Environment.getExternalStorageDirectory().canWrite();

	        result = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && Environment.getExternalStorageDirectory().canWrite();
	    }catch(Exception e){
	        // do nothing
	    }finally{
	        return result;
	    }
	}

	public static String getCommonRootDir()
	{
		
        String dirPath = null;

        // 判断SDCard是否存在并且是可用的
        if (isSDCardExistAndCanWrite())
        {
        	try {
        		dirPath = Environment.getExternalStorageDirectory().getPath() + APP_SDCARD_AMOUNT_ROOT_PATH;
    		} catch (Exception e) {
    			e.printStackTrace();
    			return "";
    		}
            
        }
        else
        {
			try {
				dirPath = RapidEnv.getApplication().getFilesDir().getAbsolutePath() + APP_SDCARD_UNAMOUNT_ROOT_PATH;
			} catch (Exception e) {
				return null;
			}
		}
		File file = new File(dirPath);
		if (!file.exists())
		{
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static String getCommonPath(String path)
	{
		final String rootDir = getCommonRootDir();
		String fullPath = null;
		if (!TextUtils.isEmpty(path))
		{
			fullPath = rootDir + path;
		}
		else
		{
			fullPath = rootDir;
		}
		try {
			return getPath(fullPath, false);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public static byte[] compressBitmap(Bitmap bitmap, Bitmap.CompressFormat format, int bitRate) {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(format, bitRate, localByteArrayOutputStream);
		byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();

		try {
			localByteArrayOutputStream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return arrayOfByte;
	}

	public static String getFilesPath(String path) {
		final String rootDir = getFilesDir();
		String fullPath = null;

		if (!TextUtils.isEmpty(path))
		{
			fullPath = rootDir + path;
		}
		else
		{
			fullPath = rootDir;
		}

		try {
			return getPath(fullPath, false);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "";
	}

	private static String getFilesDir() {
		String dirPath = DemoApplication.getInstance().getFilesDir().getAbsolutePath();
		return dirPath;
	}

	public static byte [] readFromFile(String filePath) {
		byte [] buffer = null;
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(filePath);
			int length = fin.available();
			buffer = new byte[length];
			fin.read(buffer);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buffer;
	}

	public static String getPath(String path, boolean nomedia)
	{
		File file = new File(path);
		if (!file.exists() || !file.isDirectory())
		{
			file.mkdirs();
			if (nomedia)
			{
				File nomediaFile = new File(path + File.separator + ".nomedia");
				try
				{
					nomediaFile.createNewFile();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return file.getAbsolutePath();
	}

    public static String getRapidDir()
    {
        return getFilesPath(RAPID_DIR_PATH) + "/";
    }

    public static String getRapidConfigDir()
    {
        return getFilesPath(RAPID_CONFIG_DIR_PATH) + "/";
    }

    public static String getRapidDebugDir()
    {
        return getCommonPath(RAPID_DEBUG_DIR_PATH) + "/";
    }

    public static String getRapidBenchMarkDir()
    {
        return getCommonPath(RAPID_BENCH_MARK_DIR_PATH) + "/";
    }

	public static String getRapidSandBoxDir(){
		return getFilesPath(RAPID_SANDBOX_PATH) + "/";
	}

	public static String getRapidTemporaryDir(){
		return getFilesPath(RAPID_UPDATE_TEMPORARY) + "/";
	}


	public static boolean write2File(byte[] data, String dest)
	{
		if (data == null)
		{
			return false;
		}
		File file = new File(dest);
		if (file.exists())
		{
			deleteFile(dest);
		}
		FileOutputStream fos = null;
		try
		{
			file.createNewFile();
			fos = new FileOutputStream(file);
			fos.write(data);
			return true;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			try{
			file.delete();
			} catch(Exception e1) {
				e1.printStackTrace();
			}

			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (fos != null)
				{
					fos.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return false;
	}

	public static boolean deleteFile(String path)
	{
		File dir = new File(path);
		if (dir.exists() && dir.isFile())
		{
			return dir.delete();
		}
		return false;
	}

    public static void deleteFileOrDir(String path) {
        File file = new File(path);
        
        if (!file.exists() || !file.canWrite()) {
            XLog.d("FileUtil", "<deleteFileOrDir> file " + path + " not exist or can't writable");
            return;
        }
        
        if (!file.isDirectory()) {
            file.delete();
            return;
        }
        
        List<File> dirs = new LinkedList<File>();
        dirs.add(file);
        while (!dirs.isEmpty()) {
            File dir = dirs.remove(0);
            
            if (!dir.exists()) {
                continue;
            }
            
            File[] childFiles = dir.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                dir.delete();
                continue;
            }
            
            for (File childFile : childFiles) {
                if (childFile.isDirectory()) {
                    dirs.add(childFile);
                } else {
                    childFile.delete();
                }
            }
        }
    }

	public static boolean readFile(String dest, ByteArrayOutputStream baos)
    {
        return readFile(dest,baos,null);
    }

	public static boolean readFile(String dest, ByteArrayOutputStream baos, ByteArrayUtils byteArrayPool)
	{
		File file = new File(dest);
		if (null == baos || file.length() == 0)
		{

			return false;
		}
		FileInputStream fis = null;
        byte[] buf = null;
		try
		{
			fis = new FileInputStream(file);
            if(byteArrayPool!=null){
                buf = byteArrayPool.getBuf(1024*8);
            }
            else {
                buf = new byte[1024*8];
            }
			while (true)
			{
				int numread = fis.read(buf);
				if (-1 == numread)
				{
					break;
				}
				baos.write(buf, 0, numread);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fis != null)
			{
				try
				{
					fis.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
            if(buf!=null && byteArrayPool!=null){
               byteArrayPool.returnBuf(buf);
            }
		}
		if (baos.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	public static boolean isFileExists(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}

		File file = new File(filePath);
		return file.exists();
	}
}
