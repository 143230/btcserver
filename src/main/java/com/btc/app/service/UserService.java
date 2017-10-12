package com.btc.app.service;


import com.btc.app.bean.UserBean;

public interface UserService {
    int insertUser(UserBean user);
    UserBean testConnection();
    UserBean searchUser(String phone);
}
