package com.btc.app.service.impl;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import com.btc.app.dao.CoinInfoMapper;
import com.btc.app.dao.CoinMapper;
import com.btc.app.push.xinge.XinGePush;
import com.btc.app.service.CoinService;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.spider.phantomjs.JubiSpider;
import com.btc.app.util.CoinNameMapper;
import com.btc.app.util.MarketTypeMapper;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
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
    private Map<String, Map<String, CoinBean>> mainsitecoinMap;
    private Map<String, CoinInfoBean> coinInfoBeanMap;
    private AtomicInteger coinNumber = new AtomicInteger();
    private XinGePush xgpush = XinGePush.getInstance();
//    private WeiXinPush wxpush = WeiXinPush.getInstance();

    public int insertCoinInfo(CoinInfoBean bean) {
        return this.coinInfoDao.insert(bean);
    }

    public int insertCoin(CoinBean bean) {
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
        loadInfoBeanMap();
        if (coinMarketMap == null) {
            coinMarketMap = new ConcurrentHashMap<String, Map<CoinBean, CoinBean>>();
            logger.info("加载数据库数据中...");
            long now = System.currentTimeMillis();
            List<CoinBean> todayBeans = this.coinDao.getTodayCoinInfo(CoinNameMapper.COIN_MARKET);
            logger.info("数据库总数据："+todayBeans.size());
            for (CoinBean bean : todayBeans) {
                String market = getMarketNameType(bean.getMarket_type());
                String coinid = bean.getCoin_id();
                if (coinInfoBeanMap.containsKey(coinid)) {
                    bean.setInfoBean(coinInfoBeanMap.get(coinid));
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
            String coinid = bean.getCoin_id();
            if (coinInfoBeanMap.containsKey(coinid)) {
                bean.setInfoBean(coinInfoBeanMap.get(coinid));
            }
//            BigDecimal rise;
            BigDecimal percent = bean.getPercent();
            BigDecimal oldpercent = null;
            if (percent == null) continue;
            Map<CoinBean, CoinBean> map;
            if (coinMarketMap.containsKey(market)) {
                map = coinMarketMap.get(market);
                if (coinMarketMap.get(market).containsKey(bean)) {
                    CoinBean oldBean = map.remove(bean);
                    if (oldBean == null) continue;
                    oldpercent = oldBean.getPercent();
                    if (oldpercent != null && percent.subtract(oldpercent).abs().compareTo(new BigDecimal(5)) < 0) {
                        map.put(bean, bean);
                        coinMarketMap.put(market, map);
                        continue;
                    }
//                    logger.info("前端接收Bean：" + bean + "\t原涨跌幅：" + oldpercent);
//                rise = percent.subtract(oldpercent);
                }/* else {
                    logger.info("前端接收新Bean：" + bean + "\t当日首次数据");
                }*/
            } else {
//                rise = percent;
//                logger.info("前端接收新Bean：" + bean + "\t当日首次数据");
                map = new ConcurrentHashMap<CoinBean, CoinBean>();
            }
            //插入数据库并推送到前端
            map.put(bean, bean);
            coinMarketMap.put(market, map);
            insertCoin(bean);

            if (bean.getRank() <= 50 && bean.getMarket_type() == 33) {
                logger.info("Bean：" + bean + "\t原涨跌幅：" + oldpercent);
                xgpush.pushASyncCoinByTag(bean);
            }

            logger.debug("Coin Map Size:" + map.size());
            num++;
        }
        coinNumber.set(num);
    }

    private void loadInfoBeanMap(){
        if (coinInfoBeanMap == null) {
            coinInfoBeanMap = new ConcurrentHashMap<String, CoinInfoBean>();
            List<CoinInfoBean> infoBeans = this.coinInfoDao.getAll();
            for (CoinInfoBean infoBean : infoBeans) {
                coinInfoBeanMap.put(infoBean.getCoinid(), infoBean);
            }
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

    public List<CoinBean> getCoinInfoByIds(Set<String> ids, String symbol) {
        List<CoinBean> coinBeans = getTodayCoinInfo(symbol);
        List<CoinBean> beanList = new ArrayList<CoinBean>();
        for(CoinBean bean:coinBeans){
            String id = bean.getCoin_id();
            if(ids.contains(id)){
                beanList.add(bean);
            }
        }
        return beanList;
    }

    public List<CoinBean> getCoinById(String coinid){
        List<CoinBean> lists = new ArrayList<CoinBean>();
        if(mainsitecoinMap == null){
            //loadMainSiteMap();
            return lists;
        }
        Map<String, CoinBean> map = mainsitecoinMap.get(coinid);
        if(map == null) return lists;
        for(String platform: map.keySet()){
            CoinBean bean = map.get(platform);
            lists.add(bean);
        }
        return lists;
    }

    public List<CoinBean> getCoinByPattern(String pattern, String symbol) {
        List<CoinBean> coinBeans = getTodayCoinInfo(symbol);
        List<CoinBean> beanList = new ArrayList<CoinBean>();
        for(CoinBean bean:coinBeans){
            String id = bean.getCoin_id();
            CoinInfoBean infoBean = bean.getInfoBean();
            if(id.matches(pattern)){
                beanList.add(bean);
                continue;
            }
            if(infoBean == null)continue;
            String englishName = infoBean.getEnglishname();
            String chineseName = infoBean.getChinesename();
            if(englishName.matches(pattern) || chineseName.matches(pattern)){
                beanList.add(bean);
            }
        }
        return beanList;
    }

    public CoinBean getCoinInfoById(String target_id, String symbol) {
        List<CoinBean> coinBeans = getTodayCoinInfo(symbol);
        CoinBean ret_bean = null;
        for(CoinBean bean:coinBeans){
            String id = bean.getCoin_id();
            if(target_id.equals(id)){
                ret_bean = bean;
            }
        }
        return ret_bean;
    }

    public void handleMainSiteCoinBeans(List<CoinBean> coinBeanList) {
//        loadInfoBeanMap();
//        loadMainSiteMap();
        if(mainsitecoinMap == null){
            System.out.println("开始加载币列表");
            mainsitecoinMap = new ConcurrentHashMap<String, Map<String, CoinBean>>();
        }
        for (CoinBean bean : coinBeanList) {
            String platform = bean.getPlatform();
            String coinid = bean.getCoin_id();
            Map<String, CoinBean> map;
            if (mainsitecoinMap.containsKey(coinid)) {
                map = mainsitecoinMap.get(coinid);
                if (map.containsKey(platform)) {
                    map.remove(platform);
                }
            }else{
                map = new ConcurrentHashMap<String, CoinBean>();
            }
            map.put(platform, bean);
            mainsitecoinMap.put(coinid, map);
            //insertCoinInfo(bean);
        }
    }

    private void loadMainSiteMap(){
        if (mainsitecoinMap == null) {
            mainsitecoinMap = new ConcurrentHashMap<String, Map<String, CoinBean>>();
            logger.info("加载MainSite数据库数据中...");
            long now = System.currentTimeMillis();
            for(String siteid: CoinNameMapper.OTHER_PLATFORM_MAP.keySet()) {
                String platform = CoinNameMapper.OTHER_PLATFORM_MAP.get(siteid);
                List<CoinBean> todayBeans = this.coinDao.getTodayCoinInfo(platform);
                logger.info("数据库总数据：" + todayBeans.size());
                for (CoinBean bean : todayBeans) {
                    String coinid = bean.getCoin_id();
                    if (coinid == null) continue;
                    Map<String, CoinBean> map;
                    if (mainsitecoinMap.containsKey(coinid)) {
                        map = mainsitecoinMap.get(coinid);
                    } else {
                        map = new ConcurrentHashMap<String, CoinBean>();
                    }
                    map.put(platform, bean);
                    mainsitecoinMap.put(coinid, map);
                }
            }
            long end = System.currentTimeMillis();
            logger.info("加载完成，用时 "+ (end - now) + " ms");
        }
    }

    public int getCoinInfo() {
        return coinNumber.getAndSet(0);
    }
}
