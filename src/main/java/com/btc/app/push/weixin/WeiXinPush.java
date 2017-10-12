package com.btc.app.push.weixin;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.NewsBean;
import com.btc.app.bean.WeiboBean;
import com.btc.app.push.xinge.PushMethodInvoker;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class WeiXinPush {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

    private static final String DEFAULT_GROUP = "比特币信息推送";
    private static class LazyHolder {
        private static final WeiXinPush INSTANCE = new WeiXinPush();
    }

    private WXAutoChatSpider spider;
    private BlockingQueue<PushWeiXinInvoker> queue;
    private final Thread pusher;
    private WeiXinPush() {
        spider = new WXAutoChatSpider(WXAutoChatSpider.createDriver(), WXAutoChatSpider.WX_URL);
        queue = new PriorityBlockingQueue<PushWeiXinInvoker>();
        AsyncWeiXinPush ps = new AsyncWeiXinPush(queue);
        pusher = new Thread(ps);
        pusher.start();
    }

    public static final WeiXinPush getInstance() {
        return LazyHolder.INSTANCE;
    }

    private String createWeiboMessage(WeiboBean bean){
        String source;
        if(bean.getFrom_web().equals("WEIBO")) {
            source = String.format("%s: %s  来自 %s<br>内容： %s<br>https://m.weibo.cn/status/%s<br>时间： [%s]",
                    "微博", bean.getWbname(), bean.getFrom_device(),
                    bean.getRawText(), bean.getWbid(), sdf.format(bean.getUpdate_time()));
        }else{
            source = String.format("%s: %s  来自 %s<br>内容： %s<br>https://mobile.twitter.com%s<br>时间： [%s]",
                    "推特", bean.getWbname(), bean.getFrom_device(),
                    bean.getRawText(), bean.getWbid(), sdf.format(bean.getUpdate_time()));
        }
        return source;
    }

    public void pushAsyncWeiboToAll(WeiboBean bean) {
        pushAsyncWeiboToAll(bean, new DefaultAsyncWeiXinPushListener());
    }

    public void pushAsyncWeiboToAll(WeiboBean bean, AsyncWeiXinPushListener listener) {
        String mess = createWeiboMessage(bean);
        if(mess == null ||mess.length() <= 0){
            return;
        }
        PushWeiXinInvoker invoker = new PushToGroupInvoker(spider, listener,DEFAULT_GROUP, mess,PushWeiXinInvoker.WEIBO_MESSAGE);
        queue.add(invoker);
    }

    public String createNewsMessage(NewsBean bean){
        /*String source = String.format("<a href=\"%s\">%s</a>",bean.getUrl(),bean.getTitle());
        String s = HtmlUtils.htmlEscape(source);
        System.out.println(s);*/
        String source = String.format("%s<br>%s<br>时间：[%s]",bean.getTitle(),bean.getUrl(),sdf.format(bean.getUpdate_time()));
        return source;
    }

    public void pushASyncNewsToAll(NewsBean bean) {
        pushASyncNewsToAll(bean, new DefaultAsyncWeiXinPushListener());
    }

    public void pushASyncNewsToAll(NewsBean bean, AsyncWeiXinPushListener listener) {
        String mess = createNewsMessage(bean);
        if(mess == null ||mess.length() <= 0){
            return;
        }
        PushWeiXinInvoker invoker = new PushToGroupInvoker(spider, listener,DEFAULT_GROUP, mess,PushWeiXinInvoker.NEWS_MESSAGE);
        queue.add(invoker);
    }

    public String createCoinMessage(CoinBean bean){
        String source = String.format("%s/%s<br>当前价格：%s%s<br>类型：%s<br>涨跌幅：%s<br>时间:[%s]<br>%s",
                bean.getChinesename(),bean.getEnglishname(),bean.getMarketSymbol(),
                bean.getPrice().setScale(8,
                BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString().toString(),bean.marketType(),
                bean.getPercent().setScale(8,
                BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString().toString(),
                sdf.format(bean.getUpdate_time()),bean.getPlatform());
        return source;
    }
    public void pushASyncCoinToAll(CoinBean bean) {
        pushASyncCoinToAll(bean, new DefaultAsyncWeiXinPushListener());
    }

    public void pushASyncCoinToAll(CoinBean bean, AsyncWeiXinPushListener listener) {
        String mess = createCoinMessage(bean);
        if(mess == null ||mess.length() <= 0){
            return;
        }
        PushWeiXinInvoker invoker = new PushToGroupInvoker(spider, listener,DEFAULT_GROUP, mess,PushWeiXinInvoker.COIN_MESSAGE);
        queue.add(invoker);
    }
    public static void main(String[] args) {
        WeiXinPush push = WeiXinPush.getInstance();
        NewsBean bean = new NewsBean();
        bean.setUrl("https://www.baidu.com");
        bean.setTitle("关于聚币将再次暂停部分币种交易的公告");
        bean.setUpdate_time(new Date());
        push.pushASyncNewsToAll(bean);
        WeiboBean bean1 = new WeiboBean();
        bean1.setFrom_web("WEIBO");
        bean1.setWbname("比特币战车");
        bean1.setFrom_device("iPhone 7s");
        bean1.setUpdate_time(new Date());
        bean1.setWbid("4143236366367146");
        bean1.setRawText("【最新分享】当前持仓：比特币（价值投资、龙头币风向标、避险）、莱特币（价值投机、比特金莱特银、创始人回归、闪电支付、补涨、避险）、ETH（ICO众筹概念、避险）、注意力币（国外大佬概念、小仓位择机卖出支持ETH）；飞币（小仓位择机卖出支持ETH、新平台概念、币久龙头币概念）。 \u200B\u200B\u200B");
        bean1.setUid("1839109034");
        push.pushAsyncWeiboToAll(bean1);
        CoinBean bean2 = new CoinBean();
        bean2.setMarket_type(1);
        bean2.setChinesename("比特币");
        bean2.setEnglishname("BTC");
        bean2.setUpdate_time(new Date());
        bean2.setPercent(new BigDecimal("0.7"));
        bean2.setPrice(new BigDecimal("20001"));
        bean2.setPlatform("https://www.jubi.com/");
        push.pushASyncCoinToAll(bean2);
    }
}
