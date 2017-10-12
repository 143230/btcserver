package com.btc.app.spider.htmlunit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc.app.bean.WeiboBean;
import com.btc.app.spider.htmlunit.inter.BlogHtmlUnitSpider;
import com.btc.app.util.EmojiMapper;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class WeiboHtmlUnitSpider extends HtmlUnitBasicSpider implements BlogHtmlUnitSpider {
    private String weiboId;
    private List<WeiboBean> weiboBeans;
    private JSONObject cardListJson = null;
    private JSONObject userInfoJson = null;

    public WeiboHtmlUnitSpider(String wbid, BrowserVersion version) throws InterruptedException {
        super(wbid,version);
        this.weiboId = wbid;
        this.url = String.format("https://m.weibo.cn/u/%s?uid=%s",weiboId,weiboId);
    }


    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        //logger.info("The Page of: "+url+" has start to parse.");
        weiboBeans = new ArrayList<WeiboBean>();
        JSONArray array = cardListJson.getJSONArray("cards");
        for(int i=0;i<array.size();i++){
            JSONObject object = array.getJSONObject(i);
            int card_type = object.getInteger("card_type");
            if(card_type!=9)continue;
            JSONObject mblog = object.getJSONObject("mblog");
            if(mblog.containsKey("title"))continue;
            WeiboBean bean = new WeiboBean();
            String created_at = mblog.getString("created_at");
            Date time = changeTime(created_at);
            bean.setUpdate_time(time);
            String id = mblog.getString("id");
            bean.setWbid(id);
            String text = mblog.getString("text");
            bean.setText(text);
            String source = mblog.getString("source");
            bean.setFrom_device(source);

            String rawText = mblog.getString("raw_text");
            if(rawText == null){
                rawText = cleanHtmlTag(text);
            }
            bean.setRawText(rawText);

            JSONObject user = mblog.getJSONObject("user");
            String uid = user.getString("id");
            bean.setUid(uid);
            String imageurl = user.getString("profile_image_url");
            bean.setImageurl(imageurl);
            String screen_name = user.getString("screen_name");
            bean.setWbname(screen_name);
            bean.setFrom_web("WEIBO");
            weiboBeans.add(bean);
//            System.out.println(bean);
        }
    }

    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        //int status_code = response.getStatusCode();
        if(url.startsWith("https://m.weibo.cn/api/container/getIndex?")){
            //logger.info("下载文件："+url+"\tStatus_Code: "+status_code);
            String content = response.getContentAsString();
            JSONObject json = JSONObject.parseObject(content);
            if(json.containsKey("cardlistInfo")){
                cardListJson = json;
            }else if(json.containsKey("userInfo")){
                userInfoJson = json;
            }
        }
        if(cardListJson!=null && userInfoJson!=null){
            this.setFinished(true);
        }
    }

    private Date changeTime(String time){
        long now = System.currentTimeMillis();
        if(time.equals("刚刚")){
            now -= 30000;
        } else if(time.endsWith("秒前")){
            now -= Integer.valueOf(time.substring(0,time.length()-2))*1000;
        }else if(time.endsWith("分钟前")){
            now -= Integer.valueOf(time.substring(0,time.length()-3))*60*1000;
        }else if(time.endsWith("小时前")){
            now -= Integer.valueOf(time.substring(0,time.length()-3))*60*60*1000;
        }else if(time.matches("[0-1][0-9]-[0-3][0-9]")){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                Date publish_time = formatter.parse(year+"-"+time);
                return publish_time;
            }catch (ParseException e){
                return null;
            }
        }else if(time.matches("[0-9]{4}-[0-1][0-9]-[0-3][0-9]")){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date publish_time = formatter.parse(time);
                return publish_time;
            }catch (ParseException e){
                return new Date(now);
            }
        }else{
            return new Date(now);
        }
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //System.out.println(formatter.format(new Date(now)));
        return new Date(now);
    }

    public static String cleanHtmlTag(String source){
        try {
            Document doc = Jsoup.parse("<html><body><div id='html_content'>" + source + "</div></body></html>");
            Elements element = doc.select("div");
            Elements imgs = element.select("img[src]");
            for (int i = 0; i < imgs.size(); i++) {
                Element element1 = imgs.get(i);
                if (element1.hasAttr("alt")) {
                    String alt = element1.attr("alt");
                    //System.out.println(alt);
                    element1.appendText(EmojiMapper.getUTF8Emoji(alt));
                }
            }
            String text = element.text();
            //System.out.println("Parsed Result:=========>>>" + text);
            return text;
        }catch (Exception e){
            return source;
        }
    }

    public List<WeiboBean> getWeiboBeanList() {
        return weiboBeans;
    }

    public static void main(String[] args) throws Exception {
        /*BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("./test")));
        String line = "";
        while((line = reader.readLine())!=null) {
            System.out.println(line);
            cleanHtmlTag(line);
        }
        reader.close();*/
        WeiboHtmlUnitSpider spider = new WeiboHtmlUnitSpider("1839109034",BrowserVersion.INTERNET_EXPLORER);
        //spider.setJavaScriptEnabled(false);
        spider.openAndWait();
        spider.parseHtml();
        spider.release();
    }
}
