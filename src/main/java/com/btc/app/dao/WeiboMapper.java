package com.btc.app.dao;

import com.btc.app.bean.WeiboBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WeiboMapper {
    int insert(WeiboBean weibo);
    WeiboBean testConnect();
    List<WeiboBean> getLatestWeiboInfo(int count);
    WeiboBean isHave(String wbid);
    List<WeiboBean> getWeiboInfo(@Param("start")int start, @Param("count")int count);
};