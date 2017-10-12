package com.btc.app.spider.proxy;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.bean.ProxyBean;
import com.btc.app.spider.service.BasicHtmlUnitSpiderService;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class KuaiDaiLiHtmlUnitSpiderService extends BasicHtmlUnitSpiderService {
    private KuaiDaiLiHtmlUnitSpider spider;
    private final String URL;
    private BlockingQueue<ProxyBean> inQueue;

    public KuaiDaiLiHtmlUnitSpiderService(BlockingQueue<ProxyBean> inQueue, String url) {
        this.URL = url;
        this.inQueue = inQueue;
    }

    public void run() {
        ProxyServiceThread pool = ProxyServiceThread.getInstance();
        ProxyBean bean = null;
        try {
            spider = new KuaiDaiLiHtmlUnitSpider(inQueue,URL,BrowserVersion.INTERNET_EXPLORER);
            //spider.setJavaScriptEnabled(false);

            bean  = pool.getProxy(1, TimeUnit.SECONDS);
            if(bean != null){
                spider.setProxy(bean);
                System.out.println("using proxy: "+bean);
            }
            spider.openAndWait();
            spider.parseHtml();
            //System.out.println("Finished for Url: "+URL);
            if(bean != null) {
                pool.releaseProxy(bean);
            }
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+URL);
            //程序被中断，此处应该提醒管理员
        } catch (TimeoutException e){
            System.out.println("TimeoutException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+URL);
        } catch (XpathSyntaxErrorException e){
            System.out.println("XpathSyntaxErrorException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+URL);
        } catch (IOException e){
            System.out.println("IOException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+URL);
        } catch (Exception e){
            System.out.println("Exception: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+URL);
            e.printStackTrace();
        }finally {
            spider.close();
        }
    }
}
