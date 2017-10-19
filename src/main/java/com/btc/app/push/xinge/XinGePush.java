package com.btc.app.push.xinge;

import com.btc.app.bean.CoinBean;
import com.btc.app.bean.CoinInfoBean;
import com.btc.app.bean.NewsBean;
import com.btc.app.bean.WeiboBean;
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
    public static final boolean ANDROID_PUSH_ENABLE = true;
    public static final int current_environment = ENVIRONMENT_PRODUCT;
//    public static final int current_environment = ENVIRONMENT_TEST;
    public static final boolean IOS_PUSH_ENABLED = true;

    public static enum Device {
        android,
        ios
    }

//    public static final String COIN_PUSHALL_ANDROID_TAG = "COIN_PUSHALL_ANDROID_MESSAGE_TAG";
//    public static final String NEWS_PUSHALL_ANDROID_TAG = "NEWS_PUSHALL_ANDROID_MESSAGE_TAG";
//    public static final String WEIBO_PUSHALL_ANDROID_TAG = "WEIBO_PUSHALL_ANDROID_MESSAGE_TAG";

    public static final String PUSHALL_ANDROID_TAG = "PUSHALL_ANDROID_MESSAGE_TAG";
    public static final String PUSHALL_IOS_TAG = "PUSHALL_IOS_MESSAGE_TAG";
