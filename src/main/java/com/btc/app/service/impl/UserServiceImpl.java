package com.btc.app.service.impl;

import com.btc.app.bean.UserBean;
import com.btc.app.dao.UserMapper;
import com.btc.app.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.WeakHashMap;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userDao;
    private WeakHashMap<String, UserBean> userMap = new WeakHashMap<String, UserBean>();

    public int insertUser(UserBean user) {
        return userDao.insert(user);
    }

    public UserBean testConnection() {
        return userDao.testConnect();
    }

    public UserBean searchUser(String phone) {
        if(userMap.containsKey(phone)){
            return userMap.get(phone);
        }else{
            UserBean bean = userDao.searchUser(phone);
            if(bean != null){
                userMap.put(phone, bean);
                return bean;
            }
            return null;
        }
    }
}
