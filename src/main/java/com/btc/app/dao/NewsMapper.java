package com.btc.app.dao;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.NewsBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewsMapper {
    int insert(NewsBean news);
    NewsBean testConnect();
    List<NewsBean> getLatestNewsInfo(int count);
    NewsBean isHave(String url);
    List<NewsBean> getNewsInfo(@Param("start")int start, @Param("count")int count);
};