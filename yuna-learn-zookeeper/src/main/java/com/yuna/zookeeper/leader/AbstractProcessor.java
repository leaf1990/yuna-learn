package com.yuna.zookeeper.leader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yeyayun on 2018/3/14 0014.
 */
public abstract class AbstractProcessor {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private AtomicBoolean init = new AtomicBoolean(false);
    private ScheduledExecutorService executorService;

    public abstract void process();

    public final void init(ScheduledExecutorService executorService) {
        this.executorService = executorService;
        if (init.compareAndSet(false, true)) {
            doInit();
        }
    }

    protected void doInit() {

    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }
}
