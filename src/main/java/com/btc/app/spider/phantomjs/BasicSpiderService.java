package com.btc.app.spider.phantomjs;

import com.btc.app.spider.phantomjs.DriverManager;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

public abstract class BasicSpiderService implements Runnable {
    protected static final Logger logger = Logger.getLogger("BasicSpiderService");
    protected static final int RETRY_TIME = 3;
    private DriverManager manager;
    public BasicSpiderService(DriverManager manager){
        this.manager = manager;
    }
    public WebDriver getDriver() throws InterruptedException {
        return manager.getDriver();
    }
    public boolean releaseDriver(WebDriver driver){
        return manager.releaseDriver(driver);
    }
    public WebDriver restartDriver(WebDriver driver){
        return manager.restartDriver(driver);
    }
    public void shutdownDriver(WebDriver driver){
        manager.shutdownDriver(driver);
    }
}
