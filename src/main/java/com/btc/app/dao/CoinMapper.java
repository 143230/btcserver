package com.btc.app.dao;

import com.btc.app.bean.CoinBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CoinMapper {
    int insert(CoinBean record);
    CoinBean testConnect();
    List<CoinBean> getLatestCoinInfo(int count);
    List<CoinBean> getTodayCoinInfo();
    List<CoinBean> getCoinInfo(@Param("start")int start, @Param("count")int count);
}