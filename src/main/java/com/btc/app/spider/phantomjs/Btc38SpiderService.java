package com.btc.app.spider.phantomjs;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.service.NewsService;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class Btc38SpiderService extends BasicSpiderService {
    private static final String BTC38_URL = "http://www.btc38.com/";
    private NewsService newsService;
    private Btc38Spider spider;

    public Btc38SpiderService(DriverManager manager, NewsService service) {
        super(manager);
        this.newsService = service;
    }

    public void run() {
        try {
            getResult();
        } catch (InterruptedException e) {
            System.out.println("Thread Was Interrupted, Exiting.");
            if(spider != null) {
                WebDriver driver = spider.getDriver();
                shutdownDriver(driver);
                spider = null;
            }
        } catch (TimeoutException e){
            System.out.println("Btc38 Spider Timeout.");
            if(spider != null){
                WebDriver driver = spider.getDriver();
                shutdownDriver(driver);
                spider = null;
            }
        } catch (XpathSyntaxErrorException e){
            System.out.println("Web HTML Has Been Changed, Please Quickly Update.");
            if(spider != null) {
                WebDriver driver = spider.getDriver();
                releaseDriver(driver);
                spider = null;
            }
        } catch (Exception e){
            System.out.println("Exception: "+e.getMessage());
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

    public void getResult() throws InterruptedException, XpathSyntaxErrorException {
        WebDriver driver = this.getDriver();
        spider = new Btc38Spider(driver, BTC38_URL);
        spider.openAndWait();
        spider.parseHtml(5);
        newsService.handleResult(spider);
    }
}
