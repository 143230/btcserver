package com.btc.app.listener;

import com.btc.app.bean.CoinBean;
import com.btc.app.push.xinge.XinGePush;
import com.btc.app.service.CoinService;
import com.btc.app.service.NewsService;
import com.btc.app.service.WeiboService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.math.BigDecimal;

public class SpiderListener implements ServletContextListener {
    private SchedulerThread schedulerThread;
    CoinService coinService;
    NewsService newsService;
    WeiboService weiboService;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("初始化监听器");
        WebApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext());
        try {
            coinService = (CoinService) ac.getBean("coinService");
            newsService = (NewsService) ac.getBean("newsService");
            weiboService = (WeiboService) ac.getBean("weiboService");
            CoinBean bean = coinService.testConnection();
            /*XinGePush push = XinGePush.getInstance();
            bean.setChinesename("测试信息："+bean.getChinesename());
            System.out.println(push.pushCoinToAll(bean, BigDecimal.ZERO));*/
            System.out.println(bean);
            String str = null;
            if (str == null && schedulerThread == null) {
                schedulerThread = new SchedulerThread(coinService, newsService, weiboService);
                schedulerThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("销毁监听器");
        if (schedulerThread != null && !schedulerThread.isInterrupted()) {
            schedulerThread.interrupt();
        }
    }
}
