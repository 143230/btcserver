package com.btc.app.spider.service;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import com.btc.app.service.CoinService;
import com.btc.app.service.NewsService;
import com.btc.app.spider.htmlunit.JubiHtmlUnitSpider;
import com.btc.app.statistics.SystemStatistics;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;

public class JubiHtmlUnitSpiderService extends BasicHtmlUnitSpiderService {
    private CoinService coinService;
    private NewsService newsService;
    private JubiHtmlUnitSpider spider;
    private long time_used = 0;
    private static final String JUBI_URL = "https://www.jubi.com/";
    private static final int RESTART_INTERVAL = 50;
    private SystemStatistics statistics;

    public JubiHtmlUnitSpiderService(CoinService service,NewsService newsService) {
        this.coinService = service;
        this.newsService = newsService;
        statistics = SystemStatistics.getInstance();
    }

    public void run() {
        boolean succ = true;
        while (true){
            try {
                if(spider != null && time_used >RESTART_INTERVAL && time_used % RESTART_INTERVAL == 0){
                    logger.info("JubiHtmlUnitSpiderService Has Been Used: "+time_used+", Restarting.");
                    spider.close();
                    spider = null;
                    statistics.add("totalSuccessCount",1);
                }
                if(spider != null && spider.isBadlink()){
                    logger.info("JubiHtmlUnitSpiderService Has Changed To Bad Link: "+time_used+", Restarting.");
                    spider.close();
                    statistics.add("totalErrorCount",1);
                    spider = null;
                }
                succ = false;
                if(spider == null) {
                    statistics.add("putIntoQueue",1);
                    spider = new JubiHtmlUnitSpider(JUBI_URL);
                    spider.openAndWait();
                }
                time_used++;
                spider.parseHtml();
                coinService.handleResult(spider);
                newsService.handleResult(spider);
                //Thread.sleep(5000);
                synchronized (spider) {
                    spider.wait(5000);
                }
                succ = true;
            } catch (InterruptedException e) {
                System.out.println("InterruptedException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+JUBI_URL);
                spider.close();
                spider = null;
                statistics.add("InterruptedException",1);
                //程序被中断，此处应该提醒管理员
                break;
            } catch (IllegalArgumentException e){
                System.out.println("IllegalArgumentException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+JUBI_URL);
                spider.close();
                spider = null;
                statistics.add("IllegalArgumentException",1);
                break;
            } catch (TimeoutException e){
                System.out.println("TimeoutException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+JUBI_URL);
                spider.release();
                spider = null;
                statistics.add("TimeoutException",1);
            } catch (XpathSyntaxErrorException e){
                System.out.println("XpathSyntaxErrorException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+JUBI_URL);
                spider.release();
                spider = null;
                statistics.add("XpathSyntaxErrorException",1);
            } catch (IOException e){
                System.out.println("IOException: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+JUBI_URL);
                spider.release();
                spider = null;
                statistics.add("IOException",1);
            } catch (Exception e){
                System.out.println("Exception: ["+e.getClass()+"] Message:"+e.getMessage()+"\tUrl: "+JUBI_URL);
                e.printStackTrace();
                //spider.close();
                statistics.add("OtherException",1);
                statistics.add(e.getClass().getSimpleName(),1);
                if(spider != null) {
                    spider.release();
                    spider = null;
                }
            } finally {
                if(!succ){
                    statistics.add("totalErrorCount",1);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new JubiHtmlUnitSpiderService(null,null));
        thread.start();
        Thread.sleep(20000);
        System.out.println("send interrupt signal");
        thread.interrupt();
        Thread.sleep(10000);
    }
}
