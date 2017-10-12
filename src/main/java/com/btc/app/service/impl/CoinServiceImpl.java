package com.btc.app.service.impl;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import com.btc.app.dao.CoinInfoMapper;
import com.btc.app.dao.CoinMapper;
import com.btc.app.push.xinge.XinGePush;
import com.btc.app.service.CoinService;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.spider.phantomjs.JubiSpider;
import com.btc.app.util.MarketTypeMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.btc.app.util.MarketTypeMapper.getMarketNameType;

@Service("coinService")
public class CoinServiceImpl implements CoinService {
    private static final Logger logger = Logger.getLogger(CoinService.class);
    @Resource
    private CoinMapper coinDao;
    @Resource
    private CoinInfoMapper coinInfoDao;
    //    private Map<CoinBean, CoinBean> coinBeanMap;
    private Map<String, Map<CoinBean, CoinBean>> coinMarketMap;
    private Map<String, CoinInfoBean> coinInfoBeanMap;
    private AtomicInteger coinNumber = new AtomicInteger();
    private XinGePush xgpush = XinGePush.getInstance();
//    private WeiXinPush wxpush = WeiXinPush.getInstance();

    public int insertCoinInfo(CoinBean bean) {
        return this.coinDao.insert(bean);
    }

    public CoinBean testConnection() {
        return this.coinDao.testConnect();
    }

    public void handleResult(JubiSpider spider) {
        List<CoinBean> coinBeanList = spider.getCoinBeans();
        handleCoinBeans(coinBeanList);
    }

    public synchronized void handleCoinBeans(List<CoinBean> coinBeanList) {
        int num = 0;
        if (coinInfoBeanMap == null) {
            coinInfoBeanMap = new ConcurrentHashMap<String, CoinInfoBean>();
            List<CoinInfoBean> infoBeans = this.coinInfoDao.getAll();
            for (CoinInfoBean infoBean : infoBeans) {
                coinInfoBeanMap.put(infoBean.getSymbol().toUpperCase(), infoBean);
            }
        }
        if (coinMarketMap == null) {
            coinMarketMap = new ConcurrentHashMap<String, Map<CoinBean, CoinBean>>();
            logger.info("加载数据库数据中...");
            long now = System.currentTimeMillis();
            List<CoinBean> todayBeans = this.coinDao.getTodayCoinInfo();
            logger.info("数据库总数据："+todayBeans.size());
            for (CoinBean bean : todayBeans) {
                String market = getMarketNameType(bean.getMarket_type());
                String symbol = bean.getEnglishname().toUpperCase();
                if (coinInfoBeanMap.containsKey(symbol.toUpperCase())) {
                    bean.setInfoBean(coinInfoBeanMap.get(symbol.toUpperCase()));
                }
                Map<CoinBean, CoinBean> map;
                if (coinMarketMap.containsKey(market)) {
                    map = coinMarketMap.get(market);
                } else {
                    map = new ConcurrentHashMap<CoinBean, CoinBean>();
                }
                map.put(bean, bean);
                coinMarketMap.put(market, map);
            }
            long end = System.currentTimeMillis();
            logger.info("加载完成，用时 "+ (end - now) + " ms");
        }
        for (CoinBean bean : coinBeanList) {
            String market = getMarketNameType(bean.getMarket_type());
            String symbol = bean.getEnglishname().toUpperCase();
//            BigDecimal rise;
            BigDecimal percent = bean.getPercent();
            if (percent == null) continue;
            mergeCoinInfo(bean);
            Map<CoinBean, CoinBean> map;
            if (coinMarketMap.containsKey(market)) {
                map = coinMarketMap.get(market);
                if (coinMarketMap.get(market).containsKey(bean)) {
                    CoinBean oldBean = map.remove(bean);
                    if (oldBean == null) continue;
                    BigDecimal oldpercent = oldBean.getPercent();
                    if (oldpercent != null && percent.subtract(oldpercent).abs().compareTo(new BigDecimal(5)) < 0) {
                        map.put(bean, bean);
                        coinMarketMap.put(market, map);
                        continue;
                    }
                    logger.info("前端接收Bean：" + bean + "\t原涨跌幅：" + oldpercent);
//                rise = percent.subtract(oldpercent);
                } else {
                    logger.info("前端接收新Bean：" + bean + "\t当日首次数据");
                }
            } else {
//                rise = percent;
                logger.info("前端接收新Bean：" + bean + "\t当日首次数据");
                map = new ConcurrentHashMap<CoinBean, CoinBean>();
            }
            //插入数据库并推送到前端
            map.put(bean, bean);
            coinMarketMap.put(market, map);
            insertCoinInfo(bean);

            if (bean.getRank() <= 50 && bean.getMarket_type() == 32) {
                xgpush.pushASyncCoinToAll(bean);
//              wxpush.pushASyncCoinToAll(bean);
            }

            logger.debug("Coin Map Size:" + map.size());
            num++;
        }
        coinNumber.set(num);
    }

