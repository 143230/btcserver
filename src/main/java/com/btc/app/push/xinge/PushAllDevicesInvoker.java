package com.btc.app.push.xinge;

import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

import static com.btc.app.push.xinge.XinGePush.IOS_TYPE;

public class PushAllDevicesInvoker extends PushMethodInvoker {
    private MessageIOS messageIOS;

    public PushAllDevicesInvoker(XingeApp xinge, int TYPE,
                                 AsyncXinGePushListener listener,
                                 MessageIOS messageIOS) {
        super(xinge, TYPE, listener);
        this.messageIOS = messageIOS;
    }

    public JSONObject invoke() {
        return xinge.pushAllDevice(0, messageIOS, IOS_TYPE);
        //return new JSONObject("{\"result\":{\"push_id\":\"2966453760\"},\"ret_code\":0}");
    }

    public MessageIOS getMessageIOS() {
        return messageIOS;
    }

    @Override
    public String toString() {
        return messageIOS.toJson();
    }
}
