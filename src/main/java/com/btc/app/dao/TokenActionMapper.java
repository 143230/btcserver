package com.btc.app.dao;

import com.btc.app.bean.PhoneBean;
import com.btc.app.bean.TokenActionBean;

public interface TokenActionMapper {
    TokenActionBean testConnect();
    int insert(TokenActionBean bean);
}
