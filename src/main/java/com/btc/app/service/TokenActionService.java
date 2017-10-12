package com.btc.app.service;

import com.btc.app.bean.TokenActionBean;

public interface TokenActionService {

    TokenActionBean testConnect();

    int insert(TokenActionBean bean);
}
