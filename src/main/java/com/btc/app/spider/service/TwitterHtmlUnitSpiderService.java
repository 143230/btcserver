package com.btc.app.spider.service;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.bean.ProxyBean;
import com.btc.app.service.WeiboService;
import com.btc.app.spider.htmlunit.TwitterHtmlUnitSpider;
import com.btc.app.spider.htmlunit.WeiboHtmlUnitSpider;
import com.btc.app.statistics.SystemStatistics;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;
import java.net.Proxy;
import java.util.Random;

public class TwitterHtmlUnitSpiderService extends BasicHtmlUnitSpiderService {
    private WeiboService weiboService;
    private TwitterHtmlUnitSpider spider;
    private final String TWITTER_ID;//civickey
    private final String TWITTER_URL;
    private SystemStatistics statistics;

    public TwitterHtmlUnitSpiderService(WeiboService service, String twitterid) {
        this.weiboService = service;
        this.TWITTER_ID = twitterid;
        this.TWITTER_URL = String.format("https://mobile.twitter.com/%s",twitterid);
        this.statistics = SystemStatistics.getInstance();
    }

    public void run() {
        boolean succ = false;
        try {
            statistics.add("putIntoQueue",1);
            spider = new TwitterHtmlUnitSpider(TWITTER_URL);
            spider.setJavaScriptEnabled(false);
            ProxyBean bean = new ProxyBean("116.196.94.105",1080, Proxy.Type.SOCKS,"");
            spider.setProxy(bean);
            spider.openAndWait();
            spider.parseHtml();
            weiboService.handleResult(spider);
            succ = true;
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+TWITTER_URL);
            spider.close();
            statistics.add("InterruptedException",1);
            //程序被中断，此处应该提醒管理员
        } catch (TimeoutException e){
            System.out.println("TimeoutException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+TWITTER_URL);
            statistics.add("TimeoutException",1);
        } catch (XpathSyntaxErrorException e){
            System.out.println("XpathSyntaxErrorException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+TWITTER_URL);
            statistics.add("XpathSyntaxErrorException",1);
        } catch (IOException e){
            System.out.println("IOException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+TWITTER_URL);
            statistics.add("IOException",1);
        } catch (Exception e){
            System.out.println("Exception: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+TWITTER_URL);
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
