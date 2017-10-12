package test;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.ProxyBean;
import com.btc.app.bean.WeiboBean;
import com.btc.app.spider.htmlunit.HtmlUnitBasicSpider;
import com.btc.app.spider.proxy.ProxyUtils;
import com.btc.app.util.MarketTypeMapper;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDivElement;
import org.w3c.dom.html.HTMLLIElement;

import java.math.BigDecimal;
import java.net.Proxy;
import java.util.Date;
import java.util.List;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class TestHtmlUnitSpider extends HtmlUnitBasicSpider {
    private int count = 0;

    public TestHtmlUnitSpider(String url) throws InterruptedException {
        super(url);
    }
    public TestHtmlUnitSpider(String url,int timewait) throws InterruptedException {
        super(url,timewait);
    }

    @Override
    public void parseHtml() throws Exception {
        if (!this.finished) throw new Exception("The Page of: " + url + " has not load Finished.");

        client.getPage("http://www.feixiaohao.com/all/#USD");
    }



    public void downloadFile(WebRequest request, WebResponse response) {
        String url = request.getUrl().toString();
        int status_code = response.getStatusCode();
        //logger.info(request.getAdditionalHeaders().);
        logger.info(response.getContentType());
        logger.info("下载文件：" + url + "\tStatus_Code: " + status_code + "\tCount:" + count++);
        if (url.equals(this.url)) {
            setFinished(true);
        }
    }

    public static void main(String[] args) throws Exception {
        //https://yunbi.com/?warning=false
        //https://www.okcoin.com/
        //https://binance.zendesk.com/hc/en-us
        //https://www.bitfinex.com/
        //https://www.chbtc.com/
        //http://www.feixiaohao.com/all/#CNY
        //https://coinmarketcap.com/currencies/views/all/
        TestHtmlUnitSpider spider;
        spider = new TestHtmlUnitSpider("http://www.feixiaohao.com/all/#CNY",30);
        try {
//            spider.setJavaScriptEnabled(false);
            //ProxyBean bean = new ProxyBean("116.196.94.105",1080, Proxy.Type.SOCKS,"");
            //spider.setProxy(bean);
            spider.openAndWait();
            spider.parseHtml();
            Thread.sleep(2000000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
