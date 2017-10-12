package com.btc.app.spider.phantomjs;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import cn.wanghaomiao.xpath.model.JXNode;
import com.btc.app.bean.CoinBean;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class JubiSpider extends BasicSpider {
    private List<CoinBean> coinBeans;
    public JubiSpider(WebDriver driver, String url) {
        super(driver, url);
    }

    public void parseHtml(int time_wait) throws XpathSyntaxErrorException,InterruptedException{
        this.coinBeans = new ArrayList<CoinBean>();
        for (int i = 0; i < time_wait; i++) Thread.sleep(1000);
        String doc_str = driver.getPageSource();
        JXDocument document = new JXDocument(doc_str);
        List<JXNode> coin_list = document.selN("//ul[@id='price_today_ul']/li/a/dl");
        Date date = new Date();
        for (JXNode coinele : coin_list) {
            if(!coinele.isText()){
                CoinBean coin = new CoinBean();
                List<JXNode> nameeles = coinele.sel(".//dt/p/text()");
                //System.out.println(nameeles.size());
                List<JXNode> codele = coinele.sel(".//dt/p/b/text()");
                //System.out.println(codele.size());
                //System.out.println(codele.get(0).toString());
                //System.out.println(nameeles.get(0).toString());
                coin.setChinesename(nameeles.get(0).toString().trim());
                coin.setEnglishname(codele.get(0).toString().trim());
                List<JXNode> properties = coinele.sel(".//dd");
                for(int i=0;i<4;i++){
                    JXNode property = properties.get(i);
                    String value = property.getElement().text();
                    switch (i){
                        case 0: coin.setPrice(changeValue(value));break;
                        case 1:coin.setTurnvolume(changeValue(value));break;
                        case 2:coin.setTurnnumber(changeValue(value));break;
                        case 3:coin.setPercent(changeValue(value));break;
                        default:break;
                    }
                }
                coin.setMarket_type(1);
                coin.setUpdate_time(date);
                //System.out.println(coin);
                coinBeans.add(coin);
                //System.out.println("=========================");
            }
        }
        //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
    public static BigDecimal changeValue(String value){
        double number=0;
        double times = 1;
        if(value.startsWith("￥")){
            value = value.substring(1);
        }
        if(value.endsWith("万")){
            value = value.substring(0,value.length()-1);
            times = 10000;
        }else if(value.endsWith("亿")){
            value = value.substring(0,value.length()-1);
            times = 100000000;
        }else if(value.endsWith("%")){
            value = value.substring(0,value.length()-1);
            times = 0.001;
        }
        return new BigDecimal(value).multiply(BigDecimal.valueOf(times));
    }

    public List<CoinBean> getCoinBeans() {
        return coinBeans;
    }

    public static void main(String[] args) {
        try {
            WebDriver driver = createDriver();
            JubiSpider spider = new JubiSpider(driver, "https://www.jubi.com/");
            spider.openAndWait();
            spider.parseHtml(5);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
