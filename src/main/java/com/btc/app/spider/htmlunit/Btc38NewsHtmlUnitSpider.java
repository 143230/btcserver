package com.btc.app.spider.htmlunit;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.NewsBean;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class Btc38NewsHtmlUnitSpider extends HtmlUnitBasicSpider {
    private Date new_time;

    public Date getNew_time() {
        return new_time;
    }

    public Btc38NewsHtmlUnitSpider(String url) throws InterruptedException {
        super(url);
    }

    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        HtmlElement timeele = this.page.getFirstByXPath("//div[@class=\"lf_con lf\"]/div/div[@class=\"header\"]/p/span");
        if(timeele == null){
            throw new NoSuchElementException("No Such Element in this Page");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(timeele.getTextContent());
        //System.out.println(sdf.format(date));
        this.new_time = date;
    }

    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        if(url.equals(this.url)){
            //logger.info("下载文件："+url+"\t Type:"+response.getContentType());
            setFinished(true);
        }
    }
}
