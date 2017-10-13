package com.btc.app.listener;

import com.btc.app.statistics.SystemStatistics;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author cuixuan
 */

public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
    private static final Logger logger = Logger.getLogger(CustomRejectedExecutionHandler.class);
    private SystemStatistics statistics = SystemStatistics.getInstance();

    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        // 记录异常
        // 报警处理等
        logger.info(r.getClass().getSimpleName() + " execute rejected, Active Thread Number In Pool: " + executor.getActiveCount());
        BlockingQueue<Runnable> list = executor.getQueue();
        statistics.add("rejected_job",1);
    }
}