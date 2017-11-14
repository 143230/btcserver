package com.btc.app.spider.service;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.service.CoinService;
import com.btc.app.spider.phantomjs.FeiXiaoHaoCoinInfoSpider;
import com.btc.app.spider.phantomjs.WeiboSpider;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.io.*;

public class FeiXiaoHaoCoinInfoHtmlUnitSpiderService extends BasicHtmlUnitSpiderService {
    private CoinService coinService;
    private FeiXiaoHaoCoinInfoSpider spider;

    public FeiXiaoHaoCoinInfoHtmlUnitSpiderService(CoinService service) throws Exception {
        this.coinService = service;
    }

    public void run() {
        WebDriver driver = null;
        try {
            System.out.println(new File("./").getAbsolutePath());
            String[] coinids = {"0x", "10mtoken", "1337coin", "1credit",
                                "2give", "300-token", "42-coin", "808coin",
                                "8bit", "9coin", "abjcoin", "abncoin", "aces"};
            String coinid = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("coinid.list")));
            driver = WeiboSpider.createDriver();
            while ((coinid = reader.readLine()) != null) {
//            for(String coinid:coinids){
                System.out.println(coinid);
                spider = new FeiXiaoHaoCoinInfoSpider(driver, coinid);
                spider.openAndWait();
                spider.parseHtml(10);
//                coinService.insertCoinInfo(spider.getInfoBean());
                System.out.println(spider.getInfoBean());
                driver.get("about:blank");
            }
            driver.close();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: [" + e.getClass() + "] Message:" + e.getMessage() + "\tUrl: ");
            //程序被中断，此处应该提醒管理员
        } catch (TimeoutException e) {
            System.out.println("TimeoutException: [" + e.getClass() + "] Message:" + e.getMessage() + "\tUrl: ");
        } catch (XpathSyntaxErrorException e) {
            System.out.println("XpathSyntaxErrorException: [" + e.getClass() + "] Message:" + e.getMessage() + "\tUrl: ");
        } catch (IOException e) {
            System.out.println("IOException: [" + e.getClass() + "] Message:" + e.getMessage() + "\tUrl: ");
        } catch (Exception e) {
            System.out.println("Exception: [" + e.getClass() + "] Message:" + e.getMessage() + "\tUrl: ");
            e.printStackTrace();
        } finally {
            if (spider != null) {
                driver.close();
            }
        }
    }
}
