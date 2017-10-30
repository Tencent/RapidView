package com.tencent.rapidview.utils;

public final class RapidSkinFile
{
    public String fileName = "";

    public String fileVer = "";

    public String fileUrl = "";

    public String fileMd5 = "";

    public int fileType = 0;

    public RapidSkinFile()
    {
    }

    public RapidSkinFile(String fileName, String fileVer, String fileUrl, String fileMd5, int fileType)
    {
        this.fileName = fileName;
        this.fileVer = fileVer;
        this.fileUrl = fileUrl;
        this.fileMd5 = fileMd5;
        this.fileType = fileType;
    }
}

