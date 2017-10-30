package com.tencent.rapidview.deobfuscated;

/**
 * @Class IBytes
 * @Desc 用于在lua中做字节流传输的对象接口
 *
 * @author arlozhang
 * @date 2017.03.01
 */
public interface IBytes {

    byte[] getArrayByte();

    boolean isNil();

    String getString();

    long getLength();
}
