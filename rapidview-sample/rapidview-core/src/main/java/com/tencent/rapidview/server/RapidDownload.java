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
