package com.btc.app.service.impl;

import com.btc.app.bean.PhoneBean;
import com.btc.app.dao.PhoneMapper;
import com.btc.app.service.CoinService;
import com.btc.app.service.PhoneService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.WeakHashMap;

@Service("phoneService")
public class PhoneServiceImpl implements PhoneService {
    private static final Logger logger = Logger.getLogger(CoinService.class);
    @Resource
    private PhoneMapper phoneDao;
    private WeakHashMap<String, PhoneBean> uuidTokenMap = new WeakHashMap<String, PhoneBean>();

    public PhoneBean testConnect() {
        return phoneDao.testConnect();
    }

    public PhoneBean searchPhone(String pid, int uid) {
        if (pid == null) return null;
        String key = pid + "|" + uid;
        if (uuidTokenMap.containsKey(key)) {
            return uuidTokenMap.get(key);
        }
        PhoneBean bean = phoneDao.searchPhone(pid, uid);
        uuidTokenMap.put(key, bean);
        return bean;
    }


    public int insert(PhoneBean bean) {
        if (bean.getPid() == null) return -1;
        String key = bean.getPid() + "|" + bean.getUid();
        if (!uuidTokenMap.containsKey(key)){
            uuidTokenMap.put(key,bean);
        }
        return phoneDao.insert(bean);
    }

    public int updateToken(String token, String pid, int uid) {
        if(token == null || pid == null)return -1;
        String key = pid + "|" + uid;
        PhoneBean bean;
        if (uuidTokenMap.containsKey(key)) {
            bean = uuidTokenMap.get(key);
        }else {
            bean = phoneDao.searchPhone(pid, uid);
        }
        if(bean == null)return -2;
        bean.setToken(token);
        uuidTokenMap.put(key,bean);
        return phoneDao.updateToken(token, pid, uid);
    }
}
