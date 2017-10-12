package com.btc.app.spider.proxy;

import com.btc.app.bean.ProxyBean;
import com.btc.app.spider.htmlunit.HtmlUnitBasicSpider;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.dom.Node;

import java.net.Proxy;
import java.util.Queue;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class ThreeOneFHtmlUnitSpider extends HtmlUnitBasicSpider {
    private Queue<ProxyBean> queue;

    public ThreeOneFHtmlUnitSpider(Queue<ProxyBean> queue, String url) throws InterruptedException {
        super(url);
        this.queue = queue;
    }

    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        //System.out.println(this.page.asXml());
        HtmlTable table = this.page.getFirstByXPath(".//table[@class=\"table table-striped\"]");
        if(table == null)throw new Exception("The Page of: "+url+" has changed.");
        for(int i=1;i<table.getRowCount();i++){
            HtmlTableRow row = table.getRow(i);
            String ip = row.getCell(1).asText().trim();
            int port = Integer.valueOf(row.getCell(2).asText().trim());
            Proxy.Type type = ProxyUtils.parseType(row.getCell(4).asText().trim());
            String country = row.getCell(3).asText().trim();
            ProxyBean bean = new ProxyBean(ip,port,type,country);
            //System.out.println("Getting New Proxy: "+bean);
            queue.add(bean);
        }
    }

    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        //logger.info(request.getAdditionalHeaders());
        if(url.equals(this.url)){
            //logger.info("下载文件："+url+"\tStatus_Code: "+status_code);
            setFinished(true);
        }
    }

    public static void main(String[] args) throws Exception {
        ThreeOneFHtmlUnitSpider spider = new ThreeOneFHtmlUnitSpider(null,"http://31f.cn/http-proxy/");
        spider.setJavaScriptEnabled(false);
        spider.openAndWait();
        spider.parseHtml();
    }

}
