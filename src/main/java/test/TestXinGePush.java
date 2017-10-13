package test;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import com.btc.app.push.xinge.AsyncXinGePushListener;
import com.btc.app.push.xinge.PushMethodInvoker;
import com.btc.app.push.xinge.XinGePush;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

public class TestXinGePush {
    private CoinBean bean;
    private XinGePush push;

    @Before
    public void prepare(){
        bean = new CoinBean();
        bean.setChinesename("BTC");
        bean.setSymbol("BTC");
        bean.setCoin_id("bitcoin");
        bean.setEnglishname("Bitcoin");
        bean.setMarket_type(32);
        bean.setRank(1);
        bean.setUrl("https://coinmarketcap.com/currencies/bitcoin/");
        bean.setTurnvolume(new BigDecimal("678784.997979"));
        bean.setTurnnumber(new BigDecimal("16619925.0"));
        bean.setPrice(new BigDecimal("1.0"));
        bean.setPercent(new BigDecimal("15.11"));
        bean.setPlatform("https://coinmarketcap.com");
        bean.setUpdate_time(new Date(1507872864000L));
        CoinInfoBean infoBean = new CoinInfoBean();
        infoBean.setChinesename("Bitcoin");
        infoBean.setEnglishname("Bitcoin");
        infoBean.setSymbol("BTC");
        infoBean.setImageurl("https://files.coinmarketcap.com/static/img/coins/128x128/bitcoin.png");
        bean.setInfoBean(infoBean);
        push = XinGePush.getInstance();
    }
    @Test
    public void testPushCoinMessageToAll(){
        push.pushASyncCoinToAll(bean, new AsyncXinGePushListener() {
            public void pushSuccess(PushMethodInvoker invoker, JSONObject object) {
                System.out.println("Push Success To Server: " + object.toString() + "\nMessage: " + invoker);
                assert object.getInt("ret_code") == 0;
            }

            public void pushException(PushMethodInvoker invoker, Exception e) {
                System.out.println(e);
                assert false;
            }
        });
        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
