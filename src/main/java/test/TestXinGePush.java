package test;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import com.btc.app.bean.NewsBean;
import com.btc.app.bean.WeiboBean;
import com.btc.app.push.xinge.XinGePush;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

public class TestXinGePush {
    private CoinBean coinBean;
    private NewsBean newsBean;
    private WeiboBean weiboBean;
    private XinGePush push;

    @Before
    public void prepare(){
        coinBean = new CoinBean();
        coinBean.setChinesename("BTC");
        coinBean.setSymbol("BTC");
        coinBean.setCoin_id("bitcoin");
        coinBean.setEnglishname("Bitcoin[测试]");
        coinBean.setMarket_type(32);
        coinBean.setRank(1);
        coinBean.setUrl("https://coinmarketcap.com/currencies/bitcoin/");
        coinBean.setTurnvolume(new BigDecimal("678784.997979"));
        coinBean.setTurnnumber(new BigDecimal("16619925.0"));
        coinBean.setPrice(new BigDecimal("1.0"));
        coinBean.setPercent(new BigDecimal("15.11"));
        coinBean.setPlatform("https://coinmarketcap.com");
        coinBean.setUpdate_time(new Date(1507872864000L));
        CoinInfoBean infoBean = new CoinInfoBean();
        infoBean.setChinesename("Bitcoin");
        infoBean.setEnglishname("Bitcoin");
        infoBean.setSymbol("BTC");
        infoBean.setImageurl("https://files.coinmarketcap.com/static/img/coins/128x128/bitcoin.png");
        coinBean.setInfoBean(infoBean);

        newsBean = new NewsBean();
        newsBean.setTitle("提现提币业务最新进展情况的通知[推送测试]");
        newsBean.setWebname("[云币网]");
        newsBean.setUrl("https://yunbi.zendesk.com/hc/zh-cn/articles/115000165622-提现提币业务最新进展情况的通知");
        newsBean.setWebicon("http://static.feixiaohao.com/PlatImages/20170805/e59be8d85ef4428384ea2df4ff8c6920_15_15.png");
        newsBean.setNew_type(6);
        newsBean.setUpdate_time(new Date());

        weiboBean = new WeiboBean();
        weiboBean.setWbname("比特币的那点事[推送测试]");
        weiboBean.setFrom_device("微博 weibo.com");
        weiboBean.setFrom_web("WEIBO");
        weiboBean.setImageurl("https://tva1.sinaimg.cn/crop.0.0.179.179.180/d87f5f8bjw1e9uvsfd8t6j2050050glq.jpg");
        weiboBean.setText("【“全球最幸福国家”敞开怀抱，50个比特币可投资移民】南太平洋岛国瓦努阿图成为全球首个接受加密数字货币移民的国家，其投资移民门槛是28万美元，折合本周五交易价约50个比特币。瓦努阿图曾被海外智库评为“全球最幸福国家”，该国护照免签全球118个国家地区。");
        weiboBean.setUid("3632226187");
        weiboBean.setWbid("4162710093066709");
        weiboBean.setUpdate_time(new Date());

        push = XinGePush.getInstance();
        System.out.println(push.queryDeviceCount(XinGePush.Device.android).toString(4));
    }

    @Test
    public void testPushCoinMessageToAll(){
        push.pushASyncCoinToAll(coinBean);
    }

    @Test
    public void testSinglePushAndroidCoinMessage(){
        final String token = "ef4bba012504a72347b98d9da68d8e66c7bbc28e";
        JSONObject obj = push.pushSingleDevice(token, coinBean, XinGePush.Device.android);
        System.out.println(obj.toString(4));
        assert obj.getInt("ret_code") == 0;
    }

    @Test
    public void testAddTag(){
        JSONObject obj = push.batchSetTagsSync("","bitcoin", XinGePush.Device.ios);
        System.out.println(obj.toString(4));
        assert obj.getInt("ret_code") == 0;
    }

    @Test
    public void testQueryTags(){
        JSONObject obj = push.queryTags(XinGePush.Device.android);
        System.out.println(obj.toString(4));
        assert obj.getInt("ret_code") == 0;
        obj = push.queryTags(XinGePush.Device.ios);
        System.out.println(obj.toString(4));
        assert obj.getInt("ret_code") == 0;
    }

    @Test
    public void testPushNewMessage(){
        push.pushASyncNewsToAll(newsBean);
    }

    @Test
    public void testPushWeiboMessage(){
        push.pushAsyncWeiboToAll(weiboBean);
    }

    @After
    public void clean(){
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
