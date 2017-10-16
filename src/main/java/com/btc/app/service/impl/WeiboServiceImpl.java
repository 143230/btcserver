package com.btc.app.service.impl;

import com.btc.app.bean.WeiboBean;
import com.btc.app.dao.WeiboMapper;
import com.btc.app.push.weixin.WeiXinPush;
import com.btc.app.push.xinge.XinGePush;
import com.btc.app.service.WeiboService;
import com.btc.app.spider.htmlunit.inter.BlogHtmlUnitSpider;
import com.btc.app.spider.phantomjs.WeiboSpider;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service("weiboService")
public class WeiboServiceImpl implements WeiboService {
    private static final Logger logger = Logger.getLogger(WeiboService.class);
    @Resource
    private WeiboMapper weiboDao;
    private Map<String,WeiboBean> weiboBeanMap = new WeakHashMap<String, WeiboBean>();
    private AtomicInteger weiboNumber = new AtomicInteger();
    private XinGePush xgpush = XinGePush.getInstance();

    public int insertWeiboInfo(WeiboBean bean) {
        return weiboDao.insert(bean);
    }

    public WeiboBean testConnection() {
        return weiboDao.testConnect();
    }

    public void handleResult(WeiboSpider spider) {
        WeiboBean bean = spider.getWeiboBean();
        handleWeiboResult(bean);
    }

    public List<WeiboBean> getLatestWeiboInfo(int count) {
        return weiboDao.getLatestWeiboInfo(count);
    }

    public List<WeiboBean> getWeiboInfo(int start, int count) {
        return weiboDao.getWeiboInfo(start, count);
    }

    public boolean handleWeiboResult(WeiboBean bean){
            if(bean == null)return false;
            String wbid = bean.getWbid();
            if(!weiboBeanMap.containsKey(wbid)){
                WeiboBean weiboBean = weiboDao.isHave(bean.getWbid());
                if(weiboBean != null)weiboBeanMap.put(wbid,weiboBean);
            }
            if(!weiboBeanMap.containsKey(wbid)){
                logger.info("新发表微博：" + bean.toString());
                //插入数据库并推送
                weiboDao.insert(bean);
                xgpush.pushAsyncWeiboToAll(bean);
                return true;
            }
            return false;
    }
    public void handleResult(BlogHtmlUnitSpider spider) {
        List<WeiboBean> weiboBeanList = spider.getWeiboBeanList();
        int num = 0;
        for(WeiboBean bean:weiboBeanList){
            if(handleWeiboResult(bean)){
                num++;
            }
        }
        weiboNumber.set(num);
    }

    public int getWeiboInfo() {
        return weiboNumber.getAndSet(0);
    }
}
