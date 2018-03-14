package com.yuna.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yeyayun on 2018/3/14 0014.
 */
public class ThreadFactoryImpl implements ThreadFactory {
    private AtomicInteger threadCount = new AtomicInteger(0);
    private String prexThreadName;

    public ThreadFactoryImpl(String prexThreadName) {
        this.prexThreadName = prexThreadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, prexThreadName + threadCount.getAndIncrement());
    }
}
