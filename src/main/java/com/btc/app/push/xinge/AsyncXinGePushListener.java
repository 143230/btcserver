package com.btc.app.push.xinge;

import com.tencent.xinge.MessageIOS;
import org.json.JSONObject;

public interface AsyncXinGePushListener {
    public void pushSuccess(PushMethodInvoker invoker, JSONObject object);

    public void pushException(PushMethodInvoker invoker, Exception e);
}
