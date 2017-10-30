package com.tencent.rapidview.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @Class RapidDownload
 * @Desc 实现RapidView下载接口
 *
 * @author arlozhang
 * @date 2017.02.20
 */
public class RapidDownload implements RapidDownloadWrapper.IDownload {

    private String DOWNLOAD_FROM = "rapidview";

    private RapidDownloadWrapper.IDownload.ICallback mCallback = null;

    private List<String> mTicketList = new ArrayList<String>();

    public RapidDownload(){
    }

    @Override
    public boolean download(String ticket, String url, ICallback callback) {
        return false;
    }
}
