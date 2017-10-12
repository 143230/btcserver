package com.btc.app.spider.service;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.service.CoinService;
import com.btc.app.service.NewsService;
import com.btc.app.spider.htmlunit.Btc38HtmlUnitSpider;
import com.btc.app.spider.htmlunit.BterHtmlUnitSpider;
import com.btc.app.statistics.SystemStatistics;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;

public class BterHtmlUnitSpiderService extends BasicHtmlUnitSpiderService {
    private CoinService coinService;
    private BterHtmlUnitSpider spider;
    private final String BTER_URL;
    public static final String BTER_BTC = "https://bter.com/datasingle/BTC";
    public static final String BTER_CNY = "https://bter.com/datasingle/CNY";
    public static final String BTER_ETH = "https://bter.com/datasingle/ETH";
    private SystemStatistics statistics;

    public BterHtmlUnitSpiderService(CoinService service,String url) throws Exception {
        BTER_URL = url;
        this.coinService = service;
        this.statistics = SystemStatistics.getInstance();
    }

    public void run() {
        boolean succ = false;
        try {
            statistics.add("putIntoQueue",1);
            spider = new BterHtmlUnitSpider(BTER_URL);
            spider.setJavaScriptEnabled(false);
            spider.openAndWait();
            spider.parseHtml();
            coinService.handleResult(spider);
            succ = true;
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+BTER_URL);
            spider.close();
            statistics.add("InterruptedException",1);
            //程序被中断，此处应该提醒管理员
        } catch (TimeoutException e){
            System.out.println("TimeoutException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+BTER_URL);
            statistics.add("TimeoutException",1);
        } catch (XpathSyntaxErrorException e){
            System.out.println("XpathSyntaxErrorException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+BTER_URL);
            statistics.add("XpathSyntaxErrorException",1);
        } catch (IOException e){
            System.out.println("IOException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+BTER_URL);
            statistics.add("IOException",1);
        } catch (Exception e){
            System.out.println("Exception: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+BTER_URL);
            statistics.add("OtherException",1);
            statistics.add(e.getClass().getSimpleName(),1);
            e.printStackTrace();
        }finally {
            if(!succ){
                statistics.add("totalErrorCount",1);
            }else{
                statistics.add("totalSuccessCount",1);
            }
            //spider.close();
            if(spider != null) {
                spider.release();
            }
        }
    }
}
