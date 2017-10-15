package com.btc.app.push.xinge;

import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

import java.util.List;

import static com.btc.app.push.xinge.XinGePush.IOS_TYPE;

public class PushByTagsInvoker extends PushMethodInvoker {
    private MessageIOS messageIOS;
    private Message message;
    private final List<String> tags;
    private final String tagOp;

    public PushByTagsInvoker(XinGePush xinge, int TYPE,
                             AsyncXinGePushListener listener,
                             MessageIOS messageIOS,
                             List<String> tags, String tagOp, String messageTag) {
        super(xinge, TYPE, listener, messageTag);
        this.messageIOS = messageIOS;
        this.tags = tags;
        this.tagOp = tagOp;
    }

    public PushByTagsInvoker(XinGePush xinge, int TYPE,
                             AsyncXinGePushListener listener,
                             Message message,
                             List<String> tags, String tagOp, String messageTag) {
        super(xinge, TYPE, listener, messageTag);
        this.message = message;
        this.tags = tags;
        this.tagOp = tagOp;
    }

    public JSONObject invoke() {
        if(messageIOS != null) {
            return xinge.pushByTags(tags, tagOp, messageIOS);
        }else {
            return xinge.pushByTags(tags,tagOp, message);
        }
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
        if(messageIOS != null) {
            return messageIOS.toJson();
        }else{
            return message.toJson();
        }
    }
}
