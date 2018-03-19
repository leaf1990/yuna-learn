package com.yuna.netty.rocketmq;

import io.netty.channel.Channel;

/**
 * Created by yuna430 on 2018/3/19 0019.
 */
public interface ChannelEventListener {
    void onChannelIdle(String remoteAddr, Channel channel);

    void onChannelClose(String remoteAddr, Channel channel);

    void onChannelConnect(String remoteAddr, Channel channel);

    void onChannelException(String remoteAddr, Channel channel);
}
