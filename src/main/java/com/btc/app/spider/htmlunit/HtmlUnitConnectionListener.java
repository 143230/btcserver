package com.btc.app.spider.htmlunit;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;

import java.io.IOException;

public class HtmlUnitConnectionListener extends FalsifyingWebConnection {

    private HtmlUnitBasicSpider spider;
    public HtmlUnitConnectionListener(WebClient webClient, HtmlUnitBasicSpider spider) throws IllegalArgumentException {
        super(webClient);
        this.spider = spider;
    }
    public HtmlUnitConnectionListener(WebClient webClient) throws IllegalArgumentException {
        super(webClient);
        this.spider = null;
    }

    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {
        // 得到了这个响应，你想怎么处理就怎么处理了，不多写了

        WebResponse response = super.getResponse(request);
        String url = response.getWebRequest().getUrl().toString();

        //System.out.println(response.getStatusCode());
        //System.out.println(response.getStatusMessage());
        //System.out.println(response.getResponseHeaders());
//        System.out.println("下载文件链接：" + url);
        //System.out.println(response.getContentAsString());
        if(spider != null) {
            spider.downloadFile(request, response);
        }
        if(spider.checkUrl(url)){
            return createWebResponse(response.getWebRequest(), "", "application/javascript", 200, "Ok");
        }
        return response;
    }

}