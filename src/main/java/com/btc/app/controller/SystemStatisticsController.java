package com.btc.app.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.btc.app.bean.CoinBean;
import com.btc.app.bean.NewsBean;
import com.btc.app.bean.PhoneBean;
import com.btc.app.bean.WeiboBean;
import com.btc.app.service.CoinService;
import com.btc.app.service.NewsService;
import com.btc.app.service.PhoneService;
import com.btc.app.service.WeiboService;
import com.btc.app.statistics.SystemStatistics;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */

@Controller
@RequestMapping("/")
public class SystemStatisticsController {
    private static final SystemStatistics statistics = SystemStatistics.getInstance();

    @RequestMapping("/statistics")
    public @ResponseBody String getStat(HttpServletRequest request){
        Map<String,Integer> map = statistics.getAll();
        JSONObject object = new JSONObject();
        for(String key:map.keySet()){
            object.put(key,map.get(key));
        }
        return object.toJSONString();
    }

}
