package com.btc.app.spider.htmlunit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc.app.bean.CoinBean;
import com.btc.app.bean.NewsBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.spider.htmlunit.inter.NewsHtmlUnitSpider;
import com.btc.app.util.CoinNameMapper;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class OkCoinHtmlUnitSpider extends HtmlUnitBasicSpider implements CoinHumlUnitSpider,NewsHtmlUnitSpider {
    private List<CoinBean> coinBeans;
    private List<NewsBean> newsBeans;
    private JSONObject coinJson;

    public OkCoinHtmlUnitSpider(String url) throws InterruptedException {
        super(url);
    }

    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");

        if(coinJson == null)throw new Exception("The Page of: "+url+" has Changed.");
        //logger.info("The Page of: "+url+" has start to parse.");
        int code = coinJson.getInteger("code");
        if(code != 0)throw new Exception("The Page of: "+url+" has Changed.");
        JSONArray array = coinJson.getJSONArray("data");
        coinBeans = new ArrayList<CoinBean>();
        for(int i=0;i<array.size();i++){
            JSONObject object = array.getJSONObject(i);
            String symbol = object.getString("symbol");
            CoinBean bean = new CoinBean();
            String[] splits = symbol.split("_");
            if(splits.length!=2)continue;
            String engName = splits[0];
            String mtype = splits[1];
            bean.setEnglishname(engName);
            bean.setChinesename(CoinNameMapper.getChineseName(engName));
            if (mtype.equalsIgnoreCase("usd")){
                bean.setMarket_type(4);
            }else if(mtype.equalsIgnoreCase("cny")){
                bean.setMarket_type(1);
            }
            bean.setPlatform(this.url);
            BigDecimal price = object.getBigDecimal("last");
            bean.setPrice(price);
            BigDecimal percent = BterHtmlUnitSpider.strToBigDecimal(object.getString("changePercentage"));
            bean.setPercent(percent);
            BigDecimal vol = object.getBigDecimal("volume");
            bean.setTurnvolume(vol);
            bean.setUpdate_time(new Date());
            System.out.println(bean);
            coinBeans.add(bean);
        }
        HtmlAnchor anchor = this.page.getFirstByXPath(".//a[@class=\"newsContent\"]");
        if(anchor == null)return;
        String url = anchor.getHrefAttribute();
        String title = anchor.getTextContent();
        NewsBean bean = new NewsBean();
        bean.setUrl(url);
        bean.setTitle(title.trim());
        bean.setNew_type(1);
        bean.setUpdate_time(getNewCreateTime(bean));
//        System.out.println(bean);
        newsBeans = new ArrayList<NewsBean>();
        newsBeans.add(bean);
    }

    public void downloadFile(WebRequest request, WebResponse response) {
//      System.out.println(request.getAdditionalHeaders());
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        if(url.endsWith("v2/markets/market-tickers")){
//            logger.info("下载文件："+url+"\tStatus_Code: "+status_code);
            coinJson = JSONObject.parseObject(response.getContentAsString());
            setFinished(true);
        }
    }

    public List<CoinBean> getCoinBeanList() {
        return coinBeans;
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            OkCoinHtmlUnitSpider spider = new OkCoinHtmlUnitSpider("https://www.okcoin.cn/");
            //spider.setJavaScriptEnabled(false);
            spider.openAndWait();
            spider.parseHtml();
            spider.release();
        }
//        Thread.sleep(100000);
    }

    public List<NewsBean> getNewsBeanList() {
        return newsBeans;
    }

    public Date getNewCreateTime(NewsBean bean) throws Exception {
        String new_url = bean.getUrl();
        CommonNewsHtmlUnitSpider spider = new CommonNewsHtmlUnitSpider(new_url,"span",
                "class","time","yyyy-MM-dd HH:mm");
        spider.setJavaScriptEnabled(false);
        spider.openAndWait();
        spider.parseHtml();
        spider.release();
        return spider.getNew_time();
    }
}
