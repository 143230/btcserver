package com.btc.app.push.weixin;

public class PushToGroupInvoker extends PushWeiXinInvoker {
    private final String groupName;
    private final String message;

    public PushToGroupInvoker(WXAutoChatSpider spider,
                              AsyncWeiXinPushListener listener,
                              String groupName, String message, int TYPE) {
        super(spider, listener, TYPE);
        this.groupName = groupName;
        this.message = message;
    }

    public boolean invoke() {
        //return spider.sendMessage(groupName, message);
        return true;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Group: " + groupName + "\tMessage: " + message;
    }
}
