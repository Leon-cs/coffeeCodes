package com.coffeeCodes.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Date: 14-4-29 下午3:12<br/>
 */
@Slf4j
public class NetUtils {
    private static Logger logger = LoggerFactory.getLogger(NetUtils.class);
    public static final String ENC_UTF8 = "UTF-8";
    private static final String CONTENT_JSON = "application/json;charset=";
    private static final int DEFAULT_CONNECT_TIMEOUT = 30;  // 默认超时时间

    /**
     * 从流中获取数据
     *
     * @param inputStream 流
     * @return 字符串
     * @throws IOException 异常
     */
    public static String getStringFromInput(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        byte[] bytes = outSteam.toByteArray();
        String result = new String(bytes);
        outSteam.close();
        return result;
    }

    /**
     * 以POST发送json请求
     *
     * @param url    url
     * @param data   json数据
     * @param encode 编码
     * @return 返回响应数据
     */
    public static String sendJsonDataByPost(String url, String data, String encode) throws IOException {
        String content = CONTENT_JSON + (!StringUtils.isNotEmpty(encode) ? ENC_UTF8 : encode);
        return sendDataByPost(url, data, encode, content, DEFAULT_CONNECT_TIMEOUT);
    }

    /**
     * 以POST发送json请求
     *
     * @param url            url
     * @param data           json数据
     * @param encode         编码
     * @param connectTimeOut 超时时间
     * @return 返回响应数据
     */
    public static String sendJsonDataByPost(String url, String data, String encode, int connectTimeOut) throws IOException {
        String content = CONTENT_JSON + (!StringUtils.isNotEmpty(encode) ? ENC_UTF8 : encode);
        return sendDataByPost(url, data, encode, content, connectTimeOut);
    }

    /**
     * 以POST发送请求
     *
     * @param url         url
     * @param data        数据
     * @param encode      编码
     * @param contentType content类型
     * @return 返回响应数据
     * @throws IOException
     */
    private static String sendDataByPost(String url, String data, String encode, String contentType, int connectTimeOut) throws IOException {
        String content = null;
        HttpPost post = new HttpPost(url);
        try {
            StringEntity entity = new StringEntity(data, encode);
            entity.setContentType(contentType);
            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, contentType));
            post.setEntity(entity);
            RequestConfig config = getRequestConfig(connectTimeOut);
            post.setConfig(config);
            HttpClient httpclient = HttpClients.createDefault();
            HttpResponse response = httpclient.execute(post);
            Integer statusCode = response.getStatusLine().getStatusCode();
            logger.info("响应状态码 = " + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                content = getResponseString(response, encode);
            }
        } finally {
            post.releaseConnection();
        }
        return content;
    }

    /**
     * get请求
     *
     * @param url
     * @param params
     * @param connectTimeOut
     * @param header
     * @return
     */
    public static String sendDataByGet(String url, Map<String, String> params, int connectTimeOut, Map<String, String> header) {
        log.info("请求开始");
        String body = null;
        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(getRequestConfig(connectTimeOut));
        setHttpGetHeader(header, httpget);
        try {
            // Get请求
            httpget.setURI(new URI(httpget.getURI().toString() + "?" + getParams(params)));
            // 发送请求
            HttpResponse response = HttpClients.createDefault().execute(httpget);
            // 获取返回数据
            body = dealWithResponse(response);
        } catch (Exception e) {
            log.error("httpclient调用异常，信息：{}", e);
        } finally {
            httpget.releaseConnection();
        }
        return body;
    }
//
//    /**
//     * get请求，返回HttpResponse对象，调用方判断statusCode
//     *
//     * @param url
//     * @param params
//     * @param connectTimeOut
//     * @param header
//     * @return
//     */
//    public static String sendDataByGetRes(String url, Map<String, String> params, int connectTimeOut, Map<String, String> header) {
//        String httpStr = null;
//        HttpGet httpget = new HttpGet(url);
//        httpget.setConfig(getRequestConfig(connectTimeOut));
//        if (header != null && !header.isEmpty()) {
//            for (String key : header.keySet()) {
//                httpget.setHeader(key, header.get(key));
//            }
//        }
//        HttpResponse response = null;
//        try {
//            // Get请求
//            httpget.setURI(new URI(httpget.getURI().toString() + "?" + getParams(params)));
//            log.info("请求参数：{}", httpget.getURI());
//            // 发送请求
//            response = HttpClients.createDefault().execute(httpget);
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != HttpStatus.SC_OK) {
//                return null;
//            }
//            HttpEntity entity = response.getEntity();
//            if (entity == null) {
//                return null;
//            }
//            httpStr = EntityUtils.toString(entity);
//        } catch (Exception e) {
//            log.error("httpclient调用异常，信息：{}", e);
//        } finally {
//            if (response != null) {
//                try {
//                    EntityUtils.consume(response.getEntity());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return httpStr;
//    }

    /**
     * 返回参数格式
     *
     * @param params
     * @return
     */
    public static String getParams(Map<String, String> params) {
        String result = "";
        if (params != null) {
            StringBuffer strb = new StringBuffer();
            for (String key : params.keySet()) {
                strb.append(key).append("=").append(params.get(key)).append("&");
            }
            result = strb.substring(0, strb.length() - 1);
        }
        return result;
    }

    /**
     * 获取RequestConfig的配置
     *
     * @return RequestConfig
     */
    private static RequestConfig getRequestConfig(int connectTimeOut) {
        return RequestConfig.custom()
                .setSocketTimeout(connectTimeOut * 1000)
                .setConnectTimeout(connectTimeOut * 1000)
                .build();
    }

    /**
     * 获取响应信息
     */
    public static String getResponseString(HttpResponse response, String encoding) throws IOException {
        String content = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), encoding));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();
        content = sb.toString();
        return content;
    }

    public static String getHostAddress() {
        String localIp = null;
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return null;
        }
        return localIp;
    }

    /**
     * 以https请求数据
     *
     * @param url     url
     * @param params  参数
     * @return 响应数据
     */
    public static String sendGetHttps(String url, Map<String, String> params, int connectTimeOut, Map<String, String> header) {
        String httpStr = null;
        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(getRequestConfig(connectTimeOut));
        setHttpGetHeader(header, httpget);
        HttpResponse response = null;
        try {
            // Get请求
            httpget.setURI(new URI(httpget.getURI().toString() + "?" + getParams(params)));
            // 发送请求
            response = createHttpsClient().execute(httpget);
            httpStr = dealWithResponse(response);
        } catch (Exception e) {
            log.error("httpclient调用异常，信息：{}", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    public static HttpResponse sendGetHttpsRes(String url, Map<String, String> params, int connectTimeOut, Map<String, String> header) {
        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(getRequestConfig(connectTimeOut));
        setHttpGetHeader(header, httpget);
        HttpResponse response = null;
        try {
            // Get请求
            httpget.setURI(new URI(httpget.getURI().toString() + "?" + getParams(params)));
            // 发送请求
            response = createHttpsClient().execute(httpget);
        } catch (Exception e) {
            log.error("httpclient调用异常，信息：{}", e);
        }
        return response;
    }

    private static String dealWithResponse(HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            log.info("请求响应状态码:{}", statusCode);
            return null;
        }
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }
        return EntityUtils.toString(entity);
    }

    private static void setHttpGetHeader(Map<String, String> header, HttpGet httpget) {
        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                httpget.setHeader(key, header.get(key));
            }
        }
    }

    public static CloseableHttpClient createHttpsClient() throws Exception {
        X509TrustManager x509mgr = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] xcs, String string) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] xcs, String string) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{x509mgr}, null);
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }
}
