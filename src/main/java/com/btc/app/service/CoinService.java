package com.btc.app.service;


import com.btc.app.bean.CoinBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.spider.phantomjs.JubiSpider;

import java.util.List;
import java.util.Set;

public interface CoinService {
    int insertCoinInfo(CoinBean bean);

    CoinBean testConnection();

    void handleResult(JubiSpider spider);

    void handleResult(CoinHumlUnitSpider spider);

    List<CoinBean> getLatestCoinInfo(int count);

    List<CoinBean> getTodayCoinInfo(String symbol);
    List<CoinBean> getCoinInfoByRank(String symbol, int start, int count, String desc);
    List<CoinBean> getCoinInfoByPercent(String symbol, int start, int count, String desc);

    List<CoinBean> getCoinInfoByIds(Set<String> ids, String symbol);
    CoinBean getCoinInfoById(String id, String symbol);

    int getCoinInfo();
}
