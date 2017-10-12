package com.btc.app.spider.htmlunit;

import com.btc.app.bean.CoinBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.util.MarketTypeMapper;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.dom.Node;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.btc.app.spider.service.BitFinexHtmlUnitSpiderService.BITFINEX_URL;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class BitFinexHtmlUnitSpider extends HtmlUnitBasicSpider implements CoinHumlUnitSpider {
    private int count = 0;
    private List<CoinBean> coinBeanList;

    public BitFinexHtmlUnitSpider(String url) throws InterruptedException {
        super(url);
    }
    public BitFinexHtmlUnitSpider(String url, int timewait) throws InterruptedException {
        super(url,timewait);
    }

    @Override
    public void parseHtml() throws Exception {
        if (!this.finished) throw new Exception("The Page of: " + url + " has not load Finished.");
        HtmlTable table = this.page.getFirstByXPath(".//table[@class=\"compact striped\"]");
        for(int i=1;i<table.getRowCount();i++){
            HtmlTableRow row = table.getRow(i);
            String currencyPair = row.getCell(0).getTextContent().trim();
            BigDecimal pairce = strToBigDecimal(row.getCell(1).getTextContent().trim());
            BigDecimal yprice = strToBigDecimal(row.getCell(2).getTextContent().trim());
            String[] pairs = currencyPair.split("/");
            String englishname = pairs[0];
            int markettype = MarketTypeMapper.getMarketType(pairs[1]);
            HtmlTableCell cell = row.getCell(4);
            HtmlSpan span = cell.getFirstByXPath(".//span");
            BigDecimal percent =null;
            int index = 0;
            for (final DomNode child : span.getChildren()) {
                final short childType = child.getNodeType();
                if (childType == Node.TEXT_NODE) {
                    index++;
                    if(index == 2){
                        percent = strToBigDecimal(child.getTextContent().trim());
                    }
                }
            }
            CoinBean bean = new CoinBean();
            bean.setEnglishname(englishname);
            bean.setChinesename(englishname);
            bean.setMarket_type(markettype);
            bean.setPrice(pairce);
            bean.setYprice(yprice);
            bean.setPercent(percent);
            bean.setUpdate_time(new Date());
            bean.setPlatform("https://www.bitfinex.com/");
            System.out.println(bean);
        }
    }

    public static BigDecimal strToBigDecimal(String str){
        str = str.trim();
        int tag = 0;
        BigDecimal multi = BigDecimal.ONE;
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<str.length();i++){
            char ch = str.charAt(i);
            if(ch>='0' && ch<='9'){
                buffer.append(ch);
            }else if(ch == '.'){
                buffer.append(ch);
            }else if(ch == '亿'){
                multi = new BigDecimal(100000000);
            }else if(ch == '万'){
                multi = new BigDecimal(10000);
            }else if(ch == '%'){
                multi = new BigDecimal(0.01);
            }
        }
        if(buffer.length() <= 0){
            return BigDecimal.ZERO;
        }
//        System.out.println(buffer);
        return new BigDecimal(buffer.toString()).multiply(multi);
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
        BitFinexHtmlUnitSpider spider;
        spider = new BitFinexHtmlUnitSpider(BITFINEX_URL,30);
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
