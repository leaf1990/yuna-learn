package com.yuna.netty.rocketmq.exception;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public class RemotingSendRequestException extends RuntimeException {
    private static final long serialVersionUID = 5391285827332471674L;

    public RemotingSendRequestException(String addr) {
        this(addr, null);
    }

    public RemotingSendRequestException(String addr, Throwable cause) {
        super("send request to <" + addr + "> failed", cause);
    }
}
