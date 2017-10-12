package com.btc.app.spider.phantomjs;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.service.CoinService;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class JubiSpiderService extends BasicSpiderService {
    private CoinService coinService;
    private JubiSpider spider;
    private static final String JUBI_URL = "https://www.jubi.com/";

    public JubiSpiderService(DriverManager manager, CoinService service) {
        super(manager);
        this.coinService = service;
    }

    public void run() {
        while (true){
            try {
                getResult();
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                System.out.println("Thread Was Interrupted, Exiting.");
                if(spider != null) {
                    WebDriver driver = spider.getDriver();
                    shutdownDriver(driver);
                    spider = null;
                }
            } catch (TimeoutException e){
                System.out.println("Jubi Spider Timeout.");
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
            }
        }
    }

    public void getResult() throws InterruptedException, XpathSyntaxErrorException {
        if(spider == null){
            WebDriver driver = this.getDriver();
            spider = new JubiSpider(driver, JUBI_URL);
            spider.openAndWait();
        }
        System.out.println(spider.driver.getCurrentUrl());
        spider.parseHtml(0);
        coinService.handleResult(spider);
    }
}
