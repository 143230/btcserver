package com.btc.app.spider.phantomjs;

import com.btc.app.service.WeiboService;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class WeiboSpiderService extends BasicSpiderService {
    private WeiboService weiboService;
    private WeiboSpider spider;
    private String id;

    public WeiboSpiderService(DriverManager manager, WeiboService service, String wbid) {
        super(manager);
        this.weiboService = service;
        this.id = wbid;
    }

    public void run() {
        try {
            WebDriver driver = this.getDriver();
            spider = new WeiboSpider(driver, this.id);
            spider.openAndWait();
            spider.parseHtml(5);
            weiboService.handleResult(spider);
        } catch (InterruptedException e) {
            logger.info("Thread Was Interrupted, Exiting.");
            if(spider != null) {
                WebDriver driver = spider.getDriver();
                shutdownDriver(driver);
                spider = null;
            }
        } catch (TimeoutException e){
            logger.info("Weibo Spider Timeout.");
            if(spider != null){
                WebDriver driver = spider.getDriver();
                shutdownDriver(driver);
                spider = null;
            }
        } catch (Exception e){
            logger.info("Exception: "+e.getMessage());
            e.printStackTrace();
            if(spider != null) {
                WebDriver driver = spider.getDriver();
                shutdownDriver(driver);
                spider = null;
            }
        }finally {
            if(spider != null) {
                WebDriver driver = spider.getDriver();
                releaseDriver(driver);
                spider = null;
            }
        }
    }
}
