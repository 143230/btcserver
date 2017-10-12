package com.btc.app.spider.http;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.service.CoinService;
import com.btc.app.spider.htmlunit.CoinMarketCapHtmlUnitSpider;
import com.btc.app.spider.service.BasicHtmlUnitSpiderService;
import com.btc.app.statistics.SystemStatistics;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;

public class CoinMarketAPIHttpSpiderService extends BasicHtmlUnitSpiderService {
    private CoinService coinService;
    private CoinMarketAPIHttpSpider spider;
    private final String convert;
    private SystemStatistics statistics;

    public CoinMarketAPIHttpSpiderService(CoinService service, String convert) throws Exception {
        this.convert = convert;
        this.coinService = service;
        this.statistics = SystemStatistics.getInstance();
    }

    public void run() {
        boolean succ = false;
        try {
            statistics.add("putIntoQueue", 1);
            spider = new CoinMarketAPIHttpSpider(this.convert);
            spider.parseHtml();
            coinService.handleResult(spider);
            succ = true;
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: [" + e.getClass() + "] Message:" + e.getMessage() + "\tUrl: " + this.convert);
            statistics.add("InterruptedException", 1);
            //程序被中断，此处应该提醒管理员
        } catch (TimeoutException e) {
            System.out.println("TimeoutException: [" + e.getClass() + "] Message:" + e.getMessage() + "\tUrl: " + this.convert);
            statistics.add("TimeoutException", 1);
        } catch (XpathSyntaxErrorException e) {
            System.out.println("XpathSyntaxErrorException: [" + e.getClass() + "] Message:" + e.getMessage() + "\tUrl: " + this.convert);
            statistics.add("XpathSyntaxErrorException", 1);
        } catch (IOException e) {
            System.out.println("IOException: [" + e.getClass() + "] Message:" + e.getMessage() + "\tUrl: " + this.convert);
            statistics.add("IOException", 1);
        } catch (Exception e) {
            System.out.println("Exception: [" + e.getClass() + "] Message:" + e.getMessage() + "\tUrl: " + this.convert);
            statistics.add("OtherException", 1);
            statistics.add(e.getClass().getSimpleName(), 1);
            e.printStackTrace();
        } finally {
            if (!succ) {
                statistics.add("totalErrorCount", 1);
            } else {
                statistics.add("totalSuccessCount", 1);
            }
        }
    }
}