//    public static final String COIN_PUSHALL_IOS_TAG = "COIN_PUSHALL_IOS_MESSAGE_TAG";
//    public static final String NEWS_PUSHALL_IOS_TAG = "NEWS_PUSHALL_IOS_MESSAGE_TAG";
//    public static final String WEIBO_PUSHALL_IOS_TAG = "WEIBO_PUSHALL_IOS_MESSAGE_TAG";
    public static final String PUSHTAGS_ANDROID_TAG = "PUSHTAGS_ANDROID_MESSAGE_TAG";
    public static final String PUSHTAGS_IOS_TAG = "PUSHTAGS_IOS_MESSAGE_TAG";
    public static final String OTHER_TAG = "OTHER_MESSAGE_TAG";

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

    //下发所有IOS设备
    protected JSONObject pushAllDevice(MessageIOS message, int type) {
        if (XinGePush.current_environment == XinGePush.ENVIRONMENT_TEST || !IOS_PUSH_ENABLED) {
            return new JSONObject("{\"result\":{\"push_id\":\"=====pushAllDevice_IOS_TEST=====\"},\"ret_code\":0}");
        }
        JSONObject obj = ios_xinge.pushAllDevice(0, message, type);
        return obj;
    }

    //下发所有Android设备
    protected JSONObject pushAllDevice(Message message) {
        if (XinGePush.current_environment == XinGePush.ENVIRONMENT_TEST || !ANDROID_PUSH_ENABLE) {
            return new JSONObject("{\"result\":{\"push_id\":\"=====pushAllDevice_ANDROID_TEST=====\"},\"ret_code\":0}");
        }
        JSONObject obj = android_xinge.pushAllDevice(0, message);
        return obj;
    }

    //单个设备下发Android通知消息
    protected JSONObject pushSingleDevice(String token, Message message) {
        if (XinGePush.current_environment == XinGePush.ENVIRONMENT_TEST || !ANDROID_PUSH_ENABLE) {
            return new JSONObject("{\"result\":{\"push_id\":\"=====pushSingleDevice_ANDROID_TEST=====\"},\"ret_code\":0}");
        }
        JSONObject obj = android_xinge.pushSingleDevice(token, message);
        return obj;
    }

    //单个设备IOS静默通知(iOS7以上)
    protected JSONObject pushSingleDevice(String token, MessageIOS message) {
        if (XinGePush.current_environment == XinGePush.ENVIRONMENT_TEST || !IOS_PUSH_ENABLED) {
            return new JSONObject("{\"result\":{\"push_id\":\"=====pushSingleDevice_IOS_TEST=====\"},\"ret_code\":0}");
        }
        return ios_xinge.pushSingleDevice(token, message, IOS_TYPE);
    }

    //下发标签选中Android设备
    protected JSONObject pushByTags(List<String> tags, String tagOp, Message message) {
        if (XinGePush.current_environment == XinGePush.ENVIRONMENT_TEST || !ANDROID_PUSH_ENABLE) {
            return new JSONObject("{\"result\":{\"push_id\":\"=====pushByTags_ANDROID_TEST=====\"},\"ret_code\":0}");
        }
        JSONObject ret = android_xinge.pushTags(0, tags, tagOp, message);
        return ret;
    }

    //下发标签选中IOS设备
    protected JSONObject pushByTags(List<String> tags, String tagOp, MessageIOS message) {
        if (XinGePush.current_environment == XinGePush.ENVIRONMENT_TEST || !IOS_PUSH_ENABLED) {
            return new JSONObject("{\"result\":{\"push_id\":\"=====pushByTags_IOS_TEST=====\"},\"ret_code\":0}");
        }
        JSONObject ret = ios_xinge.pushTags(0, tags, tagOp, message, IOS_TYPE);
        return ret;
    }

    // 设置标签
    protected JSONObject batchSetTags(List<TagTokenPair> pairs, Device device) {
        if (device == Device.ios && (XinGePush.current_environment == XinGePush.ENVIRONMENT_TEST || !IOS_PUSH_ENABLED)) {
            return new JSONObject("{\"result\":{\"push_id\":\"=====batchSetTags_IOS_TEST=====\"},\"ret_code\":0}");
        } else if (device == Device.android && (XinGePush.current_environment == XinGePush.ENVIRONMENT_TEST || !ANDROID_PUSH_ENABLE)) {
            return new JSONObject("{\"result\":{\"push_id\":\"=====batchSetTags_ANDROID_TEST=====\"},\"ret_code\":0}");
        }
        JSONObject ret;
        switch (device) {
            case ios:
                ret = ios_xinge.BatchSetTag(pairs);
                break;
            case android:
                ret = android_xinge.BatchSetTag(pairs);
                break;
            default:
                ret = null;
        }
        return ret;
    }

    // 设置标签
    protected JSONObject batchDelTags(List<TagTokenPair> pairs, Device device) {
        if (device == Device.ios && (XinGePush.current_environment == XinGePush.ENVIRONMENT_TEST || !IOS_PUSH_ENABLED)) {
            return new JSONObject("{\"result\":{\"push_id\":\"=====batchDelTags_IOS_TEST=====\"},\"ret_code\":0}");
        } else if (device == Device.android && (XinGePush.current_environment == XinGePush.ENVIRONMENT_TEST || !ANDROID_PUSH_ENABLE)) {
            return new JSONObject("{\"result\":{\"push_id\":\"=====batchDelTags_ANDROID_TEST=====\"},\"ret_code\":0}");
        }
        JSONObject ret;
        switch (device) {
            case ios:
                ret = ios_xinge.BatchDelTag(pairs);
                break;
            case android:
                ret = android_xinge.BatchDelTag(pairs);
                break;
            default:
                ret = null;
        }
        return ret;
    }

    public JSONObject queryDeviceCount(Device device){
        if(device == Device.android){
            return android_xinge.queryDeviceCount();
        }else{
            return ios_xinge.queryDeviceCount();
        }
    }

    public JSONObject queryTags(Device device){
        if(device == Device.android){
            return android_xinge.queryTags(0, 1000);
        }else{
            return ios_xinge.queryTags(0, 1000);
        }
    }

    /*==================华丽分割线，上面都不是我写的~~~====================================================================*/

    private MessageIOS createWeiboMessageIOS(WeiboBean bean) {
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

    private Message createWeiboMessage(WeiboBean bean) {
        Message mess = new Message();
        mess.setExpireTime(86400);
        mess.setTitle(bean.getWbname());
        mess.setType(Message.TYPE_NOTIFICATION);
        String text = bean.getRawText();
        String type = bean.getFrom_web();
        if (type.equalsIgnoreCase("WEIBO")) {
            if (text != null && text.length() > 78) {
                text = text.substring(0, 78);
            }
            if (text != null) {
                mess.setContent(text + (text.length() >= 78 ? "..." : "") + "详情点击>>\n微博发表时间：" + sdf.format(bean.getUpdate_time()));
            } else {
                mess.setContent("详情点击进入>>\n微博发表时间：" + sdf.format(bean.getUpdate_time()));
            }
        } else if (type.equalsIgnoreCase("TWITTER")) {
            if (text != null && text.length() > 158) {
                text = text.substring(0, 158);
            }
            if (text != null) {
                mess.setContent(text + (text.length() >= 158 ? "..." : "") + "详情点击>>\nTwitter发表时间：" + sdf.format(bean.getUpdate_time()));
            } else {
                mess.setContent("详情点击进入>>\nTwitter发表时间：" + sdf.format(bean.getUpdate_time()));
            }
        }
        Map<String, Object> custom = new HashMap<String, Object>();
        custom.put("imageurl", bean.getImageurl());
        custom.put("type", bean.getFrom_web());
        custom.put("weiboid", bean.getWbid());
        custom.put("userid", bean.getUid());
        mess.setCustom(custom);
        return mess;
    }

    private MessageIOS createNewsMessageIOS(NewsBean bean) {
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

    private Message createNewsMessage(NewsBean bean) {
        Message mess = new Message();
        mess.setExpireTime(86400);
        mess.setType(Message.TYPE_NOTIFICATION);
        mess.setTitle(bean.getTitle());
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
            mess.setContent(text + "\n新闻发表时间：" + sdf.format(bean.getUpdate_time())
                    + "\n来自：" + bean.getWebname());
        } else {
            mess.setContent(text + "\n新闻发表时间：" + sdf.format(bean.getUpdate_time()));
        }
        Map<String, Object> custom = new HashMap<String, Object>();
        custom.put("icon", bean.getWebicon());
        custom.put("type", "NEWS");
        custom.put("newsurl", bean.getUrl());
        mess.setCustom(custom);
        return mess;
    }

    private MessageIOS createCoinMessageIOS(CoinBean bean) {
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
        alert.put("url", bean.getUrl());
        aps.put("sound", "beep.wav");
        aps.put("alert", alert);
        mess.setAlert(alert);
        aps.put("badge", 1);
        //aps.put("content-available", 1);
        obj.put("aps", aps);
        mess.setRaw(obj.toString());
        System.out.println(obj.toString(4));
        return mess;
    }

    private Message createCoinMessage(CoinBean bean) {
        Message mess = new Message();
        mess.setExpireTime(86400);
        mess.setType(Message.TYPE_NOTIFICATION);
        String title = bean.getChinesename() + "/" + bean.getEnglishname();
        if (bean.getRank() > 0) {
            title += "            排名：" + bean.getRank();
        }
        mess.setTitle(title);
        mess.setContent("涨跌幅：" + bean.getPercent().setScale(8,
                BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString().toString()
                + "%\n当前价格：" + bean.getPrice() + getMarketNameType(bean.getMarket_type()) + "\n来自：" + bean.getPlatform() +
                "\n点击获取更多虚拟币信息>>");
        Map<String, Object> custom = new HashMap<String, Object>();
        custom.put("type", "COIN");
        custom.put("englishname", bean.getEnglishname());
        CoinInfoBean infoBean = bean.getInfoBean();
        if (infoBean != null && infoBean.getImageurl() != null) {
            custom.put("image", infoBean.getImageurl());
        }
        custom.put("platform", bean.getPlatform());
        if (bean.getRank() > 0) {
            custom.put("rank", bean.getRank());
        }
        custom.put("url", bean.getUrl());
        mess.setCustom(custom);
        System.out.println(mess.toJson());
        return mess;
    }

    private List<String> createTagByCoinBean(CoinBean bean){
        List<String> tags = new ArrayList<String>();
        tags.add(bean.getCoin_id());
        return tags;
    }

    /*===========================================================================================*/

    public synchronized JSONObject pushSyncWeiboToAll(WeiboBean bean) {
        MessageIOS messageIOS = createWeiboMessageIOS(bean);
        Message message = createWeiboMessage(bean);
        JSONObject json = new JSONObject();
        json.put("android", this.pushAllDevice(message));
        json.put("ios", this.pushAllDevice(messageIOS, IOS_TYPE));
        return json;
    }

    public void pushAsyncWeiboToAll(WeiboBean bean) {
        pushAsyncWeiboToAll(bean, new DefaultAsyncXinGePushListener());
    }

    public void pushAsyncWeiboToAll(WeiboBean bean, AsyncXinGePushListener listener) {
        MessageIOS messageIOS = createWeiboMessageIOS(bean);
        PushMethodInvoker invoker = new PushAllDevicesInvoker(this,
                PushMethodInvoker.WEIBO_MESSAGE, listener, messageIOS, PUSHALL_IOS_TAG);
        queue.add(invoker);
        Message message = createWeiboMessage(bean);
        invoker = new PushAllDevicesInvoker(this,
                PushMethodInvoker.WEIBO_MESSAGE, listener, message, PUSHALL_ANDROID_TAG);
        queue.add(invoker);
    }

    public synchronized JSONObject pushSyncNewsToAll(NewsBean bean) {
        MessageIOS messageIOS = createNewsMessageIOS(bean);
        Message message = createNewsMessage(bean);
        JSONObject json = new JSONObject();
        json.put("android", this.pushAllDevice(message));
        json.put("ios", this.pushAllDevice(messageIOS, IOS_TYPE));
        return json;
    }

    public void pushASyncNewsToAll(NewsBean bean) {
        pushASyncNewsToAll(bean, new DefaultAsyncXinGePushListener());
    }

    public void pushASyncNewsToAll(NewsBean bean, AsyncXinGePushListener listener) {
        MessageIOS messageIOS = createNewsMessageIOS(bean);
        PushMethodInvoker invoker = new PushAllDevicesInvoker(
                this, PushMethodInvoker.NEWS_MESSAGE, listener, messageIOS, PUSHALL_IOS_TAG);
        queue.add(invoker);
        Message message = createNewsMessage(bean);
        invoker = new PushAllDevicesInvoker(
                this, PushMethodInvoker.NEWS_MESSAGE, listener, message, PUSHALL_ANDROID_TAG);
        queue.add(invoker);
    }

    public synchronized JSONObject pushSyncCoinToAll(CoinBean bean) {
        MessageIOS messageIOS = createCoinMessageIOS(bean);
        Message message = createCoinMessage(bean);
        JSONObject json = new JSONObject();
        json.put("android", this.pushAllDevice(message));
        json.put("ios", this.pushAllDevice(messageIOS, IOS_TYPE));
        return json;
    }


    public void pushASyncCoinToAll(CoinBean bean) {
        pushASyncCoinToAll(bean, new DefaultAsyncXinGePushListener());
    }

    public void pushASyncCoinToAll(CoinBean bean, AsyncXinGePushListener listener) {
        MessageIOS messageIOS = createCoinMessageIOS(bean);
        PushMethodInvoker invoker = new PushAllDevicesInvoker(
                this, PushMethodInvoker.COIN_MESSAGE, listener, messageIOS, PUSHALL_IOS_TAG);
        queue.add(invoker);
        Message message = createCoinMessage(bean);
        invoker = new PushAllDevicesInvoker(
                this, PushMethodInvoker.COIN_MESSAGE, listener, message, PUSHALL_ANDROID_TAG);
        queue.add(invoker);
    }

    public void pushASyncCoinByTag(CoinBean bean) {
        pushASyncCoinByTag(bean, new DefaultAsyncXinGePushListener());
    }

    public void pushASyncCoinByTag(CoinBean bean, AsyncXinGePushListener listener) {
        MessageIOS messageIOS = createCoinMessageIOS(bean);
        List<String> tags = createTagByCoinBean(bean);
        PushMethodInvoker invoker = new PushByTagsInvoker(
                this, PushMethodInvoker.COIN_MESSAGE, listener, messageIOS, tags, "OR", PUSHTAGS_IOS_TAG);
        queue.add(invoker);
        Message message = createCoinMessage(bean);
        invoker = new PushByTagsInvoker(
                this, PushMethodInvoker.COIN_MESSAGE, listener, message, tags, "OR", PUSHALL_ANDROID_TAG);
        queue.add(invoker);
    }

    public synchronized JSONObject batchSetTagsSync(String token, String tag, Device device) {
        List<TagTokenPair> pairs = new ArrayList<TagTokenPair>();
        pairs.add(new TagTokenPair(tag, token));
        return this.batchSetTags(pairs, device);
    }

    public synchronized JSONObject batchDelTagsSync(String token, String tag, Device device) {
        List<TagTokenPair> pairs = new ArrayList<TagTokenPair>();
        pairs.add(new TagTokenPair(tag, token));
        return this.batchDelTags(pairs, device);
    }

    public synchronized JSONObject pushSingleDevice(String token, CoinBean bean, Device device){
        if(device == Device.android) {
            Message message = createCoinMessage(bean);
            return this.pushSingleDevice(token, message);
        }else{
            MessageIOS messageIOS = createCoinMessageIOS(bean);
            return this.pushSingleDevice(token, messageIOS);
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        System.out.println(XingeApp.pushAllAndroid(ANDROID_ACCESS_ID, ANDROID_SECRET_KEY, "标题", "大家好!"));
        XinGePush push = XinGePush.getInstance();
        for (int i = 0; i < 1; i++) {
            Message mess = new Message();

            mess.setExpireTime(86400);
            mess.setTitle("推送测试");
            mess.setContent("content");
            mess.setType(Message.TYPE_NOTIFICATION);/*
            mess.setStyle(new Style(0, 1, 1, 0, 0));
            ClickAction action = new ClickAction();
            action.setActionType(ClickAction.TYPE_URL);
            action.setUrl("http://xg.qq.com");
            action.setConfirmOnUrl(1);
            mess.setAction(action);*/

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
            System.out.println(mess);
            System.out.println(push.pushAllDevice(mess));

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
