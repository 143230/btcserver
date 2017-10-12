package com.btc.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.btc.app.bean.UserBean;
import com.btc.app.service.UserService;
import com.btc.app.util.Constant;
import com.btc.app.util.MD5Utils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by cuixuan
 */

@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping("/login")
    public @ResponseBody
    String login(HttpServletRequest request, HttpSession session) {
        JSONObject json = new JSONObject();
        if (session.getAttribute(Constant.CURRENT_USER) != null) {
            json.put("status", "error");
            json.put("message", "you have already login.");
            json.put("code", -1);
            return json.toJSONString();
        }
        String phone = request.getParameter("phone");
        String psw = request.getParameter("password");
        if (phone == null || psw == null || phone.length() != 11 || psw.length() < 6) {
            json.put("status", "error");
            json.put("message", "phone or password error");
            json.put("code", -2);
            return json.toJSONString();
        }
        psw = MD5Utils.MD5Encode(psw, Constant.ENCODE_STRING, true);
        if (phone != null && psw != null) {
            UserBean user = userService.searchUser(phone);
            if (user == null) {
                json.put("status", "error");
                json.put("message", "this number of phone is not registered, please register first.");
                json.put("code", -3);
                return json.toJSONString();
            } else if (!user.getPassword().equals(psw)) {
                json.put("status", "error");
                json.put("message", "password is not right.");
                json.put("code", -4);
                return json.toJSONString();
            }
            json.put("status", "success");
            json.put("code", 0);
            json.put("cookie", session.getId());
            session.setAttribute(Constant.CURRENT_USER, user);
        }
        return json.toJSONString();
    }

    @RequestMapping("/logout")
    public @ResponseBody
    String logout(HttpServletRequest request, HttpSession session, Model model) {
        JSONObject json = new JSONObject();
        if (session.getAttribute(Constant.CURRENT_USER) == null) {
            json.put("status", "error");
            json.put("message", "you have not login, please login first.");
            json.put("code", -1);
        } else {
            session.removeAttribute(Constant.CURRENT_USER);
            json.put("status", "success");
            json.put("code", 0);
        }
        return json.toJSONString();
    }

    @Transactional
    @RequestMapping("/register")
    public @ResponseBody
    String register(HttpServletRequest request, HttpSession session, Model model) {
        JSONObject json = new JSONObject();
        String username = request.getParameter("name");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        if (phone == null || password == null || phone.length() != 11 || password.length() < 6) {
            json.put("status", "error");
            json.put("message", "phone or password error");
            json.put("code", -1);
            return json.toJSONString();
        }
        //判断email是否已经注册
        if (userService.searchUser(phone) != null) {
            json.put("status", "error");
            json.put("message", "the account of this phone already registered, please login directly.");
            json.put("code", -2);
            return json.toJSONString();
        } else {
            UserBean user = new UserBean();
            user.setPhone(phone);
            user.setPassword(MD5Utils.MD5Encode(password, Constant.ENCODE_STRING, true));
            user.setUsername(username);
            user.setAccount_status(0);
            int lines = userService.insertUser(user);
            if (lines <= 0) {
                json.put("status", "error");
                json.put("message", "db has some error, please check it quickly.");
                json.put("code", -3);
                return json.toJSONString();
            }
            json.put("status", "success");
            json.put("code", 0);
            json.put("cookie", session.getId());
            session.setAttribute(Constant.CURRENT_USER, user);
        }
        return json.toJSONString();
    }
}
