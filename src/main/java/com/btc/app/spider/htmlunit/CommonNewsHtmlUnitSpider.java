package com.btc.app.spider.htmlunit;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class CommonNewsHtmlUnitSpider extends HtmlUnitBasicSpider {
    private Date new_time;
    private String tag;
    private String attr_name;
    private String attr_val;
    private String dateFormat;

    public Date getNew_time() {
        return new_time;
    }

    public CommonNewsHtmlUnitSpider(String url,String tag, String attr_name, String attr_val,String dateFormat) throws InterruptedException {
        super(url);
        this.tag = tag;
        this.attr_name = attr_name;
        this.attr_val = attr_val;
        this.dateFormat = dateFormat;
    }

    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        HtmlElement timeele = this.page.getFirstByXPath(String.format(".//%s[@%s=\"%s\"]",tag,attr_name,attr_val));
        if(timeele == null){
            throw new NoSuchElementException("No Such Element in this Page");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);//"yyyy-MM-dd HH:mm:ss"
        Date date = sdf.parse(timeele.getTextContent().trim());
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
