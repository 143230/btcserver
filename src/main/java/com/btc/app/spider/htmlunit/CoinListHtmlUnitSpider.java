package com.btc.app.spider.htmlunit;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.util.CoinNameMapper;
import com.btc.app.util.MarketTypeMapper;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class CoinListHtmlUnitSpider extends HtmlUnitBasicSpider implements CoinHumlUnitSpider {
    private int count = 0;
    private List<CoinBean> coinBeanList;
    private String siteid;

    public CoinListHtmlUnitSpider(String siteid) throws InterruptedException {
        super(String.format("https://coinmarketcap.com/exchanges/%s/", siteid));
        this.siteid = siteid;
    }

    @Override
    public void parseHtml() throws Exception {
        if (!this.finished) throw new Exception("The Page of: " + url + " has not load Finished.");
        coinBeanList = new ArrayList<CoinBean>();
//        List<HtmlAnchor> anchors = this.page.getByXPath(".//a[@class=\"price-toggle\"]");
//        for(HtmlAnchor anchor: anchors) {
//            System.out.println(anchor.getTextContent());
//            anchor.click();
            HtmlTable table = this.page.getFirstByXPath(".//div[@id=\"markets\"]//table");
            for (int i = 1; i < table.getRowCount(); i++) {
                HtmlTableRow row = table.getRow(i);
                int rank = Integer.valueOf(row.getCell(0).getTextContent().trim());
                String englishName = row.getCell(1).getTextContent().trim();
                HtmlAnchor anchor = row.getCell(1).getFirstByXPath(".//a");
                String url = anchor.getHrefAttribute().trim();//   /currencies/bitcoin/
                String coinid = url.substring(12,url.length()-1);

                String coinPair = row.getCell(2).getTextContent().trim();
                BigDecimal volume = strToBigDecimal(row.getCell(3).getTextContent());
                BigDecimal price = strToBigDecimal(row.getCell(4).getTextContent().trim());
                BigDecimal percent = strToBigDecimal(row.getCell(5).getTextContent().trim());
                CoinBean bean = new CoinBean();
                bean.setCoin_id(coinid);
                bean.setPlatform(this.url);
                bean.setPrice(price);
                bean.setPercent(percent);
                bean.setUpdate_time(new Date());
                bean.setTurnvolume(volume);
                bean.setEnglishname(englishName);
                String current_url = this.page.getUrl().toString();
                bean.setUrl(current_url);
                String[] marketPair = coinPair.split("/");
                int mfrom = MarketTypeMapper.getMarketType(marketPair[0]);
                int mto = MarketTypeMapper.getMarketType(marketPair[1]);
                bean.setMarket_type(mfrom << 6 + mto);
                bean.setRank(rank);
                System.out.println(bean);
                coinBeanList.add(bean);
            }
//        }
    }

    public static BigDecimal strToBigDecimal(String str){
        str = str.trim();
        int tag = 0;
        BigDecimal multi = BigDecimal.ONE;
        StringBuffer buffer = new StringBuffer();
        BigDecimal negative = BigDecimal.ONE;
        for(int i=0;i<str.length();i++){
            char ch = str.charAt(i);
            if(ch>='0' && ch<='9'){
                buffer.append(ch);
            }else if(ch == '.' && buffer.length() > 0 && tag == 0){
                buffer.append(ch);
                tag = 1;
            }else if(ch == '亿'){
                multi = new BigDecimal(100000000);
            }else if(ch == '万'){
                multi = new BigDecimal(10000);
            }else if(ch == '-'){
                negative = new BigDecimal(-1);
            }
        }
        if(buffer.length() <= 0){
            return BigDecimal.ZERO;
        }
//        System.out.println(buffer);
        return new BigDecimal(buffer.toString()).multiply(multi.multiply(negative));
    }


    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        //logger.info(request.getAdditionalHeaders().);
//        logger.info(response.getContentType());
        if (url.equals(this.url)) {
            logger.info("下载文件：" + url + "\tStatus_Code: " + status_code + "\tCount:" + count++);
            setFinished(true);
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
        CoinListHtmlUnitSpider spider;
        spider = new CoinListHtmlUnitSpider("bitfinex");
        try {
            spider.setJavaScriptEnabled(false);
            //ProxyBean bean = new ProxyBean("116.196.94.105",1080, Proxy.Type.SOCKS,"");
            //spider.setProxy(bean);
            spider.openAndWait();
            spider.parseHtml();
            Thread.sleep(2000000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CoinBean> getCoinBeanList() {
        return coinBeanList;
    }
}
