package com.btc.app.spider.phantomjs;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.NewsBean;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class Btc38Spider extends BasicSpider{
    private List<NewsBean> newsBeans;
    private List<CoinBean> coinBeans;

    public Btc38Spider(WebDriver driver, String url) {
        super(driver, url);
    }

    @Override
    public void parseHtml(int time_wait)throws InterruptedException{

        for(int i=0;i<time_wait && driver.getPageSource().contains("coin_list");i++)Thread.sleep(1000);
        //System.out.println(driver.getPageSource());
        newsBeans = new ArrayList<NewsBean>();
        coinBeans = new ArrayList<CoinBean>();
        WebElement mainleft = driver.findElement(By.xpath("//div[@class=\"main_left\"]"));
        List<WebElement> textPapers = mainleft.findElements(By.xpath(".//div[contains(@class,\"textPaper\")]"));
        for(WebElement textPaper:textPapers){
            WebElement toutiao = textPaper.findElement(By.xpath("./h3/a"));
            String url = toutiao.getAttribute("href");
            String title = toutiao.getText();
            //System.out.println("头条："+title);
            //System.out.println("URL:"+url);
            NewsBean bean = new NewsBean();
            bean.setTitle(title);
            bean.setUrl(url);
            bean.setNew_type(1);
            newsBeans.add(bean);
            //System.out.println("========================");
            //System.out.println("关联阅读：");
            List<WebElement> relates = textPaper.findElements(By.xpath(".//div[@class=\"textcon clearfix\"]/ul/li/a"));
            for(WebElement relate:relates){
                String relate_url =  relate.getAttribute("href");
                String relate_title = relate.getText();
                //System.out.println("关联阅读网址："+relate_url);
                //System.out.println("关联阅读标题："+relate_title);
                bean = new NewsBean();
                bean.setUrl(relate_url);
                bean.setTitle(relate_title);
                bean.setNew_type(2);
                newsBeans.add(bean);
            }
        }
        List<WebElement> imagePapers = mainleft.findElements(By.xpath(".//div[contains(@class,\"imgPaper\")]"));
        for(WebElement imagePaper:imagePapers){
            WebElement toutiao = imagePaper.findElement(By.xpath("./h3/a"));
            WebElement tupian = imagePaper.findElement(By.xpath("./div[@class=\"clearfix\"]/a/img"));
            WebElement zhaiyao = imagePaper.findElement(By.xpath("./div[@class=\"clearfix\"]/p"));
            String abstracts = zhaiyao.getText();
            String image_url = tupian.getAttribute("src");
            String url = toutiao.getAttribute("href");
            String title = toutiao.getText();
            NewsBean bean = new NewsBean();
            bean.setUrl(url);
            bean.setTitle(title);
            bean.setImageurl(image_url);
            bean.setAbstracts(abstracts);
            bean.setNew_type(3);
            newsBeans.add(bean);
            //System.out.println("图片新闻："+title);
            //System.out.println("URL:"+url);
            //System.out.println("图片网址："+image_url);
            //System.out.println("新闻摘要："+abstracts);
            //System.out.println("========================");
            //System.out.println("关联阅读：");
            List<WebElement> relates = imagePaper.findElements(By.xpath("./ul/li/a"));
            for(WebElement relate:relates){
                String relate_url =  relate.getAttribute("href");
                String relate_title = relate.getText();
                bean = new NewsBean();
                bean.setUrl(relate_url);
                bean.setTitle(relate_title);
                bean.setNew_type(4);
                newsBeans.add(bean);
                //System.out.println("关联阅读网址："+relate_url);
                //System.out.println("关联阅读标题："+relate_title);
            }
        }
        //System.out.println("右半部分");
        WebElement mainright = driver.findElement(By.xpath("//div[contains(@class,\"main_right\")]"));
        List<WebElement> newPapers = mainright.findElements(By.xpath(".//div[contains(@class,\"newPaper clearfix\")]"));
        for(WebElement newPaper:newPapers){
            WebElement toutiao = newPaper.findElement(By.xpath("./div/h4/a"));
            WebElement tupian = newPaper.findElement(By.xpath("./a/img"));
            WebElement zhaiyao = newPaper.findElement(By.xpath("./div/p"));
            String abstracts = zhaiyao.getText();
            String image_url = tupian.getAttribute("src");
            String url = toutiao.getAttribute("href");
            String title = toutiao.getText();
            NewsBean bean = new NewsBean();
            bean.setUrl(url);
            bean.setTitle(title);
            bean.setImageurl(image_url);
            bean.setAbstracts(abstracts);
            bean.setNew_type(5);
            newsBeans.add(bean);
            //System.out.println("图片新闻："+title);
            //System.out.println("URL:"+url);
            //System.out.println("图片网址："+image_url);
            //System.out.println("新闻摘要："+abstracts);
            //System.out.println("========================");
        }
        //System.out.println("竞争币:");
        List<WebElement> coinInfos = mainright.findElements(By.xpath(".//div[@class=\"coinInfor\"]/ul/li/a"));
        for(WebElement coinInfo:coinInfos){
            String relate_url =  coinInfo.getAttribute("href");
            String relate_title = coinInfo.getText();
            NewsBean bean = new NewsBean();
            bean.setUrl(relate_url);
            bean.setTitle(relate_title);
            bean.setNew_type(6);
            newsBeans.add(bean);
            //System.out.println("网址："+relate_url);
            //System.out.println("标题："+relate_title);
            //System.out.println("========================");
        }
        //System.out.println("政策 发展:");
        List<WebElement> policys = mainright.findElements(By.xpath(".//div[contains(@class,\"policy\")]/ul/li/a"));
        for(WebElement policy:policys){
            String relate_url =  policy.getAttribute("href");
            String relate_title = policy.getText();
            NewsBean bean = new NewsBean();
            bean.setUrl(relate_url);
            bean.setTitle(relate_title);
            bean.setNew_type(7);
            newsBeans.add(bean);
            //System.out.println("网址："+relate_url);
            //System.out.println("标题："+relate_title);
            //System.out.println("========================");
        }

        /*System.out.println("比特币价格列表：");
        List<WebElement> coin_list = driver.findElements(By.xpath(".//div[@class=\"aside\"]/div[@class=\"price_list\"]/div[@class=\"coin_list\"]/dl"));
        for(WebElement coin:coin_list){
            WebElement nm = coin.findElement(By.xpath("./dt/a"));
            String name = nm.getText();
            System.out.println("名称："+name);
            WebElement jiage = coin.findElement(By.xpath("./dd[contains(@class,\"price\")]/a"));
            String price = jiage.getText();
            System.out.println("价格："+price);
            WebElement zhangdie = coin.findElement(By.xpath("./dd[contains(@class,\"percent\")]/a"));
            String percent = zhangdie.getText();
            System.out.println("涨跌幅："+percent);
            System.out.println("--------------------------");
        }*/
    }

    public List<NewsBean> getNewsBeans() {
        return newsBeans;
    }

    public List<CoinBean> getCoinBeans() {
        return coinBeans;
    }

    public static void main(String[] args) {
        try {
            WebDriver driver = createDriver();
            Btc38Spider spider = new Btc38Spider(driver, "http://www.btc38.com/");
            spider.openAndWait();
            spider.parseHtml(10);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
