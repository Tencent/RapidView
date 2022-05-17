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
package com.tencent.rapidview.lua.interfaceimpl;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;

import com.android.internal.http.multipart.ByteArrayPartSource;
import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.MultipartEntity;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.tencent.rapidview.deobfuscated.IBytes;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidEnv;
import com.tencent.rapidview.lua.RapidLuaCaller;
import com.tencent.rapidview.parser.RapidParserObject;
import com.tencent.rapidview.utils.NetworkUtil;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.RapidThreadPool;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Class LuaJavaRequest
 * @Desc 提供给lua的https请求
 *
 * @author arlozhang
 * @date 2017.02.22
 */
public class LuaJavaRequest extends RapidLuaJavaObject {

    private String mUrl = null;

    private String mStringData = null;

    private byte[] mBytesData = null;

    private Part[] mEntityDataPart = null;

    private String mMethod = null;

    private List<String> mHeaderKey = new ArrayList<String>();

    private List<String> mHeaderValue = new ArrayList<String>();

    private LuaFunction mSucceedListener = null;

    private LuaFunction mFailedListener = null;

    // 失败最多重试次数
    public static int MAX_HTTP_RETRY = 3;

    // 最多连接个数
    static final int TOTAL_CONNECTIONS = 100;

    // 单个路由最多连接数
    static final int MAX_CONNECTIONS_PER_ROUTE = 50;

    // 从连接池中取连接的最大超时时间
    static final int MAX_TIMEOUT = 1000;

    // 分片大小
    static final int BUFFER_SIZE_DEFAULT = 8 * 1024;

    // 2g环境下的网络超时时间
    static final int CONNECTION_TIMEOUT_2G = 15;

    // 3g环境下的网络超时时间
    static final int CONNECTION_TIMEOUT_3G = 10;

    // wifi环境下的网络超时时间
    static final int CONNECTION_TIMEOUT_WIFI = 5;

    // 2g环境下的两个分片之间的超时时间
    static final int SO_TIMEOUT_2G = 45;

    // 3g环境下的两个分片之间的超时时间
    static final int SO_TIMEOUT_3G = 40;

    // wifi环境下的两个分片之间的超时时间
    static final int SO_TIMEOUT_WIFI = 30;

    public LuaJavaRequest(String rapidID, IRapidView rapidView, String url, byte[] data, LuaTable header, String method, LuaFunction succeedListener, LuaFunction failedListener){
        super(rapidID, rapidView);
        mUrl = url;
        mBytesData = data;
        setHeader(header);
        setMethod(method);
        setSucceedListener(succeedListener);
        setFailedListener(failedListener);
    }

    public LuaJavaRequest(String rapidID, IRapidView rapidView, String url, String data, LuaTable header, String method, LuaFunction succeedListener, LuaFunction failedListener){
        super(rapidID, rapidView);
        mUrl = url;
        mStringData = data;
        setHeader(header);
        setMethod(method);
        setSucceedListener(succeedListener);
        setFailedListener(failedListener);
    }

    public LuaJavaRequest(String rapidID, IRapidView rapidView, String url, LuaTable data, LuaTable header, String method, LuaFunction succeedListener, LuaFunction failedListener){
        super(rapidID, rapidView);
        mUrl = url;
        setHeader(header);
        setMultiPartData(data);
        setMethod(method);
        setSucceedListener(succeedListener);
        setFailedListener(failedListener);
    }

    private void setHeader(LuaTable header){
        LuaValue key = LuaValue.NIL;
        LuaValue value = LuaValue.NIL;

        if( header != null && header.istable() ){
            while(true){
                Varargs argsItem = header.next(key);
                key = argsItem.arg1();

                if( key.isnil() ){
                    break;
                }

                value = argsItem.arg(2);

                if( key.isstring() && value.isstring() ){
                    addHeader(key.toString(), value.toString());
                }
            }
        }
    }

