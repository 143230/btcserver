package com.btc.app.service;


import com.btc.app.bean.PhoneBean;

public interface PhoneService {
    PhoneBean testConnect();

    PhoneBean searchPhone(String pid,int uid);

    int insert(PhoneBean bean);

    int updateToken(String token, String pid,int uid);
}