    private void mergeCoinInfo(CoinBean newBean) {
        String symbol = newBean.getEnglishname().toUpperCase();
        CoinInfoBean oldInfoBean = coinInfoBeanMap.get(symbol);
        CoinInfoBean newInfoBean = newBean.getInfoBean();
        if (oldInfoBean == null) {
            if (newInfoBean != null && newInfoBean.getSymbol() != null
                    && newInfoBean.getSymbol().length() > 0) {
                coinInfoDao.insert(newInfoBean);
                coinInfoBeanMap.put(symbol, newInfoBean);
            }
            return;
        }
        if (newInfoBean == null || newInfoBean.getSymbol() == null
                || newInfoBean.getSymbol().length() == 0) return;
        boolean isUpdated = false;
        if (oldInfoBean.getChinesename() == null && newInfoBean.getChinesename() != null) {
            oldInfoBean.setChinesename(newInfoBean.getChinesename());
            isUpdated = true;
        }
        if (oldInfoBean.getEnglishname() == null && newInfoBean.getEnglishname() != null) {
            oldInfoBean.setEnglishname(newInfoBean.getEnglishname());
            isUpdated = true;
        }
        if (oldInfoBean.getImageurl() == null && newInfoBean.getImageurl() != null) {
            if (oldInfoBean.getImageurl().startsWith("http://static.feixiaohao.com/")
                    && newInfoBean.getImageurl().startsWith("https://files.coinmarketcap.com/")) {
                oldInfoBean.setImageurl(newInfoBean.getImageurl());
                isUpdated = true;
            }
        }
        if (isUpdated) {
            newBean.setInfoBean(oldInfoBean);
            coinInfoDao.update(oldInfoBean);
        }
    }

    public void handleResult(CoinHumlUnitSpider spider) {
        List<CoinBean> coinBeanList = spider.getCoinBeanList();
        this.handleCoinBeans(coinBeanList);
        //logger.info("Finished Handle Result: "+spider);
    }

    public List<CoinBean> getLatestCoinInfo(int count) {
        return this.coinDao.getLatestCoinInfo(count);
    }

    public List<CoinBean> getTodayCoinInfo(String symbol) {
        if (coinMarketMap == null || !coinMarketMap.containsKey(symbol.toUpperCase())) return Collections.EMPTY_LIST;
        final Map<CoinBean, CoinBean> todayMap = coinMarketMap.get(symbol.toUpperCase());
        List<CoinBean> todayBeans = new ArrayList<CoinBean>();
        for (CoinBean bean : todayMap.keySet()) {
            if (bean.getRank() <= 0) continue;//过滤掉除了非小号和coinmarket之外的信息
            todayBeans.add(bean);
        }
        return todayBeans;
    }

    public List<CoinBean> getCoinInfoByRank(String symbol, int start, int count, final String desc) {
        List<CoinBean> coinBeans = getTodayCoinInfo(symbol);
        Collections.sort(coinBeans, new Comparator<CoinBean>() {
            public int compare(CoinBean o1, CoinBean o2) {
                if (desc.equalsIgnoreCase("true")) {
                    return Integer.valueOf(o1.getRank()).compareTo(o2.getRank());
                } else {
                    return Integer.valueOf(o2.getRank()).compareTo(o1.getRank());
                }
            }
        });
        if (start > coinBeans.size()) {
            return Collections.emptyList();
        }
        if (count + start > coinBeans.size()) {
            return coinBeans.subList(start, coinBeans.size());
        }
        return coinBeans.subList(start, count + start);
    }

    public List<CoinBean> getCoinInfoByPercent(String symbol, int start, int count, final String desc) {
        List<CoinBean> coinBeans = getTodayCoinInfo(symbol);
        Collections.sort(coinBeans, new Comparator<CoinBean>() {
            public int compare(CoinBean o1, CoinBean o2) {
                BigDecimal percent1 = o1.getPercent();
                BigDecimal percent2 = o2.getPercent();
                if (desc.equalsIgnoreCase("true")) {
                    return percent1.compareTo(percent2);
                } else {
                    return percent2.compareTo(percent1);
                }
            }
        });
        if (start > coinBeans.size()) {
            return Collections.emptyList();
        }
        if (count + start > coinBeans.size()) {
            return coinBeans.subList(start, coinBeans.size());
        }
        return coinBeans.subList(start, count + start);
    }

    public int getCoinInfo() {
        return coinNumber.getAndSet(0);
    }
}
