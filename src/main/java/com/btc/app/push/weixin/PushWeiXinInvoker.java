package com.btc.app.push.weixin;

public abstract class PushWeiXinInvoker implements Comparable<PushWeiXinInvoker> {
    protected WXAutoChatSpider spider;
    protected long createTime;
    protected final int TYPE;
    protected final AsyncWeiXinPushListener listener;

    public static final int WEIBO_MESSAGE = 0;
    public static final int COIN_MESSAGE = 1;
    public static final int NEWS_MESSAGE = 2;

    public PushWeiXinInvoker(WXAutoChatSpider spider, AsyncWeiXinPushListener listener, int TYPE) {
        this.spider = spider;
        this.listener = listener;
        this.TYPE = TYPE;
        this.createTime = System.currentTimeMillis();
    }

    public AsyncWeiXinPushListener getListener() {
        return listener;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int compareTo(PushWeiXinInvoker o) {
        return ((Long) createTime).compareTo(o.getCreateTime());
    }

    public int getTYPE() {
        return TYPE;
    }

    public abstract boolean invoke();
}
