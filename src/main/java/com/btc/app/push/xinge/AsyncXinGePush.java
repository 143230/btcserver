package com.btc.app.push.xinge;


import com.tencent.xinge.XingeApp;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static com.btc.app.push.xinge.XinGePush.*;

public class AsyncXinGePush implements Runnable {
    private Logger logger = Logger.getLogger(AsyncXinGePush.class);
    private static final int MAX_PUSH_SIZE_PER_HOUR = 30;
    private static final long NORMAL_SLEEP_TIME_WHEN_NODATA = 1000L;
    private static final long TIME_INTERVAL = 3600000;
    private final BlockingQueue<PushMethodInvoker> queue;
    private final BlockingQueue<PushMethodInvoker> waitQueue
            = new LinkedBlockingQueue<PushMethodInvoker>();
    private final BlockingQueue<PushMethodInvoker> sendQueue
            = new PriorityBlockingQueue<PushMethodInvoker>(MAX_PUSH_SIZE_PER_HOUR);
    private final Map<String, Integer> tagFrequencyMap = new ConcurrentHashMap<String, Integer>();
    private static final Map<String, Integer> FREQUENCY_LIMIT;

    static {
        FREQUENCY_LIMIT = new HashMap<String, Integer>();
        FREQUENCY_LIMIT.put(PUSHALL_ANDROID_TAG, 30);
        FREQUENCY_LIMIT.put(PUSHALL_IOS_TAG, 30);

        FREQUENCY_LIMIT.put(PUSHTAGS_ANDROID_TAG, 50);
        FREQUENCY_LIMIT.put(PUSHTAGS_IOS_TAG, 50);

        FREQUENCY_LIMIT.put(OTHER_TAG, 30);

    }

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
                    invoker = sendQueue.poll(1, TimeUnit.SECONDS);
                    if (invoker != null) {
                        String tag = invoker.getMessageTag();
                        int count;
                        if (tagFrequencyMap.containsKey(tag)) {
                            count = tagFrequencyMap.get(tag);
                            tagFrequencyMap.put(tag, count - 1);
                        } else {
                            logger.error("count of [" + tag + "] shouldn't be 0. " + tagFrequencyMap.toString());
                        }
                    }
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

                String tag = invoker.getMessageTag();
                int limit;
                if (!FREQUENCY_LIMIT.containsKey(tag)) {
                    logger.info("tag [" + tag + "] not found, using [" + OTHER_TAG + "] limit replace.");
                    limit = FREQUENCY_LIMIT.get(OTHER_TAG);
                } else {
                    limit = FREQUENCY_LIMIT.get(tag);
                }
                int count;
                if (tagFrequencyMap.containsKey(tag)) {
                    count = tagFrequencyMap.get(tag);
                } else {
                    count = 0;
                }

                if (count >= limit) {
                    if (invoker.getTYPE() != PushMethodInvoker.COIN_MESSAGE) {
                        waitQueue.add(invoker);
                        logger.info("You have already Pushed " + count + " Messages in last " +
                                TIME_INTERVAL + "millseconds," +
                                " Waiting in Queue about Push:" + invoker);
                    } else {
                        logger.info("You have already Pushed " + count + " Messages in last " +
                                TIME_INTERVAL + "millseconds," +
                                "Cancelled Coin Push:" + invoker);
                    }
                } else {
                    JSONObject json = invoker.invoke();
                    int ret_code = json.getInt("ret_code");
                    if (ret_code == 0) {
                        invoker.setCreateTime(System.currentTimeMillis());
                        sendQueue.add(invoker);
                        tagFrequencyMap.put(tag, count + 1);
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
