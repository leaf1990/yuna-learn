package com.yuna.netty.rocketmq.exception;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public class RemotingConnectException extends RuntimeException {
    private static final long serialVersionUID = -5565366231695911316L;

    public RemotingConnectException(String addr) {
        this(addr, null);
    }

    public RemotingConnectException(String addr, Throwable cause) {
        super("connect to <" + addr + "> failed", cause);
    }
}
