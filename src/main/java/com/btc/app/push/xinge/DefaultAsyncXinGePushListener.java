package com.btc.app.push.xinge;

import com.tencent.xinge.MessageIOS;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultAsyncXinGePushListener implements AsyncXinGePushListener {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Logger logger = Logger.getLogger(DefaultAsyncXinGePushListener.class);

    public void pushSuccess(PushMethodInvoker invoker, JSONObject object) {
        //logger.info("Message: "+invoker);
        logger.info("Push Success To Server: " + object.toString() + "\nMessage: " + invoker);
        //logger.info("=============================================");
        //System.out.println(sdf.format(new Date()));
    }

    public void pushException(PushMethodInvoker invoker, Exception e) {
        logger.info(e.getMessage() + "\nMessage:" + invoker);
    }
}
