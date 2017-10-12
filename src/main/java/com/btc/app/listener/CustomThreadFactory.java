package com.btc.app.listener;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {

    private AtomicInteger count = new AtomicInteger(0);

    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        String threadName = r.getClass().getSimpleName() + "-" + SchedulerThread.class.getSimpleName() + count.addAndGet(1);
        t.setName(threadName);
        return t;
    }
}