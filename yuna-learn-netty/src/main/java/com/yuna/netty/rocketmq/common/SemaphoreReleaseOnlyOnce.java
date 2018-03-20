package com.yuna.netty.rocketmq.common;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public class SemaphoreReleaseOnlyOnce {
    private final AtomicBoolean released = new AtomicBoolean(false);
    private final Semaphore semaphore;

    public SemaphoreReleaseOnlyOnce(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public void release() {
        if (this.semaphore != null) {
            if (this.released.compareAndSet(false, true)) {
                this.semaphore.release();
            }
        }
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}
