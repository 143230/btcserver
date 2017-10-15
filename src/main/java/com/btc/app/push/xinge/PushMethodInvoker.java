package com.btc.app.push.xinge;

import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

public abstract class PushMethodInvoker implements Comparable<PushMethodInvoker> {
    protected XinGePush xinge;
    protected final int TYPE;
    protected long createTime;
    protected final AsyncXinGePushListener listener;

    public static final int WEIBO_MESSAGE = 0;
    public static final int COIN_MESSAGE = 1;
    public static final int NEWS_MESSAGE = 2;
    public static final int BATCH_SET_TAG = 3;
    public static final int BATCH_DEL_TAG = 4;

    private final String messageTag;

    public PushMethodInvoker(XinGePush xinge, int TYPE, AsyncXinGePushListener listener, String messageTag) {
        this.xinge = xinge;
        this.messageTag = messageTag;
        this.TYPE = TYPE;
        this.listener = listener;
        this.createTime = System.currentTimeMillis();
    }

    public AsyncXinGePushListener getListener() {
        return listener;
    }

    public int getTYPE() {

        return TYPE;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int compareTo(PushMethodInvoker o) {
        return ((Long) createTime).compareTo(o.getCreateTime());
    }

    public abstract JSONObject invoke();

    public String getMessageTag() {
        return messageTag;
    }
}
