package com.btc.app.spider.htmlunit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc.app.bean.CoinBean;
import com.btc.app.bean.NewsBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.spider.htmlunit.inter.NewsHtmlUnitSpider;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLLinkElement;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class JubiHtmlUnitSpider extends HtmlUnitBasicSpider implements CoinHumlUnitSpider,NewsHtmlUnitSpider {
    private List<CoinBean> coinBeans;
    private List<NewsBean> newsBeans;
    private JSONObject coinJson;
    private boolean changed;
    private boolean badlink;
    private Map<String,BigDecimal> trendsMap;


    public JubiHtmlUnitSpider(String url) throws InterruptedException {
        super(url);
        this.coinJson = null;
        this.badlink = false;
    }

    public List<CoinBean> getCoinBeanList() {
        return coinBeans;
    }
    public boolean isBadlink(){
        return this.badlink;
    }
    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        //logger.info("The Page of: "+url+" has start to parse.");
        if(!changed)return;
        coinBeans = new ArrayList<CoinBean>();
        Date time = new Date();
        for(String key:coinJson.keySet()){
            if(!trendsMap.containsKey(key)){
                throw new Exception("Trends Map not found.");
            }
            BigDecimal yprice = trendsMap.get(key);
            JSONArray array = coinJson.getJSONArray(key);
            CoinBean bean = new CoinBean();
            bean.setYprice(yprice);
            bean.setPlatform(url);
            String chinesename = array.getString(0);
            bean.setChinesename(chinesename.trim());
            String englishname = key;
            bean.setEnglishname(englishname.trim());
            String price_str = array.getString(1);
            BigDecimal price_now = new BigDecimal(price_str);//BigDecimal 类使用户能完全控制舍入行为
            bean.setPrice(price_now);
            BigDecimal price_buy = array.getBigDecimal(2);
            BigDecimal price_sell= array.getBigDecimal(3);
            BigDecimal price_max = array.getBigDecimal(4);
            BigDecimal price_min = array.getBigDecimal(5);
            BigDecimal rise ;
            if(yprice.compareTo(BigDecimal.ZERO) > 0 && price_buy.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal tmp = price_buy.subtract(yprice);
                tmp = tmp.multiply(new BigDecimal(100));
                rise = tmp.divide(yprice,6,BigDecimal.ROUND_HALF_UP);
            }else{
                rise = BigDecimal.ZERO;
            }
            bean.setPrice(price_buy);
            bean.setPercent(rise);
            BigDecimal turnnumber = array.getBigDecimal(6);
            bean.setTurnnumber(turnnumber);
            BigDecimal turnvolume = array.getBigDecimal(7);
            bean.setTurnvolume(turnvolume);
            bean.setUpdate_time(time);
            bean.setMarket_type(1);//RMB market
            //System.out.println(yprice);
            //System.out.println(array.toJSONString());
            //System.out.println(bean);
            coinBeans.add(bean);
        }
        changed = false;
        if(newsBeans == null){
            newsBeans = new ArrayList<NewsBean>();
            HtmlSpan span = this.page.getFirstByXPath(".//span[@class=\"jubi_news\"]");
            parseAnchorToBean(span);
            span = this.page.getFirstByXPath(".//span[@class=\"btc_news\"]");
            parseAnchorToBean(span);
            span = this.page.getFirstByXPath(".//span[@class=\"shanzhai_news\"]");
            parseAnchorToBean(span);
        }
    }

    private void parseAnchorToBean(HtmlSpan span) throws MalformedURLException {
        List<HtmlAnchor> anchors = span.getByXPath(".//ul[@class=\"pp_list\"]/li/a");
        for(HtmlAnchor anchor:anchors) {
            if (!anchor.getHrefAttribute().endsWith(".html")) continue;
            NewsBean bean = new NewsBean();
            String url = anchor.getTargetUrl(anchor.getHrefAttribute(), this.page).toString();
            bean.setUrl(url);
            String title = null;
            for (final DomNode child : anchor.getChildren()) {
                final short childType = child.getNodeType();
                if (childType == Node.TEXT_NODE) {
                    title = child.getTextContent().trim();
                }
            }
            bean.setTitle(title);
//            System.out.println(bean);
        }
    }

    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        if(url.startsWith("https://www.jubi.com/coin/allcoin?")){
            //logger.info("下载文件："+url+"\tStatus_Code: "+status_code);
            if(status_code != 200){
                this.badlink = true;
                return;
            }
            String content = response.getContentAsString();
            coinJson = JSONObject.parseObject(content);
            this.changed = true;
            synchronized (this) {
                this.notifyAll();
            }
        }else if(url.startsWith("https://www.jubi.com/coin/trends?")){
            //logger.info("下载文件："+url+"\tStatus_Code: "+status_code);
            if(status_code != 200){
                this.badlink = true;
                return;
            }
            String content = response.getContentAsString();
            trendsMap = new HashMap<String, BigDecimal>();
            JSONObject json = JSONObject.parseObject(content);
            for(String key:json.keySet()){
                JSONObject object = json.getJSONObject(key);
                String yprice_str = object.getString("yprice");
                BigDecimal yprice = new BigDecimal(yprice_str);//BigDecimal 类使用户能完全控制舍入行为
                trendsMap.put(key,yprice);
            }
        }
        if(coinJson != null && trendsMap != null){
            setFinished(true);
        }
    }

    public static void main(String[] args) {
        while(true) {
            try {
                JubiHtmlUnitSpider spider = new JubiHtmlUnitSpider("https://www.jubi.com/");
                spider.openAndWait();
                spider.parseHtml();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<NewsBean> getNewsBeanList() {
        return newsBeans;
    }

    public Date getNewCreateTime(NewsBean bean) throws Exception {
        return new Date();
    }
}
