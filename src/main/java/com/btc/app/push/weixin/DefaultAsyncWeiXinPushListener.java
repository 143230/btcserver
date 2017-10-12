package com.btc.app.push.weixin;

import com.btc.app.push.xinge.AsyncXinGePushListener;
import com.btc.app.push.xinge.PushMethodInvoker;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class DefaultAsyncWeiXinPushListener implements AsyncWeiXinPushListener {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Logger logger = Logger.getLogger(DefaultAsyncWeiXinPushListener.class);

    public void pushSuccess(PushWeiXinInvoker invoker, boolean object) {
        //logger.info("Message: "+invoker);
        logger.info("Push Success To Server: " + object + "\nMessage: " + invoker);
        //logger.info("=============================================");
        //System.out.println(sdf.format(new Date()));
    }

    public void pushException(PushWeiXinInvoker invoker, Exception e) {
        logger.info(e.getMessage() + "\nMessage:" + invoker);
    }
}
