package com.btc.app.spider.phantomjs;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

/**
 * Created by cuixuan on 2017/8/22.
 */
public abstract class BasicSpider {
    protected WebDriver driver;
    protected String url;
    private boolean isReady;

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public BasicSpider(WebDriver driver, String url){
        this.driver = driver;
        this.url = url;
        isReady = false;
    }
    public void openAndWait() throws TimeoutException{
        String prev = driver.getCurrentUrl();
        System.out.println(prev);
        driver.get(this.url);
        String afte = driver.getCurrentUrl();
        System.out.println(afte);
        isReady = true;
    }

    public boolean isReady(){
        return this.isReady;
    }
    
    public WebDriver getDriver(){
        return this.driver;
    }

    public static WebDriver createDriver(){
        DesiredCapabilities dcaps = DesiredCapabilities.chrome();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", true);
        //cs、s搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持、
        dcaps.setJavascriptEnabled(true);
        //驱动支持
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,"/usr/local/bin/phantomjs");
        dcaps.setCapability(
                PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX
                        + "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        dcaps.setCapability(
                PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX
                        + "Accept-Language",
                "en,zh;q=0.8,zh-CN;q=0.6");

        dcaps.setCapability(
                PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX
                        + "Connection",
                "keep-alive");
        //创建无界面浏览器对象
        WebDriver driver = new PhantomJSDriver(dcaps);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
        return driver;

    }

    public abstract void parseHtml(int time_wait) throws Exception;
}
