package com.btc.app.push.xinge;

import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

import java.util.List;

import static com.btc.app.push.xinge.XinGePush.IOS_TYPE;

public class PushByTokenInvoker extends PushMethodInvoker {
    private MessageIOS messageIOS;
    private Message message;
    private final String token;

    public PushByTokenInvoker(XinGePush xinge, int TYPE,
                              AsyncXinGePushListener listener,
                              MessageIOS messageIOS, String token, String messageTag) {
        super(xinge, TYPE, listener, messageTag);
        this.messageIOS = messageIOS;
        this.token = token;
    }

    public PushByTokenInvoker(XinGePush xinge, int TYPE,
                              AsyncXinGePushListener listener,
                              Message message, String token, String messageTag) {
        super(xinge, TYPE, listener, messageTag);
        this.message = message;
        this.token = token;
    }

    public JSONObject invoke() {
        if(messageIOS != null) {
            return xinge.pushSingleDevice(token, messageIOS);
        }else{
            return xinge.pushSingleDevice(token, message);
        }
    }

    public Message getMessage() {
        return message;
    }

    public MessageIOS getMessageIOS() {
        return messageIOS;
    }


    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        if(messageIOS != null) {
            return messageIOS.toJson();
        }else{
            return message.toJson();
        }
    }
}
