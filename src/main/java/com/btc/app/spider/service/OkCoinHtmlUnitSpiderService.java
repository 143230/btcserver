package com.btc.app.spider.service;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.service.CoinService;
import com.btc.app.service.NewsService;
import com.btc.app.spider.htmlunit.BterHtmlUnitSpider;
import com.btc.app.spider.htmlunit.OkCoinHtmlUnitSpider;
import com.btc.app.statistics.SystemStatistics;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;

public class OkCoinHtmlUnitSpiderService extends BasicHtmlUnitSpiderService {
    private CoinService coinService;
    private NewsService newsService;
    private OkCoinHtmlUnitSpider spider;
    private String url;
    public static final String OKCOIN_USD_URL = "https://www.okcoin.com/";
    public static final String OKCOIN_CNY_URL = "https://www.okcoin.cn/";
    private SystemStatistics statistics;

    public OkCoinHtmlUnitSpiderService(CoinService service,NewsService newsService, String url) throws Exception {
        this.coinService = service;
        this.newsService = newsService;
        this.url = url;
        this.statistics = SystemStatistics.getInstance();
    }

    public void run() {
        Thread.currentThread().setName("OkCoinHtmlUnitSpiderService");
        boolean succ = false;
        try {
            statistics.add("putIntoQueue",1);
            spider = new OkCoinHtmlUnitSpider(url);
            //spider.setJavaScriptEnabled(false);
            spider.openAndWait();
            spider.parseHtml();
            coinService.handleResult(spider);
            newsService.handleResult(spider);
            succ = true;
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+url);
            spider.close();
            statistics.add("InterruptedException",1);
            //程序被中断，此处应该提醒管理员
        } catch (TimeoutException e){
            System.out.println("TimeoutException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+url);
            statistics.add("TimeoutException",1);
        } catch (XpathSyntaxErrorException e){
            System.out.println("XpathSyntaxErrorException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+url);
            statistics.add("XpathSyntaxErrorException",1);
        } catch (IOException e){
            System.out.println("IOException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+url);
            statistics.add("IOException",1);
        } catch (Exception e){
            System.out.println("Exception: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+url);
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
