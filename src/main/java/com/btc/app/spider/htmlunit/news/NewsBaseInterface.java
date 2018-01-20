package com.btc.app.spider.htmlunit.news;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public interface NewsBaseInterface {
    public String getContent(String url, String pattern) throws IOException, NoSuchAlgorithmException, KeyManagementException, XpathSyntaxErrorException;
}
