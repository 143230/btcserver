package com.btc.app.service.impl;

import com.btc.app.bean.NewsBean;
import com.btc.app.dao.NewsMapper;
import com.btc.app.push.weixin.WeiXinPush;
import com.btc.app.push.xinge.XinGePush;
import com.btc.app.service.NewsService;
import com.btc.app.spider.htmlunit.inter.NewsHtmlUnitSpider;
import com.btc.app.spider.phantomjs.Btc38Spider;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service("newsService")
public class NewsServiceImpl implements NewsService {
    private static final Logger logger = Logger.getLogger(NewsService.class);
    @Resource
    private NewsMapper newsDao;
    private Map<String, NewsBean> newsBeanMap = new HashMap<String, NewsBean>();
    private AtomicInteger newsNumber = new AtomicInteger();
    private XinGePush xgpush = XinGePush.getInstance();

    public int insertCoinInfo(NewsBean bean) {
        return newsDao.insert(bean);
    }

    public NewsBean testConnection() {
        return newsDao.testConnect();
    }

    public void handleResult(Btc38Spider spider) {
        List<NewsBean> newsBeans = spider.getNewsBeans();
        handleNewsBean(newsBeans);
    }

    public List<NewsBean> getLatestNewsInfo(int count) {
        return newsDao.getLatestNewsInfo(count);
    }

    public NewsBean getOldBean(String url) {
        if (newsBeanMap.containsKey(url)) return newsBeanMap.get(url);
        return newsDao.isHave(url);
    }

    public NewsBean isHave(String url) {
        return newsDao.isHave(url);
    }

    public void handleNewsBean(List<NewsBean> newsBeans) {
        Date date = new Date();
        for (NewsBean bean : newsBeans) {
            String url = bean.getUrl();
            NewsBean oldbean = this.getOldBean(url);
            if (oldbean == null) {
                bean.setUpdate_time(date);
                logger.info("新闻： " + bean);
                newsDao.insert(bean);
                newsBeanMap.put(bean.getUrl(), bean);
            }
        }
    }

    public void handleResult(NewsHtmlUnitSpider spider) {
        List<NewsBean> newsBeans = spider.getNewsBeanList();
        Date date;
        int num = 0;
        for (NewsBean bean : newsBeans) {
            String url = bean.getUrl();
            NewsBean oldbean = this.getOldBean(url);
            if (oldbean == null) {
                try {
                    date = spider.getNewCreateTime(bean);
                } catch (Exception e) {
                    date = new Date();
                }
                bean.setUpdate_time(date);
                //插入数据库并推送到前端
                logger.info("新闻： " + bean);
                num++;
                newsDao.insert(bean);
                xgpush.pushASyncNewsToAll(bean);
                newsBeanMap.put(bean.getUrl(), bean);
            }
        }
        newsNumber.set(num);
    }

    public List<NewsBean> getNewsInfo(int start, int count) {
        return newsDao.getNewsInfo(start, count);
    }

    public int getNewsInfo() {
        return newsNumber.getAndSet(0);
    }
}
