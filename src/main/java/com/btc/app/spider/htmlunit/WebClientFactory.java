package com.btc.app.spider.htmlunit;

import com.btc.app.pool.ObjectFactory;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.log4j.Logger;

public class WebClientFactory implements ObjectFactory<WebClient> {
    private Logger logger = Logger.getLogger(WebClientFactory.class);
    public WebClient createNew() {
        WebClient client = HtmlUnitBasicSpider.getHtmlPage();
        //logger.info("create new WebClient: "+client);
        return client;
    }
}
