package com.btc.app.service;


import com.btc.app.bean.WeiboBean;
import com.btc.app.spider.htmlunit.inter.BlogHtmlUnitSpider;
import com.btc.app.spider.phantomjs.WeiboSpider;

import java.util.List;

public interface WeiboService {
    int insertWeiboInfo(WeiboBean bean);
    WeiboBean testConnection();
    void handleResult(WeiboSpider spider);
    List<WeiboBean> getLatestWeiboInfo(int count);
    List<WeiboBean> getWeiboInfo(int start, int count);
    void handleResult(BlogHtmlUnitSpider spider);
    int getWeiboInfo();
}
