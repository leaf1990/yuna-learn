package com.yuna.netty.rocketmq.exception;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public class RemotingTooMuchRequestException extends RuntimeException {
    private static final long serialVersionUID = 4326919581254519654L;

    public RemotingTooMuchRequestException(String message) {
        super(message);
    }
}
