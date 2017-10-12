package com.btc.app.spider.htmlunit;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.util.MarketTypeMapper;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.btc.app.spider.htmlunit.CoinMarketCapHtmlUnitSpider.strToBigDecimal;
import static com.btc.app.spider.service.FeiXiaoHaoHtmlUnitSpiderService.FXH_URL;
import static com.btc.app.util.MarketTypeMapper.getMarketType;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class FeiXiaoHaoHtmlUnitSpider extends HtmlUnitBasicSpider implements CoinHumlUnitSpider {
    private int count = 0;
    private List<CoinBean> coinBeanList;

    public FeiXiaoHaoHtmlUnitSpider(String url) throws InterruptedException {
        super(url);
    }
    public FeiXiaoHaoHtmlUnitSpider(String url, int timewait) throws InterruptedException {
        super(url,timewait);
    }

    @Override
    public void parseHtml() throws Exception {
        if (!this.finished) throw new Exception("The Page of: " + url + " has not load Finished.");
        HtmlTable table = this.page.getFirstByXPath(".//table[@id=\"table\"]");
        coinBeanList = new ArrayList<CoinBean>();
        for(int i=1;i<table.getRowCount();i++){
            HtmlTableRow row = table.getRow(i);
            int rank = Integer.valueOf(row.getCell(0).getTextContent().trim());
            String name = row.getCell(1).getTextContent().trim();
            String chineseName;
            String englishName;
            if(name.contains("-")){
                String[] splits = name.split("-");
                englishName = splits[0];
                chineseName = splits[1];
            }else{
                englishName = name;
                chineseName = name;
            }
            HtmlImage image = row.getFirstByXPath(String.format(".//a/img[@alt=\"%s\"]",name));
            String url = image.getSrcAttribute();

            BigDecimal totalVolume = strToBigDecimal(row.getCell(2).getTextContent());
            BigDecimal price = strToBigDecimal(row.getCell(3).getTextContent());
            BigDecimal turnNum = strToBigDecimal(row.getCell(4).getTextContent());
            BigDecimal turnVolume = strToBigDecimal(row.getCell(5).getTextContent().trim());
            BigDecimal percentday = strToBigDecimal(row.getCell(6).getTextContent());
            CoinBean bean = new CoinBean();
            CoinInfoBean infoBean = new CoinInfoBean();
            infoBean.setImageurl(url);
            infoBean.setSymbol(englishName.toUpperCase());
            infoBean.setEnglishname(englishName);
            infoBean.setChinesename(chineseName);

            bean.setPlatform("TOTAL");
            bean.setPrice(price);
            bean.setPercent(percentday);
            bean.setUpdate_time(new Date());
            bean.setTurnvolume(turnVolume);
            bean.setEnglishname(englishName);
            bean.setChinesename(chineseName);
            bean.setUrl(this.url);
            String market = this.url.split("#")[1];
            bean.setMarket_type(getMarketType(market.toUpperCase()));
            bean.setTurnnumber(turnNum);
            bean.setRank(rank);
            bean.setInfoBean(infoBean);
//            System.out.println(bean);
            coinBeanList.add(bean);
        }
    }


    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        //logger.info(request.getAdditionalHeaders());
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
        FeiXiaoHaoHtmlUnitSpider spider;
        spider = new FeiXiaoHaoHtmlUnitSpider("http://www.feixiaohao.com/list_1.html#CNY",30);
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
