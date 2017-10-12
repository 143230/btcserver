package com.btc.app.push.weixin;

import com.btc.app.push.xinge.PushMethodInvoker;
import org.json.JSONObject;

public interface AsyncWeiXinPushListener {
    public void pushSuccess(PushWeiXinInvoker invoker, boolean object);

    public void pushException(PushWeiXinInvoker invoker, Exception e);
}
