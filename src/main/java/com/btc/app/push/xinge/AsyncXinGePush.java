package com.btc.app.push.xinge;


import com.tencent.xinge.XingeApp;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AsyncXinGePush implements Runnable {
    private Logger logger = Logger.getLogger(AsyncXinGePush.class);
    private static final int MAX_PUSH_SIZE_PER_HOUR = 30;
    private static final long NORMAL_SLEEP_TIME_WHEN_NODATA = 1000L;
    private static final long TIME_INTERVAL = 3600000;
    private BlockingQueue<PushMethodInvoker> queue;
    private BlockingQueue<PushMethodInvoker> waitQueue
            = new LinkedBlockingQueue<PushMethodInvoker>();
    private static BlockingQueue<PushMethodInvoker> sendQueue
            = new PriorityBlockingQueue<PushMethodInvoker>(MAX_PUSH_SIZE_PER_HOUR);

    public AsyncXinGePush(BlockingQueue<PushMethodInvoker> queue) {
        this.queue = queue;
    }

    public void run() {
        while (true) {
            PushMethodInvoker invoker = null;
            AsyncXinGePushListener listener = null;
            try {
                long ahourago = System.currentTimeMillis() - TIME_INTERVAL;
                while (!sendQueue.isEmpty() && sendQueue.peek().getCreateTime() < ahourago) {
                    sendQueue.poll(1, TimeUnit.SECONDS);
                }
                if (sendQueue.size() < MAX_PUSH_SIZE_PER_HOUR && !waitQueue.isEmpty()) {
                    invoker = waitQueue.poll(1, TimeUnit.SECONDS);
                }
                if (invoker == null) {
                    invoker = queue.poll(1, TimeUnit.SECONDS);
                }
                if (invoker == null) {
                    if (sendQueue.size() >= MAX_PUSH_SIZE_PER_HOUR && queue.isEmpty()) {
                        long time_sleep = sendQueue.peek().getCreateTime() + TIME_INTERVAL - System.currentTimeMillis();
                        if (time_sleep > 0) {
                            Thread.sleep(time_sleep > NORMAL_SLEEP_TIME_WHEN_NODATA ? NORMAL_SLEEP_TIME_WHEN_NODATA : time_sleep);
                        }
                    } else {
                        Thread.sleep(NORMAL_SLEEP_TIME_WHEN_NODATA);
                    }
                    continue;
                }
                listener = invoker.getListener();
                if (sendQueue.size() >= MAX_PUSH_SIZE_PER_HOUR) {
                    if (invoker.getTYPE() != PushMethodInvoker.COIN_MESSAGE) {
                        waitQueue.add(invoker);
                        logger.info("You have already Pushed 30 Messages in last One hour," +
                                " Waiting in Queue about Push:" + invoker);
                    } else {
                        logger.info("You have already Pushed 30 Messages in last One hour, " +
                                "Cancelled Coin Push:" + invoker);
                    }
                } else {
                    JSONObject json = null;
                    if (XinGePush.current_environment == XinGePush.ENVIRONMENT_PRODUCT) {
                        json = invoker.invoke();
                    } else {
                        json = new JSONObject("{\"result\":{\"push_id\":\"=====ONLY_TEST=====\"},\"ret_code\":0}");
                    }
                    int ret_code = json.getInt("ret_code");
                    if (ret_code == 0) {
                        invoker.setCreateTime(System.currentTimeMillis());
                        sendQueue.add(invoker);
                    }
                    listener.pushSuccess(invoker, json);
                }
                if (sendQueue.size() >= MAX_PUSH_SIZE_PER_HOUR && queue.isEmpty()) {
                    long time_sleep = sendQueue.peek().getCreateTime() + TIME_INTERVAL - System.currentTimeMillis();
                    if (time_sleep > 0) {
                        Thread.sleep(time_sleep > NORMAL_SLEEP_TIME_WHEN_NODATA ? NORMAL_SLEEP_TIME_WHEN_NODATA : time_sleep);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("Thread was Interrupted.");
                break;
            } catch (Exception e) {
                if (listener != null) {
                    listener.pushException(invoker, e);
                } else {
                    logger.error("Get Null Listener.");
                }
            }
        }

    }
}
