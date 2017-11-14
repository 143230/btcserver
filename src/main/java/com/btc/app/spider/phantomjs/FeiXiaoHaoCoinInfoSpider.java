package com.btc.app.spider.phantomjs;

import cn.wanghaomiao.xpath.model.JXDocument;
import cn.wanghaomiao.xpath.model.JXNode;
import com.btc.app.bean.CoinInfoBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class FeiXiaoHaoCoinInfoSpider extends BasicSpider {
    private CoinInfoBean infoBean;
    private String coinid;

    public FeiXiaoHaoCoinInfoSpider(WebDriver driver, String coinid) throws InterruptedException {
        super(driver, String.format("http://www.feixiaohao.com/currencies/%s/#baseinfo", coinid));
        this.coinid = coinid;
    }

    @Override
    public void parseHtml(int time_wait) throws Exception {
        infoBean = new CoinInfoBean();
        String htmlcontent = driver.getPageSource();
        Document document = Jsoup.parse(htmlcontent);
        JXDocument jxDocument = new JXDocument(document);
        List list = jxDocument.sel("//div[@class='num']");

        Object obj = list.get(1);
        String content = this.directContent(obj);
        content = content.trim().split(" ")[0];
        content = content.replaceAll("[a-zA-Z, ]", "");
        System.out.println(content);
        if(content.equals("?")){
            infoBean.setCur_num(-1L);
        }else{
            infoBean.setCur_num(Long.valueOf(content));
        }
        obj = list.get(3);
        content = this.directContent(obj);
        content = content.trim().split(" ")[0];
        content = content.replaceAll("[a-zA-Z, ]", "");
        System.out.println(content);
        if(content.equals("?")){
            infoBean.setTotal_num(-1L);
        }else{
            infoBean.setTotal_num(Long.valueOf(content));
        }

        List<JXNode> nodes = jxDocument.selN("//div[@class='tabBody' and @style='']/table/tbody/tr/td");
        List<JXNode> table = nodes.subList(nodes.size()-6, nodes.size());
        String englishName = table.get(0).getElement().ownText();
        System.out.println(englishName);
        String chineseName = table.get(1).getElement().ownText();
        System.out.println(chineseName);
        String symbol = table.get(2).getElement().ownText();
        String time = table.get(3).getElement().ownText();
        String imageUrl = "https://files.coinmarketcap.com/static/img/coins/128x128/" + coinid + ".png";
        System.out.println(imageUrl);
        Date publish_time;
        try{
            time = time.trim();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            publish_time = sdf.parse(time);
        }catch (Exception e){
            publish_time = null;
        }
        List<JXNode> anchors = table.get(4).sel("//a");
        String websites = this.getWebsites(anchors);
        System.out.println(websites);
        anchors = table.get(5).sel("//a");
        String block_stations = this.getWebsites(anchors);
        System.out.println(block_stations);
        infoBean.setImageurl(imageUrl);
        infoBean.setCoinid(coinid);
        infoBean.setChinesename(chineseName);
        infoBean.setEnglishname(englishName);
        infoBean.setWebsites(websites);
        infoBean.setBlock_stations(block_stations);
        infoBean.setSymbol(symbol);
        infoBean.setPublish_time(publish_time);

        List<JXNode> paragraphs = jxDocument.selN("//section[@class='artBox']/p");
        String description = "";
        for(JXNode paragraph:paragraphs){
            String para_str = paragraph.getElement().text().trim();
            if(para_str.length() <= 0)continue;
            description += paragraph.getElement().text().trim()+"\n";
        }
        System.out.println(description);
        infoBean.setDescription(description);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    public CoinInfoBean getInfoBean() {
        return infoBean;
    }

    private String getWebsites(List<JXNode> anchors){
        String websites = "";
        for(JXNode anchor:anchors){
            String url = anchor.getElement().attr("href");
            websites+= url + "\000";
        }
        websites = websites.trim();
        return websites;
    }

    private String directContent(Object o){
        if(o instanceof Element){
            Element element = (Element)o;
            return element.ownText();
        }
        return o.toString();
    }

    public static void main(String[] args) throws Exception {
        //https://yunbi.com/?warning=false
        //https://www.okcoin.com/
        //https://binance.zendesk.com/hc/en-us
        //https://www.bitfinex.com/
        //https://www.chbtc.com/
        //http://www.feixiaohao.com/all/#CNY
        //https://coinmarketcap.com/currencies/views/all/
        try {
            WebDriver driver = createDriver();
            FeiXiaoHaoCoinInfoSpider spider = new FeiXiaoHaoCoinInfoSpider(driver, "nebulas-token");
            spider.openAndWait();
            spider.parseHtml(10);
            driver.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
