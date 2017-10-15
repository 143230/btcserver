package com.btc.app.push.xinge;

import com.tencent.xinge.TagTokenPair;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

import java.util.List;

public class BatchDelTagsInvoker extends PushMethodInvoker {

    private final List<TagTokenPair> pairs;
    private final XinGePush.Device device;
    public BatchDelTagsInvoker(XinGePush xinge, int TYPE,
                               AsyncXinGePushListener listener,
                               List<TagTokenPair> pairs, XinGePush.Device device, String messageTag) {
        super(xinge, TYPE, listener, messageTag);
        this.pairs = pairs;
        this.device = device;
    }
    public JSONObject invoke() {
        return xinge.batchDelTags(pairs, device);
    }
}
