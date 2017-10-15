package com.btc.app.push.xinge;

import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

import static com.btc.app.push.xinge.XinGePush.IOS_TYPE;

public class PushAllDevicesInvoker extends PushMethodInvoker {
    private MessageIOS messageIOS;
    private Message message;

    public PushAllDevicesInvoker(XinGePush xinge, int TYPE,
                                 AsyncXinGePushListener listener,
                                 MessageIOS messageIOS, String messageTag) {
        super(xinge, TYPE, listener, messageTag);
        this.messageIOS = messageIOS;
    }

    public PushAllDevicesInvoker(XinGePush xinge, int TYPE,
                                 AsyncXinGePushListener listener,
                                 Message message, String messageTag) {
        super(xinge, TYPE, listener, messageTag);
        this.message = message;
    }

    public JSONObject invoke() {
        if(messageIOS != null) {
            return xinge.pushAllDevice(messageIOS, IOS_TYPE);
        }else{
            return xinge.pushAllDevice(message);
        }
        //return new JSONObject("{\"result\":{\"push_id\":\"2966453760\"},\"ret_code\":0}");
    }

    public MessageIOS getMessageIOS() {
        return messageIOS;
    }

    public Message getMessage() {
        return message;
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