    private boolean addHeader(String key, String value){

        if( key == null || value == null ){
            return false;
        }

        mHeaderKey.add(key);
        mHeaderValue.add(value);

        return true;
    }

    private void setMultiPartData(LuaTable params){
        LuaValue key = LuaValue.NIL;
        LuaValue value = LuaValue.NIL;
        List<String> entityKey = new ArrayList<String>();
        List<Object> entityValue = new ArrayList<Object>();

        if( params != null && params.istable() ){
            while(true){
                Varargs argsItem = params.next(key);
                key = argsItem.arg1();
                Object objValue = null;

                if( key.isnil() ){
                    break;
                }

                value = argsItem.arg(2);
                objValue = CoerceLuaToJava.coerce(value, Object.class);

                if( objValue instanceof IBytes ){
                    objValue = ((IBytes) objValue).getArrayByte();
                }

                if( key.isstring()  ){
                    entityKey.add(key.toString());
                    entityValue.add(objValue);
                }
            }
        }

        mEntityDataPart = new Part[entityKey.size()];
        for( int i = 0; i < entityKey.size(); i++ ){
            Object objValue = entityValue.get(i);

            if( objValue == null ){
                continue;
            }

            try{
                if( objValue instanceof byte[] ){
                    mEntityDataPart[i] = new FilePart(entityKey.get(i), new ByteArrayPartSource(entityKey.get(i), (byte[]) objValue));
                }
                else{
                    mEntityDataPart[i] = new StringPart(entityKey.get(i), objValue.toString());
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void setMethod(String method){
        if( method == null ){
            return;
        }

        mMethod = method;
    }

    private void setSucceedListener(LuaFunction succeedListener){
        mSucceedListener = succeedListener;
    }

    private void setFailedListener(LuaFunction failedListener){
        mFailedListener = failedListener;
    }

    public boolean request(){

        if( RapidStringUtils.isEmpty(mUrl) ){
            return false;
        }

        analyzeMethod();

        if( mHeaderKey.size() == 0 || mHeaderKey.size() != mHeaderValue.size() ){
            mHeaderKey.clear();
            mHeaderValue.clear();
        }

        RapidThreadPool.get().execute(

            new Runnable() {
                @Override
                public void run() {
                    RapidParserObject parser = getParser();
                    DefaultHttpClient  httpClient = null;

                    if( parser == null ){
                        return;
                    }

                    try{
                        byte   temp[]   = new byte[1024];
                        int    count    = 0;
                        byte[] response = null;
                        HttpUriRequest req      = null;
                        HttpResponse   resp     = null;
                        HttpParams     params   = new BasicHttpParams();
                        InputStream inputStream = null;
                        ByteArrayOutputStream   outputStream      = new ByteArrayOutputStream();
                        SchemeRegistry          supportedSchemes  = new SchemeRegistry();
                        ClientConnectionManager connectionManager = null;

                        ConnManagerParams.setMaxTotalConnections(params, TOTAL_CONNECTIONS);
                        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(MAX_CONNECTIONS_PER_ROUTE));
                        ConnManagerParams.setTimeout(params, MAX_TIMEOUT);
                        HttpProtocolParams.setUseExpectContinue(params, false);

                        HttpConnectionParams.setStaleCheckingEnabled(params, false);
                        HttpConnectionParams.setSocketBufferSize(params, BUFFER_SIZE_DEFAULT);

                        supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                        supportedSchemes.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 80));

                        connectionManager = new ThreadSafeClientConnManager(params, supportedSchemes);
                        httpClient = new DefaultHttpClient(connectionManager, params);

                        proxyHttpClient(httpClient);
                        timeoutHttpClient(httpClient);
                        retryHttpClient(httpClient);

                        try {
                            keepAliveHttpClient(httpClient);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }

                        if( mMethod.compareTo("GET") == 0 ){
                            req = new HttpGet(mUrl);
                        }
                        else if( mMethod.compareTo("OPTIONS") == 0 ){
                            req = new HttpOptions(mUrl);
                        }
                        else if( mMethod.compareTo("HEAD") == 0 ){
                            req = new HttpHead(mUrl);
                        }
                        else if( mMethod.compareTo("POST") == 0 ){
                            req = new HttpPost(mUrl);
                        }
                        else if( mMethod.compareTo("PUT") == 0 ){
                            req = new HttpPut(mUrl);
                        }
                        else if( mMethod.compareTo("DELETE") == 0 ){
                            req = new HttpDelete(mUrl);
                        }
                        else if( mMethod.compareTo("TRACE") == 0 ){
                            req = new HttpTrace(mUrl);
                        }

                        if( req instanceof HttpEntityEnclosingRequestBase ){
                            AbstractHttpEntity entity = null;
                            if( mBytesData != null ){
                                entity = new ByteArrayEntity(mBytesData);
                            }
                            else if( mEntityDataPart != null && mEntityDataPart.length != 0 ){
                                entity = new MultipartEntity(mEntityDataPart);
                            }
                            else{
                                entity = new StringEntity(mStringData == null ? "" : mStringData);
                            }

                            ((HttpEntityEnclosingRequestBase)req).setEntity(entity);
                        }

                        for( int i = 0; i < mHeaderKey.size(); i++ ){

                            if( mHeaderValue.size() <= i ){
                                break;
                            }

                            String key = mHeaderKey.get(i);
                            String value = mHeaderValue.get(i);

                            req.setHeader(key, value);
                        }

                        resp = httpClient.execute(req);
                        HttpEntity entity = resp.getEntity();

                        if( resp == null || resp.getStatusLine().getStatusCode() != 200 ){

                            if( mFailedListener != null ){
                                final int respCode = resp == null ? -1 : resp.getStatusLine().getStatusCode();

                                parser.getUIHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        RapidLuaCaller.getInstance().call(mFailedListener, respCode);
                                    }
                                });
                            }

                            return;
                        }

                        inputStream = entity.getContent();

                        while ((count = inputStream.read(temp)) != -1)
                        {
                            outputStream.write(temp, 0, count);
                        }

                        outputStream.flush();
                        outputStream.close();
                        inputStream.close();

                        response = outputStream.toByteArray();

                        if( mSucceedListener != null ){
                            final Bytes finalResp = new Bytes(response);
                            parser.getUIHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    com.tencent.rapidview.lua.RapidLuaCaller.getInstance().call(mSucceedListener, CoerceJavaToLua.coerce(finalResp));
                                }
                            });

                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();

                        if( mFailedListener != null ){
                            parser.getUIHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    com.tencent.rapidview.lua.RapidLuaCaller.getInstance().call(mFailedListener, -1);
                                }
                            });
                        }
                    }
                    finally {
                        if(httpClient != null) {
                            try {
                                httpClient.getConnectionManager().shutdown();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        );

        return true;
    }

    private void proxyHttpClient(HttpClient httpClient) {
        Context context = RapidEnv.getApplication();
        if(context == null) {
            return;
        }

        // fix异常上报的权限问题，治标不治本
        int result = PackageManager.PERMISSION_DENIED;
        try {//这里有可能产生异常，catch一下
            result = context.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if (result != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        int type = 0;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            type = activeNetworkInfo == null ? 0 : activeNetworkInfo.getType();
        }
        catch (Throwable e) {  // fix rdm:62635174，特殊机型可能crash
            e.printStackTrace();
        }
        if (type == ConnectivityManager.TYPE_MOBILE) {
            String defaultHost = Proxy.getDefaultHost();
            int defaultPort = Proxy.getDefaultPort();
            if (defaultHost != null && defaultPort != -1) {
                HttpHost proxy = new HttpHost(defaultHost, defaultPort);
                httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                return;
            }
        }
        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
    }

    /**
     * 重新设置超时时间
     *
     * @param httpClient
     */
    private void timeoutHttpClient(HttpClient httpClient) {
        int connectionTimeout = 10;
        int soTimeout = 30;

        if (NetworkUtil.isWifi()) {
            connectionTimeout = CONNECTION_TIMEOUT_WIFI;
            soTimeout = SO_TIMEOUT_WIFI;
        } else if (NetworkUtil.is3G()) {
            connectionTimeout = CONNECTION_TIMEOUT_3G;
            soTimeout = SO_TIMEOUT_3G;
        } else if (NetworkUtil.is2G()) {
            connectionTimeout = CONNECTION_TIMEOUT_2G;
            soTimeout = SO_TIMEOUT_2G;
        } else {
            // 未知的网络类型，使用默认的超时设置
        }

        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout * 1000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout * 1000);
    }

    /**
     * 重新失败重试
     *
     * @param httpClient
     */
    private void retryHttpClient(DefaultHttpClient httpClient) {
        httpClient.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {

            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= MAX_HTTP_RETRY) {
                    return false;
                }

                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                else if (exception instanceof ClientProtocolException) {
                    return true;
                }

                HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
                boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
                if (!idempotent) {
                    return true;
                }
                return false;
            }
        });
    }

    private void keepAliveHttpClient(DefaultHttpClient httpClient) {
        httpClient.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {

            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                long keepAlive = super.getKeepAliveDuration(response, context);
                if (keepAlive == -1) {
                    keepAlive = 30 * 1000;
                }
                return keepAlive;
            }
        });
    }


    private void analyzeMethod(){
        if( RapidStringUtils.isEmpty(mMethod) ){
            mMethod = "GET";
        }

        if( mMethod.compareToIgnoreCase("OPTIONS") == 0 ){
            mMethod = "OPTIONS";
            return;
        }

        if( mMethod.compareToIgnoreCase("GET") == 0 ){
            mMethod = "GET";
            return;
        }

        if( mMethod.compareToIgnoreCase("HEAD") == 0 ){
            mMethod = "HEAD";
            return;
        }

        if( mMethod.compareToIgnoreCase("POST") == 0 ){
            mMethod = "POST";
            return;
        }

        if( mMethod.compareToIgnoreCase("PUT") == 0 ){
            mMethod = "PUT";
            return;
        }

        if( mMethod.compareToIgnoreCase("DELETE") == 0 ){
            mMethod = "DELETE";
            return;
        }

        if( mMethod.compareToIgnoreCase("TRACE") == 0 ){
            mMethod = "TRACE";
            return;
        }

        if( mMethod.compareToIgnoreCase("CONNECT") == 0 ){
            mMethod = "CONNECT";
            return;
        }

        mMethod = "GET";
    }

//    private SSLSocketFactory getCertificates() {
//        try {
//            File        crtFile      = null;
//            InputStream inStream     = null;
//            TrustManagerFactory trustManagerFactory = null;
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//
//            keyStore.load(null);
//
//            if( FileUtil.isFileExists(FileUtil.getRapidSandBoxDir() + mRapidID + "/" + "certificate.cer") ){
//
//                crtFile = new File(FileUtil.getRapidSandBoxDir() + mRapidID + "/" + "certificate.cer");
//            }
//            else if( RapidConfig.DEBUG_MODE && FileUtil.isFileExists(FileUtil.getRapidDebugDir() + "certificate.cer") ){
//
//                crtFile = new File(FileUtil.getRapidDebugDir() + "certificate.cer");
//            }
//            else {
//                return null;
//            }
//
//            inStream = new FileInputStream(crtFile);
//
//            keyStore.setCertificateEntry("certificate.cer", certificateFactory.generateCertificate(inStream));
//
//            try {
//                if (inStream != null) {
//                    inStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//
//            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//
//            return sslContext.getSocketFactory();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

}
