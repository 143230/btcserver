package com.btc.app.spider.htmlunit;

import com.btc.app.bean.ProxyBean;
import com.btc.app.pool.BoundedBlockingPool;
import com.btc.app.pool.Pool;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.javascript.host.Window;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import org.apache.log4j.Logger;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by cuixuan on 2017/8/22.
 */
public abstract class HtmlUnitBasicSpider {
    //private static final BlockingQueue<WebClient> queue = new LinkedBlockingQueue<WebClient>();
    private static final BoundedBlockingPool<WebClient> pool = new BoundedBlockingPool<WebClient>(300000L,7,
            20, 0,
            new WebClientValidator(),new WebClientFactory());
    protected Logger logger = Logger.getLogger(HtmlUnitBasicSpider.class);
    protected WebClient client;
    protected HtmlPage page;
    protected String url;

    protected boolean finished;
    private static final int DEFAULT_TIMEWAIT = 10;

    private static final int MIN_TIMEWAIT = 3;
    private final int time_wait;
    private final BrowserVersion version;
    private static final BrowserVersion DEFAULR_BROWSER_VERSION = BrowserVersion.INTERNET_EXPLORER;
    public HtmlUnitBasicSpider(String url, int time_wait,BrowserVersion version) throws InterruptedException {
        this.url = url;
        this.time_wait = time_wait;
        this.version = version;
        this.finished = false;
        while(this.client == null) {
            //this.client = getHtmlPage();
            this.client = pool.get();
            new HtmlUnitConnectionListener(client, this);
//            logger.info("Using client: "+client+" Object: "+this);
        }
    }

    public HtmlUnitBasicSpider(String url) throws InterruptedException {
        this(url,DEFAULT_TIMEWAIT,DEFAULR_BROWSER_VERSION);
    }

    public HtmlUnitBasicSpider(String url, int time_wait) throws InterruptedException {
        this(url,time_wait,DEFAULR_BROWSER_VERSION);
    }
    public HtmlUnitBasicSpider(String url, BrowserVersion version) throws InterruptedException {
        this(url,DEFAULT_TIMEWAIT,version);
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    public void openAndWait() throws InterruptedException,IOException {
        if(this.client == null) {
            throw new IllegalStateException("Null Web Client Found.");
        }
//        System.out.println("Window Num:"+client.getWebWindows().size());
        this.page = client.getPage(new URL(url));
        for(int i=0;i<time_wait;i++) {
            if(this.finished && i>=MIN_TIMEWAIT)break;//页面至少加载3秒
            TimeUnit.SECONDS.sleep(1);
        }
    }
    public static WebClient getHtmlPage(){
        BrowserVersion version = BrowserVersion.INTERNET_EXPLORER;
        //version.setUserAgent("Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36");
        version.setBrowserLanguage("en,zh;q=0.8,zh-CN;q=0.6");
        version.setHtmlAcceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        final WebClient webClient=new WebClient(version);
        webClient.addRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        webClient.addRequestHeader("Connection","keep-alive");
        webClient.addRequestHeader("Pragma","no-cache");
        webClient.addRequestHeader("Cache-Control","no-cache");
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setCssEnabled(false);//设置css是否生效
        webClient.getOptions().setJavaScriptEnabled(true);//设置js是否生效
        webClient.getOptions().setTimeout(10000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setRefreshHandler(new ImmediateRefreshHandler());
        webClient.getOptions().setGeolocationEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//设置ajax请求
        webClient.getOptions().setActiveXNative(true);
        webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
            public void scriptException(HtmlPage page, ScriptException scriptException) {
                //System.out.println("Caught script exception : "+scriptException.getMessage());
            }

            public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
                //System.out.println("Caught Timeout exception : AllowedTime"+allowedTime+" ExecutionTime: "+executionTime);
            }

            public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {
                //System.out.println("Caught MalformedURLException exception : "+malformedURLException.getMessage());
            }

            public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {
                //System.out.println("Caught loadScriptError exception : "+exception.getMessage());
            }
        });
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
        webClient.setIncorrectnessListener(new IncorrectnessListener() {
            public void notify(String message, Object origin) {
                //System.out.println("[Notify] " + message);
            }
        });
        webClient.waitForBackgroundJavaScript(3000);
        JavaScriptEngine engine = new DimissLogJavaScriptEnginee(webClient);
        webClient.setJavaScriptEngine(engine);

        ConfirmHandler okHandler = new ConfirmHandler() {
            public boolean handleConfirm(Page page, String message) {
                //System.out.println("[Confirm] " + message);
                return true;
            }
        };

        AlertHandler alertHandler = new AlertHandler() {
            public void handleAlert(Page page, String message) {
                //System.out.println("[Alert] " + message);
            }
        };
        webClient.setConfirmHandler(okHandler);
        webClient.setAlertHandler(alertHandler);
        return webClient;
    }
    public void setJavaScriptEnabled(boolean enabled){
        this.client.getOptions().setJavaScriptEnabled(enabled);
    }

    public abstract void parseHtml() throws Exception;

    public abstract void downloadFile(WebRequest request, WebResponse response);

    public boolean checkUrl(String url){
        return false;
    };
    public void close(){
        //client.getCurrentWindow().getJobManager().removeAllJobs();
        //client.close();
        //logger.info("destroy the client:"+client+" Object: "+this);
        //System.gc();
        pool.destroy(client);
    }

    public void release(){
        client.getOptions().setJavaScriptEnabled(true);
        ProxyConfig proxyConfig = this.client.getOptions().getProxyConfig();
        proxyConfig.setProxyHost(null);
        //logger.info("return the client: "+client+" Object: "+this);
        //client.setWebConnection(null);
        WebConnection connection = client.getWebConnection();
        if(connection instanceof HtmlUnitConnectionListener){
            client.setWebConnection(((HtmlUnitConnectionListener)connection).getWrappedWebConnection());
        }
//        System.out.println(client.getWebConnection().getClass().getSimpleName());
        pool.release(client);
    }
    public void setProxy(ProxyBean proxy){
        ProxyConfig proxyConfig = this.client.getOptions().getProxyConfig();
        proxyConfig.setProxyHost(proxy.getHost());
        proxyConfig.setProxyPort(proxy.getPort());
        if(proxy.getType() == Proxy.Type.SOCKS){
            proxyConfig.setSocksProxy(true);
        }
    }
}