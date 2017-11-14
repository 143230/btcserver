package com.btc.app.dao;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CoinInfoMapper {
    int insert(CoinInfoBean record);
    CoinInfoBean testConnect();
    List<CoinInfoBean> getAll();
    CoinInfoBean getCoinInfo(String coinid);
}