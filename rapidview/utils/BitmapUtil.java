package com.tencent.rapidview.utils;


import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;


public class BitmapUtil {

    public static byte[] compressImageAndGetBytes(Bitmap image,int maxKB) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > maxKB) {  //循环判断如果压缩后图片是否大于400kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }

        return baos.toByteArray();
    }
}
