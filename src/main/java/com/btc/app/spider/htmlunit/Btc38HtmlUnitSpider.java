package com.btc.app.spider.htmlunit;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.NewsBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.spider.htmlunit.inter.NewsHtmlUnitSpider;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.dom.Node;

import java.util.*;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class Btc38HtmlUnitSpider extends HtmlUnitBasicSpider implements NewsHtmlUnitSpider,CoinHumlUnitSpider {
    private List<NewsBean> newsBeans;
    private List<CoinBean> coinBeans;

    public Btc38HtmlUnitSpider(String url) throws InterruptedException {
        super(url);
    }


    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        //logger.info("The Page of: "+url+" has start to parse.");
        newsBeans = new ArrayList<NewsBean>();
        coinBeans = new ArrayList<CoinBean>();
        HtmlDivision mainleft = this.page.getFirstByXPath("//div[@class=\"main_left\"]");
        List<HtmlDivision> textPapers = mainleft.getByXPath(".//div[contains(@class,\"textPaper\")]");
        for(HtmlDivision textPaper:textPapers){
            HtmlAnchor toutiao = textPaper.getFirstByXPath(".//h3/a");
            String url = toutiao.getAttribute("href");
            String title = toutiao.getTextContent();
            for (final DomNode child : toutiao.getChildren()) {
                final short childType = child.getNodeType();
                if (childType == Node.TEXT_NODE) {
                    title = child.getTextContent();
                }
            }
            //System.out.println("头条："+title);
            //System.out.println("URL:"+url);
            NewsBean bean = new NewsBean();
            bean.setTitle(title);
            bean.setUrl(url);
            bean.setNew_type(1);
            newsBeans.add(bean);
            //System.out.println("========================");
            //System.out.println("关联阅读：");
            List<HtmlAnchor> relates = textPaper.getByXPath(".//div[@class=\"textcon clearfix\"]/ul/li/a");
            for(HtmlAnchor relate:relates){
                String relate_url =  relate.getAttribute("href");
                String relate_title = relate.getTextContent();
                //System.out.println("关联阅读网址："+relate_url);
                //System.out.println("关联阅读标题："+relate_title);
                bean = new NewsBean();
                bean.setUrl(relate_url);
                bean.setTitle(relate_title);
                bean.setNew_type(2);
                newsBeans.add(bean);
            }
        }
        List<HtmlDivision> imagePapers = mainleft.getByXPath(".//div[contains(@class,\"imgPaper\")]");
        for(HtmlDivision imagePaper:imagePapers){
            HtmlAnchor toutiao = imagePaper.getFirstByXPath("./h3/a");
            HtmlImage tupian = imagePaper.getFirstByXPath("./div[@class=\"clearfix\"]/a/img");
            HtmlParagraph zhaiyao = imagePaper.getFirstByXPath("./div[@class=\"clearfix\"]/p");
            String abstracts = zhaiyao.getTextContent();
            String image_url = tupian.getAttribute("src");
            String url = toutiao.getAttribute("href");
            String title = toutiao.getTextContent();
            NewsBean bean = new NewsBean();
            bean.setUrl(url);
            bean.setTitle(title);
            bean.setImageurl(image_url);
            bean.setAbstracts(abstracts);
            bean.setNew_type(3);
            newsBeans.add(bean);
            List<HtmlAnchor> relates = imagePaper.getByXPath("./ul/li/a");
            for(HtmlAnchor relate:relates){
                String relate_url =  relate.getAttribute("href");
                String relate_title = relate.getTextContent();
                bean = new NewsBean();
                bean.setUrl(relate_url);
                bean.setTitle(relate_title);
                bean.setNew_type(4);
                newsBeans.add(bean);
            }
        }
        HtmlDivision mainright = this.page.getFirstByXPath("//div[contains(@class,\"main_right\")]");
        List<HtmlDivision> newPapers = mainright.getByXPath(".//div[contains(@class,\"newPaper clearfix\")]");
        for(HtmlDivision newPaper:newPapers){
            HtmlAnchor toutiao = newPaper.getFirstByXPath("./div/h4/a");
            HtmlImage tupian = newPaper.getFirstByXPath("./a/img");
            HtmlParagraph zhaiyao = newPaper.getFirstByXPath("./div/p");
            String abstracts = zhaiyao.getTextContent();
            String image_url = tupian.getAttribute("src");
            String url = toutiao.getAttribute("href");
            String title = toutiao.getTextContent();
            NewsBean bean = new NewsBean();
            bean.setUrl(url);
            bean.setTitle(title);
            bean.setImageurl(image_url);
            bean.setAbstracts(abstracts);
            bean.setNew_type(5);
            newsBeans.add(bean);
        }
        List<HtmlAnchor> coinInfos = mainright.getByXPath(".//div[@class=\"coinInfor\"]/ul/li/a");
        for(HtmlAnchor coinInfo:coinInfos){
            String relate_url =  coinInfo.getAttribute("href");
            String relate_title = coinInfo.getTextContent();
            NewsBean bean = new NewsBean();
            bean.setUrl(relate_url);
            bean.setTitle(relate_title);
            bean.setNew_type(6);
            newsBeans.add(bean);
        }
        List<HtmlAnchor> policys = mainright.getByXPath(".//div[contains(@class,\"policy\")]/ul/li/a");
        for(HtmlAnchor policy:policys){
            String relate_url =  policy.getAttribute("href");
            String relate_title = policy.getTextContent();
            NewsBean bean = new NewsBean();
            bean.setUrl(relate_url);
            bean.setTitle(relate_title);
            bean.setNew_type(7);
            newsBeans.add(bean);
        }

        HtmlDivision analyze_left = this.page.getFirstByXPath(".//div[contains(@class,\"analyze_left\")]");
        HtmlImage ana_img = analyze_left.getFirstByXPath("./img");
        HtmlElement biaoti = analyze_left.getFirstByXPath("./h5/a/em");
        HtmlAnchor zhaiyao = analyze_left.getFirstByXPath("./p/a");
        String abstracts = zhaiyao.getTextContent();
        String image_url = ana_img.getAttribute("src");
        String url = zhaiyao.getAttribute("href");
        String title = biaoti.getTextContent();
        NewsBean newsBean = new NewsBean();
        newsBean.setUrl(url);
        newsBean.setTitle(title);
        newsBean.setImageurl(image_url);
        newsBean.setAbstracts(abstracts);
        newsBean.setNew_type(9);
        newsBeans.add(newsBean);

        List<HtmlAnchor> analyzeIntro = this.page.getByXPath(".//div[contains(@class,\"clearfix\")]" +
                "/ul[contains(@class,\"analyze_right\")]/li/a");
        for(HtmlAnchor alalyze:analyzeIntro){
            String relate_url =  alalyze.getAttribute("href");
            String relate_title = alalyze.getTextContent();
            NewsBean bean = new NewsBean();
            bean.setUrl(relate_url);
            bean.setTitle(relate_title);
            bean.setNew_type(8);
            newsBeans.add(bean);
        }
        /*
        for(NewsBean bean:newsBeans){
            String new_url = bean.getUrl();
            try {
                Btc38NewsHtmlUnitSpider spider = new Btc38NewsHtmlUnitSpider(new_url);
                spider.openAndWait(BrowserVersion.CHROME);
                spider.parseHtml();
                bean.setUpdate_time(spider.getNew_time());
            }catch (NoSuchElementException e){
                System.out.println(e.getMessage());
            }
            System.out.println(bean);
        }
        */
    }

    public Date getNewCreateTime(NewsBean bean) throws Exception {
        String new_url = bean.getUrl();
        Btc38NewsHtmlUnitSpider spider = new Btc38NewsHtmlUnitSpider(new_url);
        spider.setJavaScriptEnabled(false);
        spider.openAndWait();
        spider.parseHtml();
        spider.release();
        return spider.getNew_time();
    }

    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        //System.out.println("下载文件："+url);
        if(url.equals(this.url)){
            //logger.info("下载文件："+url+"\tStatus_Code: "+status_code);
            setFinished(true);
        }
    }
    public List<NewsBean> getNewsBeanList() {
        return newsBeans;
    }

    public List<CoinBean> getCoinBeanList() {
        return coinBeans;
    }
}
