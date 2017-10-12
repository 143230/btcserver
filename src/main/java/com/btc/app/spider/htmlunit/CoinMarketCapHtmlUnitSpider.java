package com.btc.app.spider.htmlunit;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.util.MarketTypeMapper;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.btc.app.spider.service.FeiXiaoHaoHtmlUnitSpiderService.FXH_URL;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class CoinMarketCapHtmlUnitSpider extends HtmlUnitBasicSpider implements CoinHumlUnitSpider {
    private int count = 0;
    private List<CoinBean> coinBeanList;

    public CoinMarketCapHtmlUnitSpider(String url) throws InterruptedException {
        super(url);
    }
    public CoinMarketCapHtmlUnitSpider(String url, int timewait) throws InterruptedException {
        super(url,timewait);
    }

    @Override
    public void parseHtml() throws Exception {
        if (!this.finished) throw new Exception("The Page of: " + url + " has not load Finished.");
        coinBeanList = new ArrayList<CoinBean>();
//        List<HtmlAnchor> anchors = this.page.getByXPath(".//a[@class=\"price-toggle\"]");
//        for(HtmlAnchor anchor: anchors) {
//            System.out.println(anchor.getTextContent());
//            anchor.click();
            HtmlTable table = this.page.getFirstByXPath(".//table[@id=\"currencies\"]");
            for (int i = 1; i < table.getRowCount(); i++) {
                HtmlTableRow row = table.getRow(i);
                int rank = Integer.valueOf(row.getCell(0).getTextContent().trim());
                String chineseName = row.getCell(1).getTextContent().trim();
//                String englishName = row.getCell(2).getTextContent().trim();
                HtmlAnchor anchor = row.getCell(1).getFirstByXPath(".//a");
                String url = anchor.getHrefAttribute().trim();//   /currencies/bitcoin/
                String imageurl = "https://files.coinmarketcap.com/static/img/coins/128x128/"+url.substring(12,url.length()-1)+".png";
                //https://files.coinmarketcap.com/static/img/coins/32x32/bitcoin.png

                BigDecimal totalVolume = strToBigDecimal(row.getCell(2).getTextContent());
                BigDecimal price = strToBigDecimal(row.getCell(3).getTextContent());
                BigDecimal totalNum = strToBigDecimal(row.getCell(4).getTextContent().trim());
                String numStr = row.getCell(4).getTextContent().trim();
                StringBuffer buffer= new StringBuffer();

                for(char ch:numStr.toCharArray()){
                    if(ch>='A' && ch<='Z'){
                        buffer.append(ch);
                    }
                }
                String englishName = buffer.toString();
                BigDecimal turnVolume = strToBigDecimal(row.getCell(5).getTextContent());

                BigDecimal percentday = strToBigDecimal(row.getCell(6).getTextContent());
//                System.out.println(imageurl);
                CoinBean bean = new CoinBean();
                CoinInfoBean infoBean = new CoinInfoBean();
                infoBean.setImageurl(imageurl);
                infoBean.setSymbol(englishName.toUpperCase());
                infoBean.setEnglishname(englishName);
                infoBean.setChinesename(chineseName);

                bean.setPlatform("COINMARKET");
                bean.setPrice(price);
                bean.setPercent(percentday);
                bean.setUpdate_time(new Date());
                bean.setTurnvolume(totalVolume);
                bean.setEnglishname(englishName);
                bean.setChinesename(chineseName);
                String current_url = this.page.getUrl().toString();
                bean.setUrl(current_url);
                String market = current_url.split("#")[1];
                bean.setMarket_type(MarketTypeMapper.getMarketType(market));
                bean.setTurnnumber(totalNum);
                bean.setRank(rank);
                bean.setInfoBean(infoBean);
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
        CoinMarketCapHtmlUnitSpider spider;
        spider = new CoinMarketCapHtmlUnitSpider("https://coinmarketcap.com/1#CNY",30);
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
