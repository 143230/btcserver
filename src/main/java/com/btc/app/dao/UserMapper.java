package com.btc.app.dao;

import com.btc.app.bean.UserBean;

public interface UserMapper {
    int insert(UserBean user);
    UserBean searchUser(String phone);
    UserBean testConnect();
}
