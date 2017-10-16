package com.btc.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.btc.app.bean.PhoneBean;
import com.btc.app.bean.TokenActionBean;
import com.btc.app.bean.UserBean;
import com.btc.app.push.xinge.XinGePush;
import com.btc.app.service.PhoneService;
import com.btc.app.service.TokenActionService;
import com.btc.app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.WeakHashMap;

@Controller
@RequestMapping("/token")
public class TokenActionController {

    @Resource
    PhoneService phoneService;
    @Resource
    UserService userService;
    @Resource
    TokenActionService tokenActionService;

    private XinGePush push = XinGePush.getInstance();



    /*@RequestMapping(value = "/registertoken", produces = "application/json; charset=utf-8")
    public @ResponseBody String registerToken(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        String token = request.getParameter("token");
        String pid = request.getParameter("pid");
        String account = request.getParameter("account");
        if (token == null || pid == null) {
            json.put("status", "error");
            json.put("message", "parameter needed for this api.");
            json.put("code", -1);
            return json.toJSONString();
        }
        if (token.length() != 64 || pid.length() != 40) {
            json.put("status", "error");
            json.put("message", "parameter illegal.");
            json.put("code", -2);
            return json.toJSONString();
        }
        UserBean userBean = null;
        int uid = 0;
        if(account != null){
            userBean = userService.searchUser(account);
            uid = userBean == null ? 0 :userBean.getId();
        }
        PhoneBean bean = phoneService.searchPhone(pid,uid);
        if (bean == null) {
            bean = new PhoneBean();
            bean.setPid(pid);
            bean.setUid(uid);
            bean.setToken(token);
            bean.setUpdate_time(new Date());
            int ret = phoneService.insert(bean);
            if (ret > 0) {
                json.put("status", "success");
                json.put("message", "register new phone token.");
                json.put("code", 0);
            } else {
                json.put("status", "error");
                json.put("message", "database insert error.");
                json.put("code", ret);
            }
        } else if (!bean.getToken().equalsIgnoreCase(token)) {
            int ret = phoneService.updateToken(token, pid, uid);
            if (ret > 0) {
                bean.setToken(token);
                json.put("status", "success");
                json.put("message", "update new token for phone:" + pid);
                json.put("code", 1);
            } else {
                json.put("status", "error");
                json.put("message", "database update error for phone: " + pid);
                json.put("code", ret);
            }
        } else {
            json.put("status", "success");
            json.put("message", "pid and token not changed.");
            json.put("code", 2);
        }
        return json.toJSONString();
    }*/

    @RequestMapping(value = "/addtag", produces = "application/json; charset=utf-8")
    public @ResponseBody String addTagForToken(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        String token = request.getParameter("token");
        String pid = request.getParameter("pid");
        String account = request.getParameter("account");
        String tagname = request.getParameter("tagname");
        String device = request.getParameter("device");
        if (token == null || tagname == null || pid == null || device == null) {
            json.put("status", "error");
            json.put("message", "illegal parameter for this api.");
            json.put("code", -1);
            return json.toJSONString();
        }
        UserBean userBean;
        int uid = 0;
        if(account != null){
            userBean = userService.searchUser(account);
            uid = userBean == null ? 0 :userBean.getId();
        }
        /*PhoneBean bean = phoneService.searchPhone(pid,uid);
        if(bean == null){
            json.put("status", "error");
            json.put("message", "please register first.");
            json.put("code", -2);
        }*/
        org.json.JSONObject ret_json;
        if(device.equalsIgnoreCase("ios")){
            ret_json = push.batchSetTagsSync(token,tagname, XinGePush.Device.ios);
        } else{
            ret_json = push.batchSetTagsSync(token,tagname, XinGePush.Device.android);
        }
        if(ret_json.getInt("ret_code") != 0){
            return ret_json.toString();
        }
        TokenActionBean actionBean = new TokenActionBean();
        actionBean.setUid(uid);
        actionBean.setToken(token);
        actionBean.setSymbol(tagname);
        actionBean.setActiontime(new Date());
        actionBean.setActiontype(TokenActionBean.ADD_TAG_FOR_TOKEN);
        int ret = tokenActionService.insert(actionBean);
        if(ret < 0){
            json.put("status", "error");
            json.put("message", "insert into database error(but tag set success).");
            json.put("code", -3);
        }else{
            json.put("status", "success");
            json.put("message", "success add tag for token:"+token);
            json.put("code", 1);
        }
        return json.toJSONString();
    }

    @RequestMapping(value = "/deltag", produces = "application/json; charset=utf-8")
    public @ResponseBody String delTagForToken(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        String token = request.getParameter("token");
        String pid = request.getParameter("pid");
        String account = request.getParameter("account");
        String tagname = request.getParameter("tagname");
        String device = request.getParameter("device");
        if (token == null || tagname == null || pid == null || device == null) {
            json.put("status", "error");
            json.put("message", "illegal parameter for this api.");
            json.put("code", -1);
            return json.toJSONString();
        }
        UserBean userBean;
        int uid = 0;
        if(account != null){
            userBean = userService.searchUser(account);
            uid = userBean == null ? 0 :userBean.getId();
        }
        /*PhoneBean bean = phoneService.searchPhone(pid,uid);
        if(bean == null){
            json.put("status", "error");
            json.put("message", "please register first.");
            json.put("code", -2);
        }*/
        org.json.JSONObject ret_json;
        if(device.equalsIgnoreCase("ios")){
            ret_json = push.batchDelTagsSync(token,tagname, XinGePush.Device.ios);
        } else{
            ret_json = push.batchDelTagsSync(token,tagname, XinGePush.Device.android);
        }
        if(ret_json.getInt("ret_code") != 0){
            return ret_json.toString();
        }
        TokenActionBean actionBean = new TokenActionBean();
        actionBean.setUid(uid);
        actionBean.setToken(token);
        actionBean.setSymbol(tagname);
        actionBean.setActiontime(new Date());
        actionBean.setActiontype(TokenActionBean.DEL_TAG_FOR_TOKEN);
        int ret = tokenActionService.insert(actionBean);
        if(ret < 0){
            json.put("status", "error");
            json.put("message", "insert into database error(but tag del success).");
            json.put("code", -3);
        }else{
            json.put("status", "success");
            json.put("message", "success delete tag for token:"+token);
            json.put("code", 1);
        }
        return json.toJSONString();
    }

}
