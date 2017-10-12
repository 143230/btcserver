package com.btc.app.spider.htmlunit;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.NewsBean;
import com.btc.app.spider.htmlunit.inter.CoinHumlUnitSpider;
import com.btc.app.spider.htmlunit.inter.NewsHtmlUnitSpider;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.dom.Node;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class FeiXiaoHaoNewsHtmlUnitSpider extends HtmlUnitBasicSpider implements NewsHtmlUnitSpider {
    private List<NewsBean> newsBeans;

    public FeiXiaoHaoNewsHtmlUnitSpider(String url) throws InterruptedException {
        super(url);
    }


    @Override
    public void parseHtml() throws Exception{
        if(!this.finished)throw new Exception("The Page of: "+url+" has not load Finished.");
        //logger.info("The Page of: "+url+" has start to parse.");
        List<HtmlListItem> listItems = this.page.getByXPath(".//ul[@class=\"noticeList\"]/li");
        newsBeans = new ArrayList<NewsBean>();
        for(HtmlListItem listItem:listItems){
            HtmlAnchor anchor = listItem.getFirstByXPath(".//a[@class=\"web\"]");
            String web = anchor.getTextContent().trim();
            HtmlAnchor titleAnchor = listItem.getFirstByXPath(".//a[@class=\"tit\"]");
            String url = titleAnchor.getHrefAttribute().trim();
            String title = titleAnchor.getAttribute("title").trim();
            HtmlImage image = titleAnchor.getFirstByXPath(".//img");
            String icon = image.getSrcAttribute().trim();
            HtmlSpan span = titleAnchor.getFirstByXPath(".//span[@class=\"time\"]");
            String time = span.getTextContent().trim();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(time);
            NewsBean newsBean = new NewsBean();
            newsBean.setUrl(url);
            newsBean.setTitle(title);
            newsBean.setUpdate_time(date);
            newsBean.setNew_type(1);
            newsBean.setWebicon(icon);
            newsBean.setWebname(web);
            newsBeans.add(newsBean);
//            System.out.println(newsBean);
        }

    }

    public Date getNewCreateTime(NewsBean bean) throws Exception {
        return bean.getUpdate_time();
    }

    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        //System.out.println("下载文件："+url);
        if(url.equals(this.url)){
            logger.info("下载文件："+url+"\tStatus_Code: "+status_code);
            setFinished(true);
        }
    }
    public List<NewsBean> getNewsBeanList() {
        return newsBeans;
    }

    public static void main(String[] args) throws Exception {
        FeiXiaoHaoNewsHtmlUnitSpider spider = new FeiXiaoHaoNewsHtmlUnitSpider("http://www.feixiaohao.com/notice/");
        spider.setJavaScriptEnabled(false);
        spider.openAndWait();
        spider.parseHtml();
    }

}
