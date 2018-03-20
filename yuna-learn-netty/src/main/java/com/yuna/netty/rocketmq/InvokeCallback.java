package com.yuna.netty.rocketmq;

import com.yuna.netty.rocketmq.net.ResponseFuture;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public interface InvokeCallback {
    void operationComplete(final ResponseFuture responseFuture);
}
