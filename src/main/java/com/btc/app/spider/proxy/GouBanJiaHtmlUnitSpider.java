package com.btc.app.spider.proxy;

import com.btc.app.bean.ProxyBean;
import com.btc.app.spider.htmlunit.HtmlUnitBasicSpider;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.Proxy;
import java.util.Queue;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class GouBanJiaHtmlUnitSpider extends HtmlUnitBasicSpider {
    private Queue<ProxyBean> queue;

    public GouBanJiaHtmlUnitSpider(Queue<ProxyBean> queue, String url) throws InterruptedException {
        super(url);
        this.queue = queue;
    }

    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        System.out.println(this.page.asXml());
        HtmlTable table = this.page.getFirstByXPath(".//div[@id=\"list\"]/table[@class=\"table\"]");
        if(table == null)throw new Exception("The Page of: "+url+" has changed.");
        for(int i=1;i<table.getRowCount();i++){
            HtmlTableRow row = table.getRow(i);
            String ip = "";
            int port = 0;
            HtmlTableCell ipCell = row.getCell(0);
            for (final DomNode child : ipCell.getChildren()) {
                final short childType = child.getNodeType();
                if(childType == Node.ELEMENT_NODE && ((HtmlElement)child).getAttribute("class").contains("port")){
                    port = Integer.valueOf(child.getTextContent().trim());
                    continue;
                }
                if (childType == Node.ELEMENT_NODE && !((HtmlElement)child).getAttribute("style").contains("none") ) {
                    ip+=child.getTextContent().trim();
                }
            }
            Proxy.Type type = ProxyUtils.parseType(row.getCell(2).asText().trim());
            String country = row.getCell(3).asText().trim();
            ProxyBean bean = new ProxyBean(ip,port,type,country);
            System.out.println("Getting New Proxy: "+bean);
            //queue.add(bean);
        }
    }

    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        //logger.info(request.getAdditionalHeaders());
        if(url.equals(this.url)){
            logger.info("下载文件："+url+"\tStatus_Code: "+status_code);
            setFinished(true);
        }
    }

    public static void main(String[] args) throws Exception {
        GouBanJiaHtmlUnitSpider spider = new GouBanJiaHtmlUnitSpider(null,"http://www.goubanjia.com/free/index.shtml");
        //spider.setJavaScriptEnabled(false);
        spider.openAndWait();
        spider.parseHtml();
    }

}
