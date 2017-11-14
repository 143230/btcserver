package com.btc.app.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.btc.app.bean.CoinBean;
import com.btc.app.bean.NewsBean;
import com.btc.app.bean.PhoneBean;
import com.btc.app.bean.WeiboBean;
import com.btc.app.service.CoinService;
import com.btc.app.service.NewsService;
import com.btc.app.service.PhoneService;
import com.btc.app.service.WeiboService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.acl.Group;
import java.util.*;

/**
 *
 */

@Controller
@RequestMapping("/")
public class IndexController {
    @Resource
    CoinService coinService;
    @Resource
    NewsService newsService;
    @Resource
    WeiboService weiboService;

    private static final int MAX_RET_COUNT = 100;
    private Logger logger = Logger.getLogger(IndexController.class);

    @RequestMapping("/testnews")
    public String testnews(HttpServletRequest request, Model model) {
        NewsBean bean = newsService.testConnection();
        model.addAttribute("value", JSON.toJSON(bean));
        return "/test";
    }

    @RequestMapping("/testcoin")
    public String testcoin(HttpServletRequest request, Model model) {
        CoinBean bean = coinService.testConnection();
        coinService.insertCoin(bean);
        model.addAttribute("value", JSON.toJSON(bean));
        return "/test";
    }

    @RequestMapping("/siteinfo")
    public @ResponseBody
    String getSiteInfo(HttpServletRequest request) {
        String coinid = request.getParameter("id");
        List<CoinBean> lists = coinService.getCoinById(coinid);
        JsonValueFilter valueFilter = new JsonValueFilter();
        String str = JSON.toJSONString(lists, valueFilter, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
        return str;
    }

    @RequestMapping("/appinfo")
    public @ResponseBody
    String getAppInfo(HttpServletRequest request, Model model) {
        int coin = coinService.getCoinInfo();
        int weibo = weiboService.getWeiboInfo();
        int news = newsService.getNewsInfo();
        JSONObject json = new JSONObject();
        json.put("coin_frequency", coin);
        json.put("news_frequency", news);
        json.put("weibo_frequency", weibo);
        return json.toJSONString();
    }

    @RequestMapping(value = "/coinInfo", produces = "application/json; charset=utf-8")
    public @ResponseBody
    String getCoinInfo(HttpServletRequest request) {
        int start, count;
        List<CoinBean> coinBeanList = null;
        try {
            start = Integer.valueOf(request.getParameter("start"));
            count = Integer.valueOf(request.getParameter("count"));
            String symbol = request.getParameter("symbol");
            String sort = request.getParameter("sort");
            String desc = request.getParameter("desc");
            if (symbol == null) {
                symbol = "USD";//default
            }
            if (sort == null) {
                sort = "volume";
            }
            if (desc == null) {
                desc = "true";
            }
            if (start < 0) {
                start = 0;
            }
            if (count > MAX_RET_COUNT) {
                count = MAX_RET_COUNT;
            }
            if (sort.equalsIgnoreCase("volume")) {
                coinBeanList = coinService.getCoinInfoByRank(symbol.toUpperCase(), start, count, desc);
            } else {
                coinBeanList = coinService.getCoinInfoByPercent(symbol.toUpperCase(), start, count, desc);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
//            coinBeanList = coinService.getCoinInfoByRank("BTC",0,20);
        }
        JsonValueFilter valueFilter = new JsonValueFilter();
        String str = JSON.toJSONString(coinBeanList, valueFilter, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
        return str;
    }

    @RequestMapping(value = "/coinInfo/{symbol}/{coinid}", produces = "application/json; charset=utf-8")
    public @ResponseBody String getSelectCoinInfo(@PathVariable("symbol") String symbol,
                                                  @PathVariable("coinid") String coinid) {
        CoinBean bean = null;
        try {
            if (symbol == null) {
                symbol = "USD";//default
            }
            bean = coinService.getCoinInfoById(coinid, symbol.toUpperCase());
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        JsonValueFilter valueFilter = new JsonValueFilter();
        String str = JSON.toJSONString(bean,valueFilter,  SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
        return str;
    }

    @RequestMapping(value = "/coinInfo/select", produces = "application/json; charset=utf-8")
    public @ResponseBody
    String getSelectCoinInfo(HttpServletRequest request) {
        List<CoinBean> coinBeanList = null;
        try {
            String symbol = request.getParameter("symbol");
            String[] ids = request.getParameterValues("ids");
            if(symbol == null){
                symbol="USD";//default
            }
            Set<String> target_ids = new HashSet<String>();
            Collections.addAll(target_ids, ids);
            coinBeanList = coinService.getCoinInfoByIds(target_ids, symbol.toUpperCase());
        } catch (Exception e) {
            logger.info(e.getMessage());
//            coinBeanList = coinService.getCoinInfoByRank("BTC",0,20);
        }
        JsonValueFilter valueFilter = new JsonValueFilter();
        String str = JSON.toJSONString(coinBeanList, valueFilter,  SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
        return str;
    }

    @RequestMapping(value = "/search", produces = "application/json; charset=utf-8")
    public @ResponseBody
    String getSearchResult(HttpServletRequest request) {
        List<CoinBean> coinBeanList = null;
        try {
            String symbol = request.getParameter("symbol");
            String pattern = request.getParameter("pattern");
            if(symbol == null){
                symbol="USD";//default
            }
            pattern = "^.*" + pattern + ".*$";
            coinBeanList = coinService.getCoinByPattern(pattern, symbol.toUpperCase());
        } catch (Exception e) {
            logger.info(e.getMessage());
//            coinBeanList = coinService.getCoinInfoByRank("BTC",0,20);
        }
        JsonValueFilter valueFilter = new JsonValueFilter();
        String str = JSON.toJSONString(coinBeanList, valueFilter,  SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
        return str;
    }

    @RequestMapping(value = "/newsInfo", produces = "application/json; charset=utf-8")
    public @ResponseBody
    String getNewsInfo(HttpServletRequest request) {
        int start, count;
        List<NewsBean> newsBeanList = null;
        try {
            start = Integer.valueOf(request.getParameter("start"));
            count = Integer.valueOf(request.getParameter("count"));
            if (start < 0) {
                start = 0;
            }
            if (count > MAX_RET_COUNT) {
                count = MAX_RET_COUNT;
            }
            newsBeanList = newsService.getNewsInfo(start, count);
        } catch (Exception e) {
            logger.info(e.getMessage());
            newsBeanList = newsService.getLatestNewsInfo(10);
        }
        String str = JSON.toJSONString(newsBeanList, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
        return str;
    }

    @RequestMapping(value = "/weiboInfo", produces = "application/json; charset=utf-8")
    public @ResponseBody
    String getWeiboInfo(HttpServletRequest request) {
        int start, count;
        List<WeiboBean> weiboBeanList = null;
        try {
            start = Integer.valueOf(request.getParameter("start"));
            count = Integer.valueOf(request.getParameter("count"));
            if (start < 0) {
                start = 0;
            }
            if (count > MAX_RET_COUNT) {
                count = MAX_RET_COUNT;
            }
            weiboBeanList = weiboService.getWeiboInfo(start, count);
        } catch (Exception e) {
            logger.info(e.getMessage());
            weiboBeanList = weiboService.getLatestWeiboInfo(10);
        }
        String str = JSON.toJSONString(weiboBeanList, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
        return str;
    }
}
