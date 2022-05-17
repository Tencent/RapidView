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

import android.content.Context;

import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.framework.RapidPool;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @Class RapidXmlLoader
 * @Desc RapidView界面数据加载,用于做外部或服务器数据替换
 *
 * @author arlozhang
 * @date 2015.09.22
 */
public class RapidXmlLoader {

    private Map<String, Document> mDocumentCacheMap = new ConcurrentHashMap<String, Document>();

    private static RapidXmlLoader mSelf;

    private RapidXmlLoader(){}

    public static RapidXmlLoader self() {

        if (mSelf == null) {
            mSelf = new RapidXmlLoader();
        }

        return mSelf;
    }

    private synchronized Document bytesToDocument(byte[] bytesXml) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new ByteArrayInputStream(bytesXml));
        } catch (Exception e) {
            e.printStackTrace();
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "解析XML异常，XML：" + new String(bytesXml));
        }

        return document;
    }

    public Document getDocument(Context context, String name, String photonID, boolean limitLevel) {
        Document doc = null;
        byte[] bytesXml = null;

        if( RapidConfig.DEBUG_MODE &&
                FileUtil.isFileExists(FileUtil.getRapidDebugDir() + name) ){
            bytesXml = RapidFileLoader.getInstance().getBytes(name, RapidFileLoader.PATH.enum_debug_path);

            try {
                doc = bytesToDocument(bytesXml);
            }
            catch (Exception e) {
                e.printStackTrace();
                doc = null;
            }

            return doc;
        }

        if( !RapidStringUtils.isEmpty(photonID) ){

            bytesXml = RapidFileLoader.getInstance().getBytes(photonID + "/" + name, RapidFileLoader.PATH.enum_sandbox_path);
            if( bytesXml == null && limitLevel ) {
                return doc;
            }

            try{
                doc = bytesToDocument(bytesXml);
            }
            catch (Exception e){
                e.printStackTrace();
                doc = null;
            }

            if( doc != null ){
                return doc;
            }
        }

        if( limitLevel ){
            return doc;
        }

        doc = mDocumentCacheMap.get(name);
        if( doc != null ) {
            return doc;
        }

        bytesXml = RapidPool.getInstance().getFile(name, true);

        if( bytesXml == null ) {
            bytesXml = RapidAssetsLoader.getInstance().get(context, name);
        }

        if( bytesXml == null ){
            return doc;
        }

        try {
            doc = bytesToDocument(bytesXml);
        } catch (Exception e) {
            e.printStackTrace();
            doc = null;
        }

        if( doc != null ){
            mDocumentCacheMap.put(name, doc);
        }

        return doc;
    }
    public void deleteDocument(String name){
        mDocumentCacheMap.remove(name);
    }
}