package com.btc.app.spider.proxy;

import com.btc.app.bean.ProxyBean;
import com.btc.app.spider.htmlunit.HtmlUnitBasicSpider;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import java.net.Proxy;
import java.util.*;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class Data5UHtmlUnitSpider extends HtmlUnitBasicSpider{
    private Queue<ProxyBean> queue;

    public Data5UHtmlUnitSpider(Queue<ProxyBean> queue, String url) throws InterruptedException {
        super(url);
        this.queue = queue;
    }


    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        //logger.info("The Page of: "+url+" has start to parse.");
        List<HtmlElement> list = this.page.getByXPath(".//div[@class=\"wlist\"]/ul/li[@style=\"text-align:center;\"]/ul[@class=\"l2\"]");
        for(HtmlElement element:list){
            List<HtmlSpan> spans = element.getByXPath(".//span");
            String ip = spans.get(0).asText().trim();
            int port = Integer.valueOf(spans.get(1).asText().trim());
            Proxy.Type type = ProxyUtils.parseType(spans.get(3).asText().trim());
            String country = spans.get(4).asText().trim();
            ProxyBean bean = new ProxyBean(ip,port,type,country);
            //System.out.println("Getting New Proxy: "+bean);
            queue.add(bean);
        }
    }

    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        //System.out.println(url);
        if(url.equals(this.url)){
            setFinished(true);
        }
    }

    public static void main(String[] args) throws Exception {
        Data5UHtmlUnitSpider spider = new Data5UHtmlUnitSpider(null,"http://www.data5u.com/free/gngn/index.shtml");
        //spider.setJavaScriptEnabled(false);
        spider.openAndWait();
        spider.parseHtml();
    }
}
