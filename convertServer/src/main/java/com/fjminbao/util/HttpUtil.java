package com.fjminbao.util;



import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.*;
import java.net.ProtocolException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 用于发送HTTP请求，HTTP工具类
 * @Author: xianyuanLi
 * @Date: created in 11:57 2019/12/20
 * Descrpition:
 */
public class HttpUtil {
    private static HttpUtil httpUtil = null;

    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            httpUtil = new HttpUtil();
        }
        return httpUtil;
    }

    /**
     * POST发送信息
     *
     * @param url
     * @param fileKeyName
     * @param file
     * @param param
     * @return
     * @throws Exception
     */
    public static String post(String url, String fileKeyName, File file, Map<String, String> param) throws Exception {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
        meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        meb.setCharset(Charset.forName("utf-8"));
        ContentType contentType = ContentType.create("text/plain", Consts.UTF_8);
        if (param != null && param.size() > 0) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                meb.addPart(entry.getKey(), new StringBody(entry.getValue(), contentType));
            }
        }
        if (!StringUtils.isEmpty(fileKeyName) && file != null && file.length() > 0) {
            FileBody fileBody = new FileBody(file);
            meb.addPart(fileKeyName, fileBody);
        }
        httppost.setEntity(meb.build());

        CloseableHttpResponse response = httpClient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(entity, "UTF-8");
            response.close();
        } else {
            result = EntityUtils.toString(entity, "UTF-8");
            response.close();
            throw new IllegalArgumentException(result);
        }
        return result;
    }

    /**
     * POST发送信息
     *
     * @param url
     * @param paramsMap
     * @return
     * @throws Exception
     */
    public static String post(String url, Map<String, Object> paramsMap) throws Exception {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        //配置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .setRedirectsEnabled(true)
                .build();
        HttpPost httpPost = new HttpPost(url);
        //设置超时时间
        httpPost.setConfig(requestConfig);

        //装配post请求参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        if (paramsMap != null) {
            for (String key : paramsMap.keySet()) {
                list.add(new BasicNameValuePair(key, String.valueOf(paramsMap.get(key))));
            }
        }

        //将参数进行编码为合适的格式,如将键值对编码为param1=value1&param2=value2
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, "utf-8");
        httpPost.setEntity(urlEncodedFormEntity);

        //执行 post请求
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
        String strRequest = "";
        if (null != closeableHttpResponse && !"".equals(closeableHttpResponse)) {
            if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = closeableHttpResponse.getEntity();
                strRequest = EntityUtils.toString(httpEntity, "UTF-8");
                closeableHttpResponse.close();
            } else {
                strRequest = "Error Response" + closeableHttpResponse.getStatusLine().getStatusCode();
                throw new IllegalArgumentException(strRequest);
            }
        }
        return strRequest;
    }

    private URL parseUrl(String httpUrl) {
        URL url = null;

        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException var4) {
            var4.printStackTrace();
        }

        return url;
    }

    private HttpURLConnection openHttpUrlConnection(String httpUrl) {
        return this.openHttpUrlConnection(this.parseUrl(httpUrl));
    }

    private HttpURLConnection openHttpUrlConnection(URL url) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return connection;
    }

    public String sendHttp(String httpUrl, String method, String requestParams, String authorization) {
        HttpURLConnection connection = this.openHttpUrlConnection(httpUrl);

        try {
            connection.setRequestMethod(method);
        } catch (ProtocolException var16) {
            var16.printStackTrace();
        }

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        OutputStream outputStream = null;
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        String result = "";

        try {
            outputStream = connection.getOutputStream();
            outputStream.write(requestParams.getBytes());
            outputStream.flush();
            inputStream = connection.getInputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            boolean var11 = true;

            int i;
            while ((i = inputStream.read(b)) != -1) {
                byteArrayOutputStream.write(b, 0, i);
            }

            result = byteArrayOutputStream.toString();
        } catch (IOException var17) {
            var17.printStackTrace();
        } finally {
            this.closeRes(inputStream, outputStream);
            this.closeRes((OutputStream) byteArrayOutputStream);
            connection.disconnect();
        }

        return result;
    }

    private void closeRes(InputStream inputStream, OutputStream outputStream) {
        this.closeRes(inputStream);
        this.closeRes(outputStream);
    }

    private void closeRes(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    private void closeRes(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }
    }

    //根据文件链接把文件下载下来并且转成字节码
    public static byte[] getImageFromURL(String urlPath) {
        byte[] data = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            // conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);
            is = conn.getInputStream();
            if (conn.getResponseCode() == 200) {
                data = readInputStream(is);
            } else {
                data = null;
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
            conn.disconnect();
        }
        return data;
    }

    public static byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            baos.flush();
        } catch (IOException e) {
        }
        byte[] data = baos.toByteArray();
        try {
            is.close();
            baos.close();
        } catch (IOException e) {
        }
        return data;
    }
}