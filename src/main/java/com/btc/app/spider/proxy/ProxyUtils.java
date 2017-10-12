package com.btc.app.spider.proxy;

import com.btc.app.bean.ProxyBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ProxyUtils {
    private static final int DEFAULT_TIMEOUT = 10000;
    private static final String FOREIGN_URL = "https://twitter.com/push_service_worker.js";
    private static final String HTTPS_URL = "https://tb1.bdstatic.com/tb/static-tbmall/widget/paykey_dialog/paykey_dialog.js";
    private static final String HTTP_URL = "http://jsqmt.qq.com/cdn_djl.js";

    public static long HttpsProxy(ProxyBean bean) throws Exception {
        return HttpsProxy(bean, HTTPS_URL);
    }

    public static long HttpProxy(ProxyBean bean) throws Exception {
        return HttpProxy(bean, HTTP_URL);
    }

    public static long foreignHttpsProxy(ProxyBean bean) throws Exception{
        return HttpsProxy(bean,FOREIGN_URL);
    }

    public static long HttpsProxy(ProxyBean bean, String url) throws Exception {
        HttpsURLConnection httpsConn;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        BufferedReader reader = null;
        try {
            URL urlClient = new URL(url);
            //System.out.println("请求的URL========：" + urlClient);

            SSLContext sc = SSLContext.getInstance("SSL");
            // 指定信任https
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
            //创建代理虽然是https也是Type.HTTP
            Proxy proxy1 = new Proxy(bean.getType(), new InetSocketAddress(bean.getHost(), bean.getPort()));
            long start = System.currentTimeMillis();
            //设置代理
            httpsConn = (HttpsURLConnection) urlClient.openConnection(proxy1);

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
            // 发送POST请求必须设置如下两行
            //httpsConn.setDoOutput(true);
            //httpsConn.setDoInput(true);
            /*// 获取URLConnection对象对应的输出流
            if (param != null && param.length() > 0) {
                out = new PrintWriter(httpsConn.getOutputStream());
                // 发送请求参数
                out.print(param);
                // flush输出流的缓冲
                out.flush();
            }*/
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(httpsConn.getInputStream()));
            String line = in.readLine();
            // 断开连接
            httpsConn.disconnect();
            long end = System.currentTimeMillis();
            if (!line.startsWith("_.Module.define")) {
                throw new IllegalStateException("the content error for url:" + url);
            }
            return end - start;
            //System.out.println("====result====" + result);
            //System.out.println("返回结果：" + httpsConn.getResponseMessage());

        } catch (Exception e) {
            throw e;
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

    public static long HttpProxy(ProxyBean bean, String url) throws Exception {
        HttpURLConnection httpConn;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        BufferedReader reader = null;
        try {
            URL urlClient = new URL(url);
            //System.out.println("请求的URL========：" + urlClient);
            //创建代理
            Proxy proxy1 = new Proxy(bean.getType(), new InetSocketAddress(bean.getHost(), bean.getPort()));
            long start = System.currentTimeMillis();
            //设置代理
            httpConn = (HttpURLConnection) urlClient.openConnection(proxy1);
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
            // 发送POST请求必须设置如下两行
            //httpConn.setDoOutput(true);
            //httpConn.setDoInput(true);
            /*// 获取URLConnection对象对应的输出流
            if (param != null && param.length() > 0) {
                out = new PrintWriter(httpConn.getOutputStream());
                // 发送请求参数
                out.print(param);
                // flush输出流的缓冲
                out.flush();
            }*/
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream()));
            String line = in.readLine();
            // 断开连接
            httpConn.disconnect();
            long end = System.currentTimeMillis();
            if (!line.startsWith("(function(){")) {
                //System.out.println(line);
                throw new IllegalStateException("the content error for url:" + url);
            }
            return end - start;
        } catch (Exception e) {
            throw e;
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


    public static Proxy.Type parseType(String type) {
        if (type.contains("http") || type.contains("HTTP") || type.length() <= 0) {
            return Proxy.Type.HTTP;
        }else if(type.contains("socks5")){
            return Proxy.Type.SOCKS;
        }
        return Proxy.Type.HTTP;
    }

    public static void main(String[] args) throws Exception {
        ProxyBean bean = new ProxyBean("174.120.70.232", 80, Proxy.Type.HTTP,"上海");
        System.out.println(foreignHttpsProxy(bean));
    }
}
