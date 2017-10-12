package com.btc.app.spider.http;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.util.MarketTypeMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.btc.app.util.MarketTypeMapper.getMarketType;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class CoinMarketAPIHttpSpider extends HttpBasicSpider implements CoinHumlUnitSpider {
    private int count = 0;
    private List<CoinBean> coinBeanList;
    private JSONArray array;
    private String convert;

    public CoinMarketAPIHttpSpider(String convert) throws InterruptedException, MalformedURLException {
        super(String.format("https://api.coinmarketcap.com/v1/ticker/?limit=2000&convert=%s", convert));
        this.convert = convert;
    }

    public void parseHtml() throws Exception {
        /**
         {
         "id": "bitcoin",
         "name": "Bitcoin",
         "symbol": "BTC",
         "rank": "1",
         "price_usd": "4319.72",
         "price_btc": "1.0",
         "24h_volume_usd": "1253070000.0",
         "market_cap_usd": "71694660663.0",
         "available_supply": "16597062.0",
         "total_supply": "16597062.0",
         "percent_change_1h": "0.46",
         "percent_change_24h": "1.84",
         "percent_change_7d": "16.04",
         "last_updated": "1506839055"
         },
         */
//        System.out.println(this.url);
        String content = openAndGetContent();
        array = new JSONArray(content);
        coinBeanList = new ArrayList<CoinBean>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
//            System.out.println(object.toString(4));
            String id = object.getString("id");
            String name = object.getString("name");
            String symbol = object.getString("symbol");
            int rank = getJsonInteger(object, "rank");
            BigDecimal price_usd = getJsonBigDecimal(object, "price_usd");
            BigDecimal price_btc = getJsonBigDecimal(object, "price_btc");
            BigDecimal volume_usd_24h = getJsonBigDecimal(object, "24h_volume_usd");
            BigDecimal market_cap_usd = getJsonBigDecimal(object, "market_cap_usd");
            BigDecimal available_supply = getJsonBigDecimal(object, "available_supply");
            BigDecimal total_supply = getJsonBigDecimal(object, "total_supply");
            BigDecimal percent_change_1h = getJsonBigDecimal(object, "percent_change_1h");
            BigDecimal percent_change_24h = getJsonBigDecimal(object, "percent_change_24h");
            BigDecimal percent_change_7d = getJsonBigDecimal(object, "percent_change_7d");
            BigDecimal price = getJsonBigDecimal(object, "price_" + convert.toLowerCase());
            BigDecimal volume_24h = getJsonBigDecimal(object, "24h_volume_" + convert.toLowerCase());
            BigDecimal market_cap = getJsonBigDecimal(object, "market_cap_" + convert.toLowerCase());
            Date date = new Date(getJsonLong(object, "last_updated"));
            String url = "https://coinmarketcap.com/currencies/" + id + "/";
            String imageUrl = "https://files.coinmarketcap.com/static/img/coins/128x128/" + id + ".png";
            CoinBean bean = new CoinBean();
            bean.setPlatform("https://coinmarketcap.com");
            bean.setCoin_id(id);
            bean.setSymbol(symbol);
            bean.setEnglishname(name);
            bean.setUpdate_time(date);
            bean.setPercent(percent_change_24h);
            bean.setPrice(price);
            bean.setChinesename(name);
            bean.setEnglishname(symbol);
            bean.setRank(rank);
            bean.setMarket_type(getMarketType(convert.toUpperCase()));
            bean.setTurnnumber(market_cap);
            bean.setTurnvolume(volume_24h);
            bean.setUrl(url);
//            System.out.println(bean.getUrl());
            CoinInfoBean infoBean = new CoinInfoBean();
            infoBean.setImageurl(imageUrl);
            infoBean.setChinesename(name);
            infoBean.setEnglishname(name);
            infoBean.setSymbol(symbol);
            bean.setInfoBean(infoBean);
            coinBeanList.add(bean);
//            System.out.println(bean);
        }
    }

    private BigDecimal getJsonBigDecimal(JSONObject object, String key) {
        try {
            return object.getBigDecimal(key);
        } catch (JSONException e) {
            return BigDecimal.ZERO;
        }
    }

    private long getJsonLong(JSONObject object, String key) {
        try {
            return object.getLong(key) * 1000;
        } catch (JSONException e) {
            return System.currentTimeMillis();
        }
    }

    private int getJsonInteger(JSONObject object, String key) {
        try {
            return object.getInt(key);
        } catch (JSONException e) {
            return 0;
        }
    }

    public static void main(String[] args) throws Exception {
        //https://yunbi.com/?warning=false
        //https://www.okcoin.com/
        //https://binance.zendesk.com/hc/en-us
        //https://www.bitfinex.com/
        //https://www.chbtc.com/
        //http://www.feixiaohao.com/all/#CNY
        //https://coinmarketcap.com/currencies/views/all/
        CoinMarketAPIHttpSpider spider;
        spider = new CoinMarketAPIHttpSpider("BTC");
        try {
            spider.parseHtml();
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CoinBean> getCoinBeanList() {
        return coinBeanList;
    }
}
