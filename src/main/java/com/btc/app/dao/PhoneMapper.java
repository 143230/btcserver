package com.btc.app.dao;

import com.btc.app.bean.PhoneBean;
import org.apache.ibatis.annotations.Param;

public interface PhoneMapper {
    PhoneBean testConnect();
    PhoneBean searchPhone(@Param("pid")String pid,@Param("uid")int uid);
    int insert(PhoneBean bean);
    int updateToken( @Param("token")String token, @Param("pid")String pid,@Param("uid")int uid);
}
