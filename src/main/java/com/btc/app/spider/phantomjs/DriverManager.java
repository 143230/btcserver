package com.btc.app.spider.phantomjs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DriverManager {
    private static final int DEFAULT_SIZE = 0;
    private static final int MAX_SIZE = 30;
    private BlockingQueue<WebDriver> queue;
    private int using_size;
    public DriverManager(int size,int maxsize){
        queue = new LinkedBlockingQueue<WebDriver>();
        using_size=0;
        for(int i=0;i<size && i<maxsize;i++){
            WebDriver driver = createDriver();
            queue.add(driver);
        }
    }
    public DriverManager(){
        this(DEFAULT_SIZE,MAX_SIZE);
    }

    public synchronized WebDriver getDriver() throws InterruptedException {
        while(true) {
            if (queue.size() <= 0) {
                if (using_size < MAX_SIZE) {
                    WebDriver driver = createDriver();
                    using_size++;
                    return driver;
                } else {
                    this.wait();
                }
            } else {
                WebDriver driver = queue.poll();
                using_size++;
                return driver;
            }
        }
    }

    public synchronized boolean releaseDriver(WebDriver driver){
        boolean flag = queue.add(driver);
        using_size--;
        this.notifyAll();
        return flag;
    }

    public WebDriver restartDriver(WebDriver driver){
        driver.close();
        driver.quit();
        driver = createDriver();
        return driver;
    }

    public void shutdownDriver(WebDriver driver){
        try {
            using_size--;
            driver.close();
            driver.quit();
        }catch (Exception e){
            System.out.println("Driver Close Exception: "+e.getMessage());
        }
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
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
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
}
