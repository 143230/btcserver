package com.btc.app.spider.htmlunit;

import com.btc.app.pool.Pool;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.log4j.Logger;

public class WebClientValidator implements Pool.Validator<WebClient>{
    private Logger logger = Logger.getLogger(WebClientValidator.class);
    public boolean isValid(WebClient client) {
        return true;
    }

    public void invalidate(WebClient client) {
        client.getCurrentWindow().getJobManager().removeAllJobs();
        client.close();
    }
}
