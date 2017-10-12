package com.btc.app.push.xinge;

import com.tencent.xinge.TagTokenPair;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

import java.util.List;

public class BatchDelTagsInvoker extends PushMethodInvoker {

    private final List<TagTokenPair> pairs;
    public BatchDelTagsInvoker(XingeApp xinge, int TYPE,
                               AsyncXinGePushListener listener,
                               List<TagTokenPair> pairs) {
        super(xinge, TYPE, listener);
        this.pairs = pairs;
    }
    public JSONObject invoke() {
        return xinge.BatchDelTag(pairs);
    }
}
