package com.btc.app.service.impl;

import com.btc.app.bean.TokenActionBean;
import com.btc.app.dao.TokenActionMapper;
import com.btc.app.service.TokenActionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("tokenActionService")
public class TokenActionImpl implements TokenActionService {
    @Resource
    private TokenActionMapper tokenActionDao;
    public TokenActionBean testConnect() {
        return tokenActionDao.testConnect();
    }

    public int insert(TokenActionBean bean) {
        return tokenActionDao.insert(bean);
    }
}
