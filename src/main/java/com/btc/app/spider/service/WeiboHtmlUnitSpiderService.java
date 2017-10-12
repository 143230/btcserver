package com.btc.app.spider.service;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.service.WeiboService;
import com.btc.app.spider.htmlunit.WeiboHtmlUnitSpider;
import com.btc.app.statistics.SystemStatistics;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;
import java.util.Random;

public class WeiboHtmlUnitSpiderService extends BasicHtmlUnitSpiderService {
    private WeiboService weiboService;
    private WeiboHtmlUnitSpider spider;
    private final String WEIBO_ID;//1839109034
    private final String WEIBO_URL;
    private SystemStatistics statistics;

    public WeiboHtmlUnitSpiderService(WeiboService service,String wbid) {
        this.weiboService = service;
        this.WEIBO_ID = wbid;
        this.WEIBO_URL = String.format("https://m.weibo.cn/u/%s?uid=%s",wbid,wbid);
        this.statistics = SystemStatistics.getInstance();
    }

    public void run() {
        boolean succ = false;
        try {
            statistics.add("putIntoQueue",1);
            spider = new WeiboHtmlUnitSpider(WEIBO_ID,BrowserVersion.INTERNET_EXPLORER);
            spider.openAndWait();
            spider.parseHtml();
            weiboService.handleResult(spider);
            succ = true;
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+WEIBO_URL);
            spider.close();
            statistics.add("InterruptedException",1);
            //程序被中断，此处应该提醒管理员
        } catch (TimeoutException e){
            System.out.println("TimeoutException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+WEIBO_URL);
            statistics.add("TimeoutException",1);
        } catch (XpathSyntaxErrorException e){
            System.out.println("XpathSyntaxErrorException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+WEIBO_URL);
            statistics.add("XpathSyntaxErrorException",1);
        } catch (IOException e){
            System.out.println("IOException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+WEIBO_URL);
            statistics.add("IOException",1);
        } catch (Exception e){
            System.out.println("Exception: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+WEIBO_URL);
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
