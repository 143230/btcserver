package com.btc.app.spider.phantomjs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Btc38NewsSpider extends BasicSpider {
    private Date time;
    public Btc38NewsSpider(WebDriver driver, String url) {
        super(driver, url);
    }

    public void parseHtml(int time_wait) throws ParseException, InterruptedException{
        for(int i=0;i<time_wait && driver.getPageSource().contains("coin_list");i++)Thread.sleep(1000);
        WebElement timeele = driver.findElement(By.xpath("//div[@class=\"lf_con lf\"]/div/div[@class=\"header\"]/p/span"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(timeele.getText());
        System.out.println(date);
        this.time = date;
    }

    public Date getTime() {
        return time;
    }

    public static void main(String[] args) {
        try {
            WebDriver driver = createDriver();
            Btc38NewsSpider spider = new Btc38NewsSpider(driver, "http://www.btc38.com/news/2017/8/15136.html");
            spider.openAndWait();
            spider.parseHtml(1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
