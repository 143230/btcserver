package com.btc.app.push.xinge;

import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

import java.util.List;

import static com.btc.app.push.xinge.XinGePush.IOS_TYPE;

public class PushByTokenInvoker extends PushMethodInvoker {
    private final MessageIOS messageIOS;
    private final String token;

    public PushByTokenInvoker(XingeApp xinge, int TYPE,
                              AsyncXinGePushListener listener,
                              MessageIOS messageIOS, String token) {
        super(xinge, TYPE, listener);
        this.messageIOS = messageIOS;
        this.token = token;
    }

    public JSONObject invoke() {
        return xinge.pushSingleDevice(token, messageIOS, IOS_TYPE);
    }

    public MessageIOS getMessageIOS() {
        return messageIOS;
    }


    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return messageIOS.toJson();
    }
}
