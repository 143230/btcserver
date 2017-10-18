package com.btc.app.spider.phantomjs;

import com.btc.app.bean.WeiboBean;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class WeiboSpider extends BasicSpider {
    private String userid;
    private WeiboBean weiboBean;

    public WeiboSpider(WebDriver driver, String userid) {
        super(driver, userid);
        this.userid = userid;
        this.url = String.format("https://m.weibo.cn/u/%s?uid=%s",userid,userid);
    }
    /*public WeiboSpider(WebDriver driver,String wbid){
        this.weiboId = wbid;
        this.weiboUrl = String.format("https://m.weibo.cn/u/%s?uid=%s",weiboId,weiboId);
        super(driver,weiboUrl);
    }*/

    @Override
    public void parseHtml(int wait_time) throws InterruptedException {
        for(int i=0;i<wait_time && driver.getPageSource().contains("加载中");i++)Thread.sleep(1000);
        List<WebElement> webElementList = driver.findElements(By.xpath("//div[contains(@class,'card m-panel card9')]"));
        for(WebElement element:webElementList){
            //System.out.println(element.getText());
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            List<WebElement> card_titles = element.findElements(By.xpath(".//div[@class=\"card-title\"]"));
            if(card_titles.size()>0)continue;
            weiboBean = new WeiboBean();
            weiboBean.setUid(this.userid);
            WebElement header = element.findElement(By.xpath(".//header[@class=\"weibo-top m-box m-avatar-box\"]"));
            WebElement h3text = header.findElement(By.xpath(".//h3[@class=\"m-text-cut\"]"));
            String name = h3text.getText();
            //System.out.println("NAME:"+name);
            weiboBean.setWbname(name);
            WebElement h4text = header.findElement(By.xpath(".//h4[@class=\"m-text-cut\"]"));
            WebElement timespan = h4text.findElement(By.xpath(".//span[@class=\"time\"]"));
            String time = timespan.getText();
            weiboBean.setUpdate_time(changeTime(time.trim()));
            //System.out.println("TIME:"+time);
            try {
                WebElement fromspan = h4text.findElement(By.xpath(".//span[@class=\"from\"]"));
                String from = fromspan.getText();
                //System.out.println("FROM:" + from);
                weiboBean.setFrom_device(from.substring(3));
            }catch (org.openqa.selenium.NoSuchElementException e){
                System.out.println(e.getStackTrace());
            }
            WebElement article = element.findElement(By.xpath(".//article[@class=\"weibo-main\"]"));
            WebElement weibotext = article.findElement(By.xpath(".//div[@class=\"weibo-og\"]/div[@class=\"weibo-text\"]"));
            String text = weibotext.getText();
            weiboBean.setText(text);
            //System.out.println("TEXT:"+text);
            //System.out.println("+++++++++++++++++++++++++++++++=");
            weibotext.click();
            Thread.sleep(5000);
            String idurl = this.driver.getCurrentUrl();
            System.out.println(idurl);
            if(idurl.startsWith("https://m.weibo.cn/status/")){
                String wbid = idurl.substring(26);
                weiboBean.setWbid(wbid);
            }
            break;
        }
    }
    private static Date changeTime(String time){
        long now = System.currentTimeMillis();
        if(time.equals("刚刚")){
            now -= 30000;
        } else if(time.endsWith("秒前")){
            now -= Integer.valueOf(time.substring(0,time.length()-2))*1000;
        }else if(time.endsWith("分钟前")){
            now -= Integer.valueOf(time.substring(0,time.length()-3))*60*1000;
        }else if(time.endsWith("小时前")){
            now -= Integer.valueOf(time.substring(0,time.length()-3))*60*60*1000;
        }else{
            return null;
        }
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //System.out.println(formatter.format(new Date(now)));
        return new Date(now);
    }

    public WeiboBean getWeiboBean() {
        return weiboBean;
    }

    public static void main(String[] args) {
        try {
            WebDriver driver = createDriver();
            WeiboSpider spider = new WeiboSpider(driver, "2513608082");
            spider.openAndWait();
            spider.parseHtml(10);
            driver.close();
            System.out.println(driver.getPageSource());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
