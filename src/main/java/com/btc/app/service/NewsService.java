package com.btc.app.service;


import com.btc.app.bean.NewsBean;
import com.btc.app.spider.htmlunit.inter.NewsHtmlUnitSpider;
import com.btc.app.spider.phantomjs.Btc38Spider;

import java.util.List;

public interface NewsService {
    int insertCoinInfo(NewsBean bean);

    NewsBean testConnection();

    void handleResult(Btc38Spider spider);

    List<NewsBean> getLatestNewsInfo(int count);

    NewsBean getOldBean(String url);

    NewsBean isHave(String url);

    void handleResult(NewsHtmlUnitSpider spider);

    List<NewsBean> getNewsInfo(int start, int count);

    int getNewsInfo();
}
