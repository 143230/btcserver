package com.btc.app.spider.http;

import com.btc.app.bean.ProxyBean;
import com.btc.app.spider.proxy.ProxyUtils;
import org.apache.http.HttpConnection;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpBasicSpider {
    private static final int DEFAULT_TIMEOUT = 30000;
    protected URL url;

    public HttpBasicSpider(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public String openAndGetContent() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        if (url.getProtocol().toLowerCase().equals("http")) {
            return getHttpContent();
        } else {
            return getHttpsContent();
        }
    }

    private String getHttpContent() throws IOException {
        HttpURLConnection httpConn;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        BufferedReader reader = null;
        try {
            //设置代理
            httpConn = (HttpURLConnection) this.url.openConnection();
            // 设置通用的请求属性
            httpConn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            httpConn.setRequestProperty("connection", "Keep-Alive");
            httpConn.setRequestProperty("Accept-Language", "en,zh;q=0.8,zh-CN;q=0.6");
            httpConn.setRequestProperty("Cache-Control", "no-cache");
            httpConn.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
            httpConn.setConnectTimeout(DEFAULT_TIMEOUT);
            httpConn.setReadTimeout(DEFAULT_TIMEOUT);
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream()));
            StringBuffer buff = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                buff.append(line);
            }
            // 断开连接
            httpConn.disconnect();
            return buff.toString();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private String getHttpsContent() throws NoSuchAlgorithmException, KeyManagementException, IOException {
        HttpsURLConnection httpsConn;
        PrintWriter out = null;
        BufferedReader in = null;
        BufferedReader reader = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            // 指定信任https
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
            //创建代理虽然是https也是Type.HTTP
            httpsConn = (HttpsURLConnection) url.openConnection();

            httpsConn.setSSLSocketFactory(sc.getSocketFactory());
            httpsConn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            // 设置通用的请求属性
            httpsConn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            httpsConn.setRequestProperty("connection", "Keep-Alive");
            httpsConn.setRequestProperty("Accept-Language", "en,zh;q=0.8,zh-CN;q=0.6");
            httpsConn.setRequestProperty("Cache-Control", "no-cache");
            httpsConn.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
            httpsConn.setConnectTimeout(DEFAULT_TIMEOUT);
            httpsConn.setReadTimeout(DEFAULT_TIMEOUT);
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(httpsConn.getInputStream()));
            StringBuffer buff = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                buff.append(line);
            }
            // 断开连接
            httpsConn.disconnect();
            System.out.println("获取文件："+url);
            return buff.toString();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
