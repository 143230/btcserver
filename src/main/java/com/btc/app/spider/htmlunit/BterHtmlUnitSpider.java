package com.btc.app.spider.htmlunit;

import com.btc.app.bean.CoinBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.spider.service.BterHtmlUnitSpiderService;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class BterHtmlUnitSpider extends HtmlUnitBasicSpider implements CoinHumlUnitSpider {
    private List<CoinBean> coinBeanList;

    public BterHtmlUnitSpider(String url) throws InterruptedException {
        super(url);
    }

    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        //logger.info("The Page of: "+url+" has start to parse.");
        HtmlTable table = this.page.getFirstByXPath(".//table[@id=\"marketlist\"]");
        if(table == null){
            logger.info(this.page.asXml());
            throw new Exception("The Page of: "+url+" has not Right.");
        }
        coinBeanList = new ArrayList<CoinBean>();
        for(int i=1;i<table.getRowCount();i++){
            HtmlTableRow row = table.getRow(i);
            CoinBean bean = new CoinBean();
            bean.setPlatform("https://bter.com/");
            HtmlTableCell cell = row.getCell(0);
            HtmlAnchor anchor = cell.getFirstByXPath(".//a[@class=\"coin-name\"]");
            for (final DomNode child : anchor.getChildren()) {
                final short childType = child.getNodeType();
                if (childType == Node.TEXT_NODE) {
                    String chinesename = child.getTextContent();
                    bean.setChinesename(chinesename.trim());
                }else if(childType == Node.ELEMENT_NODE){
                    String englishname = child.getTextContent();
                    bean.setEnglishname(englishname.trim());
                }
            }
            HtmlTableCell cell1 = row.getCell(1);
            for (final DomNode child : cell1.getChildren()) {
                final short childType = child.getNodeType();
                if (childType == Node.TEXT_NODE) {
                    String price = child.getTextContent();
                    BigDecimal price_now = strToBigDecimal(price);
                    bean.setPrice(price_now);
                    if(price.startsWith("￥")){
                        bean.setMarket_type(1);
                    }else if(price.startsWith("฿")){
                        bean.setMarket_type(2);
                    }else if(price.startsWith("E")){
                        bean.setMarket_type(3);
                    }else{
                        bean.setMarket_type(-1);
                    }
                }
            }
            HtmlTableCell cell2 = row.getCell(2);
            String volume = cell2.getTextContent();
            BigDecimal turnvolume = strToBigDecimal(volume);
            bean.setTurnvolume(turnvolume);
            HtmlTableCell cell3 = row.getCell(3);
            String value = cell3.getTextContent();
            BigDecimal totalvalue = strToBigDecimal(value);

            HtmlTableCell cell4 = row.getCell(4);
            String rise_str = cell4.getTextContent();
            BigDecimal rise = strToBigDecimal(rise_str);
            bean.setPercent(rise);
            bean.setUpdate_time(new Date());
            coinBeanList.add(bean);
        }
    }
    public static BigDecimal strToBigDecimal(String str){
        str = str.trim();
        if(str.matches("^[￥฿E].*")){
            str = str.substring(1);
        }
        str = str.trim();
        if(str.matches(".*\\/$")){
            str = str.substring(0,str.length()-1);
        }
        str = str.trim();
        if(str.contains(",")){
            str = str.replaceAll(",","");
        }
        str = str.trim();
        if(str.matches(".*%$")){
            str = str.substring(0,str.length()-1);
        }
        str = str.trim();
        if(str.matches("^[+-][+-].*")){
            return BigDecimal.ZERO;
        }
        if(str.matches("^[+-]{0,1}[0-9]{1,}\\.{0,1}[0-9]{1,}")){
            return new BigDecimal(str);
        }
        return null;
    }

    public void downloadFile(WebRequest request, WebResponse response) {
//        System.out.println(request.getAdditionalHeaders());
        String url = request.getUrl().toString();
//        int status_code = response.getStatusCode();
        if(url.equals(this.url)){
//            logger.info("下载文件："+url+"\tStatus_Code: "+status_code);
            setFinished(true);
        }
    }

    public List<CoinBean> getCoinBeanList() {
        return coinBeanList;
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            BterHtmlUnitSpider spider = new BterHtmlUnitSpider(BterHtmlUnitSpiderService.BTER_CNY);
            spider.setJavaScriptEnabled(false);
            spider.openAndWait();
            spider.parseHtml();
            spider.release();
//            System.out.println("finished");

        }
//        Thread.sleep(100000);
    }
}
