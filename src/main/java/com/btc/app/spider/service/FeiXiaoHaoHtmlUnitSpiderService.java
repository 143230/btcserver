package com.btc.app.spider.service;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.service.CoinService;
import com.btc.app.spider.htmlunit.BterHtmlUnitSpider;
import com.btc.app.spider.htmlunit.FeiXiaoHaoHtmlUnitSpider;
import com.btc.app.statistics.SystemStatistics;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;

public class FeiXiaoHaoHtmlUnitSpiderService extends BasicHtmlUnitSpiderService {
    private CoinService coinService;
    private FeiXiaoHaoHtmlUnitSpider spider;
    private final String URL;
    public static final String FXH_URL = "http://www.feixiaohao.com/all/#CNY";
    private SystemStatistics statistics;

    public FeiXiaoHaoHtmlUnitSpiderService(CoinService service, String url) throws Exception {
        this.URL = url;
        this.coinService = service;
        this.statistics = SystemStatistics.getInstance();
    }

    public void run() {
        boolean succ = false;
        try {
            statistics.add("putIntoQueue",1);
            spider = new FeiXiaoHaoHtmlUnitSpider(this.URL);
            spider.setJavaScriptEnabled(false);
            spider.openAndWait();
            spider.parseHtml();
            coinService.handleResult(spider);
            succ = true;
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+this.URL);
            spider.close();
            statistics.add("InterruptedException",1);
            //程序被中断，此处应该提醒管理员
        } catch (TimeoutException e){
            System.out.println("TimeoutException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+this.URL);
            statistics.add("TimeoutException",1);
        } catch (XpathSyntaxErrorException e){
            System.out.println("XpathSyntaxErrorException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+this.URL);
            statistics.add("XpathSyntaxErrorException",1);
        } catch (IOException e){
            System.out.println("IOException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+this.URL);
            statistics.add("IOException",1);
        } catch (Exception e){
            System.out.println("Exception: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+this.URL);
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
