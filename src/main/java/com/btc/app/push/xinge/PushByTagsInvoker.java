package com.btc.app.push.xinge;

import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

import java.util.List;

import static com.btc.app.push.xinge.XinGePush.IOS_TYPE;

public class PushByTagsInvoker extends PushMethodInvoker {
    private final MessageIOS messageIOS;
    private final List<String> tags;
    private final String tagOp;

    public PushByTagsInvoker(XingeApp xinge, int TYPE,
                             AsyncXinGePushListener listener,
                             MessageIOS messageIOS,
                             List<String> tags, String tagOp) {
        super(xinge, TYPE, listener);
        this.messageIOS = messageIOS;
        this.tags = tags;
        this.tagOp = tagOp;
    }

    public JSONObject invoke() {
        return xinge.pushTags(0, tags, tagOp, messageIOS, IOS_TYPE);
    }

    public MessageIOS getMessageIOS() {
        return messageIOS;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getTagOp() {
        return tagOp;
    }

    @Override
    public String toString() {
        return messageIOS.toJson();
    }
}
