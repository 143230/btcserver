package com.btc.app.listener;


import com.btc.app.service.CoinService;
import com.btc.app.service.NewsService;
import com.btc.app.service.WeiboService;
import com.btc.app.spider.htmlunit.BitFinexHtmlUnitSpider;
import com.btc.app.spider.http.CoinMarketAPIHttpSpiderService;
import com.btc.app.spider.http.WeiboHttpSpiderService;
import com.btc.app.spider.service.*;
import com.btc.app.statistics.SystemStatistics;
import com.btc.app.util.CoinNameMapper;

import java.util.concurrent.*;

import static com.btc.app.util.MarketTypeMapper.markets;

/**
 * 自定义一个 Class 线程类继承自线程类，重写 run() 方法，用于从后台获取并处理数据
 *
 * @author cuixuan
 */
public class SchedulerThread extends Thread {
    private CoinService coinService;
    private NewsService newsService;
    private WeiboService weiboService;
    private ThreadPoolExecutor executor;
    private SystemStatistics statistics = SystemStatistics.getInstance();

    public SchedulerThread(CoinService coinService, NewsService newsService, WeiboService weiboService) {
        this.coinService = coinService;
        this.newsService = newsService;
        this.weiboService = weiboService;
        executor = new ThreadPoolExecutor(
                20,
                80,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(80),
                new CustomThreadFactory(),
                new CustomRejectedExecutionHandler());
    }

    public void run() {
        System.out.println("--------开始执行-----------");
        statistics.setThreadPoolExecutor(executor);
//        executor.execute(new JubiHtmlUnitSpiderService(coinService, newsService));
        for (int i = 0; ; i++) {
            try {
//                if (i % 10 == 0) {
//                    executor.execute(new BterHtmlUnitSpiderService(coinService, BterHtmlUnitSpiderService.BTER_CNY));
//                    executor.execute(new BterHtmlUnitSpiderService(coinService, BterHtmlUnitSpiderService.BTER_ETH));
//                    executor.execute(new BterHtmlUnitSpiderService(coinService, BterHtmlUnitSpiderService.BTER_BTC));
//                  executor.execute(new OkCoinHtmlUnitSpiderService(coinService,newsService, OkCoinHtmlUnitSpiderService.OKCOIN_USD_URL));
//                  executor.execute(new OkCoinHtmlUnitSpiderService(coinService,newsService, OkCoinHtmlUnitSpiderService.OKCOIN_CNY_URL));
//                  System.out.println(executor.getActiveCount());
//                }
                if (i % 300 == 0) {
                    /*for (int j = 1; j < 13; j++) {
                        String url = String.format("http://www.feixiaohao.com/list_%s.html#CNY", j);
                        executor.execute(new FeiXiaoHaoHtmlUnitSpiderService(coinService, url));
//                        url = String.format("https://coinmarketcap.com/%s#CNY", j);
//                        executor.execute(new CoinMarketCapHtmlUnitSpiderService(coinService, url));
                    }*/
                    for (String convert : markets) {
                        executor.execute(new CoinMarketAPIHttpSpiderService(coinService, convert));
                    }
                    for(String siteid: CoinNameMapper.OTHER_PLATFORM_MAP.keySet()){
                        executor.execute(new CoinListHtmlUnitSpiderService(coinService, siteid));
                    }
//                  executor.execute(new BitFinexHtmlUnitSpiderService(coinService, url));
                }
                if (i % 180 == 0) {
                    executor.execute(new WeiboHttpSpiderService(weiboService, "1839109034"));
                    executor.execute(new WeiboHttpSpiderService(weiboService, "3632226187"));
                    executor.execute(new WeiboHttpSpiderService(weiboService, "2980854595"));
                    executor.execute(new WeiboHttpSpiderService(weiboService, "2188341020"));
                    executor.execute(new WeiboHttpSpiderService(weiboService, "3029680495"));
                    executor.execute(new WeiboHttpSpiderService(weiboService, "2149945883"));
                    executor.execute(new WeiboHttpSpiderService(weiboService, "1940696741"));
                    executor.execute(new WeiboHttpSpiderService(weiboService, "3803967956"));
                    executor.execute(new WeiboHttpSpiderService(weiboService, "3802128787"));
                    executor.execute(new WeiboHttpSpiderService(weiboService, "1908243651"));
                    executor.execute(new WeiboHttpSpiderService(weiboService, "5349265054"));
//                    executor.execute(new TwitterHtmlUnitSpiderService(weiboService, "civickey"));//VitalikButerin
//                    executor.execute(new TwitterHtmlUnitSpiderService(weiboService, "VitalikButerin"));
//                    executor.execute(new TwitterHtmlUnitSpiderService(weiboService, "VinnyLingham"));
//                    executor.execute(new TwitterHtmlUnitSpiderService(weiboService, "SatoshiLite"));
                }
                if (i % 180 == 0) {
//                    executor.execute(new Btc38HtmlUnitSpiderService(newsService));
                    executor.execute(new FeiXiaoHaoNewsHtmlUnitSpiderService(newsService));
                }
                sleep(1000);
            } catch (Exception e) {
                System.out.println("SchedulerThread Was been Interrupted.");
                executor.shutdownNow();
                break;
            }
        }
        System.out.println("____FUCK TIME:" + System.currentTimeMillis());
    }

}