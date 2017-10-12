package com.btc.app.push.xinge;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import com.btc.app.bean.NewsBean;
import com.btc.app.bean.WeiboBean;
import com.btc.app.util.EmojiMapper;
import com.btc.app.util.MarketTypeMapper;
import com.tencent.xinge.*;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static com.btc.app.util.MarketTypeMapper.getMarketNameType;

public class XinGePush {
    public static final int ENVIRONMENT_TEST = 0;
    public static final int ENVIRONMENT_PRODUCT = 1;
    public static final int current_environment = ENVIRONMENT_TEST;
//    public static final int current_environment = ENVIRONMENT_PRODUCT;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static class LazyHolder {
        private static final XinGePush INSTANCE = new XinGePush();
    }

    static final int IOS_TYPE = XingeApp.IOSENV_DEV;

    public static final XinGePush getInstance() {
        return LazyHolder.INSTANCE;
    }

    private XingeApp ios_xinge;
    private XingeApp android_xinge;
    private BlockingQueue<PushMethodInvoker> queue;
    private final Thread pusher;
    private static final long IOS_ACCESS_ID = 2200265987L;
    private static final String IOS_SECRET_KEY = "1d135d073af6e90d0d3ff3997da38adf";


    private static final long ANDROID_ACCESS_ID = 2100265989L;
    private static final String ANDROID_SECRET_KEY = "cdcf5e42526d1efe7596010bfd479784";

    private XinGePush() {
        ios_xinge = new XingeApp(IOS_ACCESS_ID, IOS_SECRET_KEY);
        android_xinge = new XingeApp(ANDROID_ACCESS_ID, ANDROID_SECRET_KEY);
        queue = new PriorityBlockingQueue<PushMethodInvoker>();
        AsyncXinGePush ps = new AsyncXinGePush(queue);
        pusher = new Thread(ps);
        pusher.start();
    }

    //下发所有设备
    protected JSONObject pushAllDevice(MessageIOS message) {
        JSONObject obj = ios_xinge.pushAllDevice(0, message, XingeApp.IOSENV_DEV);
        return obj;
    }

    //下发所有Android设备
    protected JSONObject pushAllDevice(Message message) {
        JSONObject obj = android_xinge.pushAllDevice(0, message);
        return obj;
    }

    /*==================华丽分割线，上面都不是我写的~~~====================================================================*/

    private MessageIOS createWeiboMessage(WeiboBean bean) {
        MessageIOS mess = new MessageIOS();
        mess.setExpireTime(86400);
        JSONObject obj = new JSONObject();
        JSONObject aps = new JSONObject();
        JSONObject alert = new JSONObject();
        alert.put("title", bean.getWbname());
        String text = bean.getRawText();
        String type = bean.getFrom_web();
        if (type.equalsIgnoreCase("WEIBO")) {
            if (text != null && text.length() > 78) {
                text = text.substring(0, 78);
            }
            if (text != null) {
                alert.put("body", text + (text.length() >= 78 ? "..." : "") + "详情点击>>\n微博发表时间：" + sdf.format(bean.getUpdate_time()));
            } else {
                alert.put("body", "详情点击进入>>\n微博发表时间：" + sdf.format(bean.getUpdate_time()));
            }
        } else if (type.equalsIgnoreCase("TWITTER")) {
            if (text != null && text.length() > 158) {
                text = text.substring(0, 158);
            }
            if (text != null) {
                alert.put("body", text + (text.length() >= 158 ? "..." : "") + "详情点击>>\nTwitter发表时间：" + sdf.format(bean.getUpdate_time()));
            } else {
                alert.put("body", "详情点击进入>>\nTwitter发表时间：" + sdf.format(bean.getUpdate_time()));
            }
        }
        alert.put("imageurl", bean.getImageurl());
        alert.put("type", bean.getFrom_web());
        alert.put("weiboid", bean.getWbid());
        alert.put("userid", bean.getUid());
        aps.put("sound", "beep.wav");
        aps.put("alert", alert);
        aps.put("badge", 1);
        //aps.put("content-available", 1);
        obj.put("aps", aps);
        mess.setRaw(obj.toString());
        System.out.println(obj.toString(4));
        return mess;
    }

    private MessageIOS createNewsMessage(NewsBean bean) {
        MessageIOS mess = new MessageIOS();
        mess.setExpireTime(86400);
        JSONObject obj = new JSONObject();
        JSONObject aps = new JSONObject();
        JSONObject alert = new JSONObject();
        alert.put("title", bean.getTitle());
        String text = bean.getAbstracts();
        if (text != null) {
            if (text.length() > 78) {
                text = text.substring(0, 78);
            }
            text = text + "...详情点击>>";
        } else {
            text = "详情点击进入>>";
        }
        if (bean.getWebname() != null) {
            alert.put("body", text + "\n新闻发表时间：" + sdf.format(bean.getUpdate_time())
                    + "\n来自：" + bean.getWebname());
        } else {
            alert.put("body", text + "\n新闻发表时间：" + sdf.format(bean.getUpdate_time()));
        }
        alert.put("icon", bean.getWebicon());
        alert.put("type", "NEWS");
        alert.put("newsurl", bean.getUrl());
        aps.put("sound", "beep.wav");
        aps.put("alert", alert);
        aps.put("badge", 1);
        //aps.put("content-available", 1);
        obj.put("aps", aps);
        mess.setRaw(obj.toString());
        System.out.println(obj.toString(4));
        return mess;
    }

    private MessageIOS createCoinMessage(CoinBean bean) {
        MessageIOS mess = new MessageIOS();
        mess.setExpireTime(86400);
        JSONObject obj = new JSONObject();
        JSONObject aps = new JSONObject();
        JSONObject alert = new JSONObject();
        String title = bean.getChinesename() + "/" + bean.getEnglishname();
        if (bean.getRank() > 0) {
            title += "            排名：" + bean.getRank();
        }
        alert.put("title", title);
        alert.put("body", "涨跌幅：" + bean.getPercent().setScale(8,
                BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString().toString()
                + "%\n当前价格：" + bean.getPrice() + getMarketNameType(bean.getMarket_type()) + "\n来自：" + bean.getPlatform() +
                "\n点击获取更多虚拟币信息>>");
        alert.put("type", "COIN");
        alert.put("englishname", bean.getEnglishname());
        CoinInfoBean infoBean = bean.getInfoBean();
        if (infoBean != null && infoBean.getImageurl() != null) {
            alert.put("image", infoBean.getImageurl());
        }
        alert.put("platform", bean.getPlatform());
        if (bean.getRank() > 0) {
            alert.put("rank", bean.getRank());
        }
        aps.put("sound", "beep.wav");
        aps.put("alert", alert);
        aps.put("badge", 1);
        //aps.put("content-available", 1);
        obj.put("aps", aps);
        mess.setRaw(obj.toString());
        System.out.println(obj.toString(4));
        return mess;
    }

    public void pushAsyncWeiboToAll(WeiboBean bean) {
        pushAsyncWeiboToAll(bean, new DefaultAsyncXinGePushListener());
    }

    public void pushAsyncWeiboToAll(WeiboBean bean, AsyncXinGePushListener listener) {
        MessageIOS mess = createWeiboMessage(bean);
        PushMethodInvoker invoker = new PushAllDevicesInvoker(ios_xinge, PushMethodInvoker.WEIBO_MESSAGE, listener, mess);
        queue.add(invoker);
    }

    public synchronized JSONObject pushSyncWeiboToAll(WeiboBean bean) {
        MessageIOS mess = createWeiboMessage(bean);
        return this.pushAllDevice(mess);
    }

    public synchronized JSONObject pushSyncNewsToAll(NewsBean bean) {
        MessageIOS mess = createNewsMessage(bean);
        return this.pushAllDevice(mess);
    }

    public void pushASyncNewsToAll(NewsBean bean) {
        pushASyncNewsToAll(bean, new DefaultAsyncXinGePushListener());
    }

    public void pushASyncNewsToAll(NewsBean bean, AsyncXinGePushListener listener) {
        MessageIOS mess = createNewsMessage(bean);
        PushMethodInvoker invoker = new PushAllDevicesInvoker(ios_xinge, PushMethodInvoker.NEWS_MESSAGE, listener, mess);
        queue.add(invoker);
    }

    public synchronized JSONObject pushSyncCoinToAll(CoinBean bean) {
        MessageIOS mess = createCoinMessage(bean);
        return this.pushAllDevice(mess);
    }


    public void pushASyncCoinToAll(CoinBean bean) {
        pushASyncCoinToAll(bean, new DefaultAsyncXinGePushListener());
    }

    public void pushASyncCoinToAll(CoinBean bean, AsyncXinGePushListener listener) {
        MessageIOS mess = createCoinMessage(bean);
        PushMethodInvoker invoker = new PushAllDevicesInvoker(ios_xinge, PushMethodInvoker.COIN_MESSAGE, listener, mess);
        queue.add(invoker);
    }

    public synchronized JSONObject batchSetTagsSync(String token, String tag) {
        List<TagTokenPair> pairs = new ArrayList<TagTokenPair>();
        pairs.add(new TagTokenPair(tag, token));
        BatchSetTagsInvoker invoker = new BatchSetTagsInvoker(ios_xinge, PushMethodInvoker.BATCH_SET_TAG, null, pairs);
        return invoker.invoke();
    }

    public synchronized JSONObject batchDelTagsSync(String token, String tag) {
        List<TagTokenPair> pairs = new ArrayList<TagTokenPair>();
        pairs.add(new TagTokenPair(tag, token));
        BatchDelTagsInvoker invoker = new BatchDelTagsInvoker(ios_xinge, PushMethodInvoker.BATCH_DEL_TAG, null, pairs);
        return invoker.invoke();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(XingeApp.pushAllAndroid(ANDROID_ACCESS_ID, ANDROID_SECRET_KEY, "标题", "大家好!"));
        XinGePush push = XinGePush.getInstance();
        for (int i = 0; i < 1; i++) {
            Message mess = new Message();

            mess.setExpireTime(86400);
            mess.setTitle("title");
            mess.setContent("content");
            mess.setType(Message.TYPE_NOTIFICATION);
            mess.setStyle(new Style(0,1,1,0,0));
            ClickAction action = new ClickAction();
            action.setActionType(ClickAction.TYPE_URL);
            action.setUrl("http://xg.qq.com");
            action.setConfirmOnUrl(1);
            mess.setAction(action);

            /*mess.setExpireTime(86400);
            JSONObject obj = new JSONObject();
            JSONObject aps = new JSONObject();
            JSONObject alert = new JSONObject();//一行43个字符
            String from = "聚币网";
            String title = "羽毛币/FT " + EmojiMapper.getUTF8Emoji("[赞]");
            int blankinsert = 86 - getStringWidth(from) * 2 - getStringWidth(title) * 2 - 5;
            //System.out.println(blankinsert);
            alert.put("title", title + genBlankStr(blankinsert) + "来自 " + from);
            //                  比特币战车比特币战车比特币战车比特币战车车
            Date date = new Date();
            alert.put("body", "BTC");
            alert.put("weiboid", "4149820304230131");
            alert.put("type", "WEIBO");
            alert.put("userid", "1839109034");
            aps.put("sound", "beep.wav");
            aps.put("alert", alert);
            aps.put("badge", 1);

            //aps.put("content-available", 1);
            obj.put("aps", aps);
            mess.setRaw(obj.toString());*/
            //System.out.println(obj.toString(4));
            System.out.println(push.pushAllDevice( mess));

            /*List<String> tags = new ArrayList<String>();
            tags.add("BTC");
            tags.add("VIP");
            PushMethodInvoker invoker = new PushByTagsInvoker(push.xinge, PushMethodInvoker.WEIBO_MESSAGE,
                    new DefaultAsyncXinGePushListener(), mess, tags, "AND");
//            PushMethodInvoker invoker = new PushByTokenInvoker(push.xinge,PushMethodInvoker.WEIBO_MESSAGE,new DefaultAsyncXinGePushListener()
//            ,mess,"d370245f3c7f838c5d8d2cd8f9d71f984c76d3c4c6bb309f21904998463babeb");
            push.queue.add(invoker);*/
        }
        /*List<String> pushIds = new ArrayList<String>();
        pushIds.add("2962654540");
        pushIds.add("2964949973");
        System.out.println(push.queryPushStatus(pushIds).toString(4));*/

    }
}
