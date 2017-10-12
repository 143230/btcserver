package com.btc.app.spider.proxy;

import com.btc.app.bean.ProxyBean;
import com.btc.app.spider.htmlunit.HtmlUnitBasicSpider;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import java.net.Proxy;
import java.util.List;
import java.util.Queue;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class XiCiDaiLiHtmlUnitSpider extends HtmlUnitBasicSpider {
    private Queue<ProxyBean> queue;

    public XiCiDaiLiHtmlUnitSpider(Queue<ProxyBean> queue,String url) throws InterruptedException {
        super(url);
        this.queue = queue;
    }

    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        HtmlTable table = this.page.getFirstByXPath(".//table[@id=\"ip_list\"]");
        if(table == null)throw new Exception("The Page of: "+url+" has changed.");
        for(int i=1;i<table.getRowCount();i++){
            HtmlTableRow row = table.getRow(i);
            String ip = row.getCell(1).asText().trim();
            int port = Integer.valueOf(row.getCell(2).asText().trim());
            Proxy.Type type = ProxyUtils.parseType(row.getCell(5).asText().trim());
            String country = row.getCell(3).asText().trim();
            ProxyBean bean = new ProxyBean(ip,port,type,country);
            //System.out.println("Getting New Proxy: "+bean);
            queue.add(bean);
        }
    }

    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        if(url.equals(this.url)){
            //logger.info("下载文件："+url+"\tStatus_Code: "+status_code+"\tCount:");
            setFinished(true);
        }
    }

}
