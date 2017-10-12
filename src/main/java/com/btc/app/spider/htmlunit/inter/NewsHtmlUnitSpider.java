package com.btc.app.spider.htmlunit.inter;

import com.btc.app.bean.NewsBean;

import java.util.Date;
import java.util.List;

public interface NewsHtmlUnitSpider {
    public List<NewsBean> getNewsBeanList();
    public Date getNewCreateTime(NewsBean bean) throws Exception;
}
