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
        }
        return "";
    }

    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, XpathSyntaxErrorException, IOException {
        NormalNews news = new NormalNews();
//        String content = news.getContent("https://binance.zendesk.com/hc/zh-cn/articles/115003739611-币安系统升级公告");
//        String content = news.getContent("https://www.zb.com/i/blog?item=116&type=");
        String content = news.getContent("https://cex.com/Art/details/id/48.html");
        System.out.println(content);
    }
}
