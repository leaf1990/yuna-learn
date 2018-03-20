package com.yuna.netty.rocketmq.net;

import com.yuna.netty.rocketmq.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public abstract class NettyRequestProcessor {
    public abstract RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception;

    public boolean rejectRequest() {
        return false;
    }
}
