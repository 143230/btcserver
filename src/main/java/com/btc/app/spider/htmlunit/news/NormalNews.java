package com.btc.app.spider.htmlunit.news;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import cn.wanghaomiao.xpath.model.JXNode;
import com.btc.app.spider.http.HttpBasicSpider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class NormalNews{
    public static String normalContent(String url,String pattern) throws IOException, NoSuchAlgorithmException, KeyManagementException, XpathSyntaxErrorException {
        HttpBasicSpider spider = new HttpBasicSpider(url);
        String htmlsrc =spider.openAndGetContent();
//      System.out.println(htmlsrc);
        JXDocument jxDocument = new JXDocument(htmlsrc);
        List<JXNode> nodeList = jxDocument.selN(pattern);
        //assert nodeList.size() == 1;
        JXNode node = nodeList.get(0);
        /*for(JXNode node: nodeList) {
            String content = node.getElement().text();
            System.out.println(content);
        }*/
//      System.out.println(content);
        return node.getElement().text();
    }

    public static String getContent(String url) throws IOException, KeyManagementException, NoSuchAlgorithmException, XpathSyntaxErrorException {
        URL u = new URL(url);
        System.out.println(u.getHost());
        String host = u.getHost();
        if(host.equals("binance.zendesk.com")) {
            return normalContent(url, "//section[@class='article-info']");
        }else if(host.equals("www.zb.com")){
            return normalContent(url, "//article");
        }else if(host.equals("cex.com")){
            return normalContent(url, ".//div[@class='txt']");
        }else if(host.equals("help.big.one")){
            return normalContent(url, "//div[@class='article-body']");
        }else if(host.equals("www.fatbtc.com")){
            return normalContent(url, "//div[@class='blog-post show1']");
        }else if(host.equals("news.kucoin.com")){
            return normalContent(url, "//div[@class='post-content']");
        }
        return "";
    }

    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, XpathSyntaxErrorException, IOException {
        NormalNews news = new NormalNews();
//        String content = news.getContent("https://binance.zendesk.com/hc/zh-cn/articles/115003739611-币安系统升级公告");
//        String content = news.getContent("https://www.zb.com/i/blog?item=116&type=");
//        String content = news.getContent("https://cex.com/Art/details/id/48.html");
//        String content = news.getContent("https://www.fatbtc.com/news-detail-37.htm");
        String content = news.getContent("https://news.kucoin.com/singularitynet-agi%E4%BA%8E2018%E5%B9%B41%E6%9C%8820%E6%97%A5%E4%B8%8A%E7%BA%BFkucoin/");
        System.out.println(content);
    }
}
