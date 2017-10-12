package com.btc.app.spider.htmlunit;

import com.btc.app.bean.ProxyBean;
import com.btc.app.bean.WeiboBean;
import com.btc.app.spider.htmlunit.inter.BlogHtmlUnitSpider;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.dom.Node;

import java.net.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class TwitterHtmlUnitSpider extends HtmlUnitBasicSpider implements BlogHtmlUnitSpider{
    private List<WeiboBean> twitterBeanList;
    public TwitterHtmlUnitSpider(String url) throws InterruptedException {
        super(url);
    }

    @Override
    public void parseHtml() throws Exception {
        if (!this.finished) throw new Exception("The Page of: " + url + " has not load Finished.");
        //System.out.println(this.page.asXml());
        //HtmlAnchor anchor = this.page.getFirstByXPath(".//a[@class=\"news_info_more\"]");
        //anchor.click();
        /*List<HtmlDivision> elementList = this.page.getByXPath("./ol[@id=\"stream-items-id\"]" +
                "/li[@class=\"js-stream-item stream-item stream-item\"]" +
                "/div[starts-with(@class,\"tweet\")]");
        System.out.println(this.page.asXml());
        for(HtmlDivision element:elementList){
            String wbid = element.getAttribute("data-permalink-path");
            String wbname = element.getAttribute("data-name");
            String from_device = null;
            String uid = element.getAttribute("data-user-id");
            HtmlParagraph paragraph = element.getFirstByXPath(".//div[@class=\"content\"]/div[@class=\"js-tweet-text-container\"]/" +
                    "p[contains(@class,\"tweet-text\")]");
            String text = paragraph.asText();
            HtmlAnchor anchor = element.getFirstByXPath(".//div[@class=\"content\"]/div[@class=\"time\"]/" +
                    "a/[contains(@class,\"tweet-timestamp\")]");
            String update_time = anchor.getAttribute("data-original-title");
            System.out.println(wbid);
        }*/
        //System.out.println(this.page.asXml());
        twitterBeanList = new ArrayList<WeiboBean>();
        List<HtmlTable> tables = this.page.getByXPath(".//table[starts-with(@class,\"tweet\")]");
        for(HtmlTable table:tables){
            WeiboBean bean = new WeiboBean();
            String wbid = table.getAttribute("href");
            bean.setWbid(wbid);
            int start_index = 0;
            boolean reblog_flag = false;
            String retwitter_name = null;
            if(table.getRowCount() == 4){
                start_index = 1;
                reblog_flag = true;
                HtmlTableRow row = table.getRow(0);
                HtmlSpan span = row.getFirstByXPath(".//span[@class=\"context\"]");
                retwitter_name = span.getTextContent();
                if(retwitter_name.endsWith(" retweeted")){
                    retwitter_name = retwitter_name.substring(0,retwitter_name.length()-10);
                }
            }
            HtmlTableRow row = table.getRow(start_index);
            HtmlTableCell cell1 = row.getCell(0);
            HtmlImage image = (HtmlImage) cell1.getFirstByXPath(".//img");
            String upicture = image.getSrcAttribute();
            bean.setImageurl(upicture);
            HtmlTableCell cell2 = row.getCell(1);
            HtmlStrong strong = cell2.getFirstByXPath(".//strong[@class=\"fullname\"]");
            String wbname = strong.getTextContent();
            if(reblog_flag){
                bean.setWbname(wbname+" Retweeted By "+retwitter_name);
            }else{
                bean.setWbname(wbname);
            }
            HtmlDivision username = cell2.getFirstByXPath(".//div[@class=\"username\"]");
            String uid = "";
            for (final DomNode child : username.getChildren()) {
                final short childType = child.getNodeType();
                if (childType == Node.TEXT_NODE){
                    uid = child.getTextContent().trim();
                }
            }
            bean.setUid(uid);
            HtmlTableCell cell3 = row.getCell(2);
            HtmlAnchor anchor = cell3.getFirstByXPath(".//a");
            String time = anchor.getTextContent();
            bean.setUpdate_time(changeTime(time));
            //System.out.println(table.getRowCount());

            HtmlTableRow row1 = table.getRow(start_index+1);
            HtmlTableCell cell = row1.getCell(0);
            HtmlDivision division = cell.getFirstByXPath(".//div[contains(@class,\"tweet-reply-context\")]");
            String text = "";
            String rawText = "";
            if(division != null){
                for (final DomNode child : division.getChildren()) {
                    final short childType = child.getNodeType();
                    if (childType == Node.TEXT_NODE){
                        text += child.asXml().trim();
                        rawText += child.getTextContent().trim()+" ";
                    }else if(childType == Node.ELEMENT_NODE){
                        text += child.asXml().trim();
                        rawText += child.getTextContent().trim()+" ";
                    }
                }
                text+="\n";
                rawText+="\n";
            }
            division = cell.getFirstByXPath(".//div[@class=\"tweet-text\"]/div[@class=\"dir-ltr\"]");
            for (final DomNode child : division.getChildren()) {
                final short childType = child.getNodeType();
                if (childType == Node.TEXT_NODE || childType == Node.ELEMENT_NODE) {
                    text += child.asXml().trim();
                    rawText += child.getTextContent().trim();
                }
            }
            bean.setText(text);
            bean.setRawText(rawText);
            bean.setFrom_device("Twitter.com");
            bean.setFrom_web("TWITTER");
            twitterBeanList.add(bean);
            //System.out.println(bean);
        }
    }


    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        //logger.info(request.getAdditionalHeaders());
        if (url.equals(this.url)) {
            logger.info("下载文件：" + url + "\tStatus_Code: " + status_code);
            setFinished(true);
        }
    }

    private static Date changeTime(String time){
        long now = System.currentTimeMillis();
        Date publish_time = null;
        //System.out.println(time);
        if(time.equals("just now")){
            now -= 30000;
            publish_time = new Date(now);
        } else if(time.endsWith("s")){
            now -= Integer.valueOf(time.substring(0,time.length()-1))*1000;
            publish_time = new Date(now);
        }else if(time.endsWith("m")){
            now -= Integer.valueOf(time.substring(0,time.length()-1))*60*1000;
            publish_time = new Date(now);
        }else if(time.endsWith("h")){
            now -= Integer.valueOf(time.substring(0,time.length()-1))*60*60*1000;
            publish_time = new Date(now);
        }else if(time.matches("[a-zA-Z]{3} [0-3]{0,1}[0-9]")){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy MMM d", Locale.US);
            try {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                publish_time = formatter.parse(year+" "+time);
            }catch (ParseException e){
                publish_time = null;
            }
        }else if(time.matches("[0-3]{0,1}[0-9] [a-zA-Z]{3} [0-9]{0,1}[0-9]")){
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yy",Locale.US);
            try {
                publish_time = formatter.parse(time);
            }catch (ParseException e){
                publish_time = new Date(now);
            }
        }else{
            publish_time = new Date(now);
        }
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        //System.out.println(formatter.format(publish_time));
        return new Date(now);
    }

    public List<WeiboBean> getWeiboBeanList() {
        return twitterBeanList;
    }

    public static void main(String[] args) throws Exception {
        //System.out.println(changeTime("28 Jul 06"));
        TwitterHtmlUnitSpider spider = new TwitterHtmlUnitSpider("https://mobile.twitter.com/SatoshiLite");
        try {
            spider.setJavaScriptEnabled(false);
            ProxyBean bean = new ProxyBean("116.196.94.105",1080, Proxy.Type.SOCKS,"");
            spider.setProxy(bean);
            spider.openAndWait();
            spider.parseHtml();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
