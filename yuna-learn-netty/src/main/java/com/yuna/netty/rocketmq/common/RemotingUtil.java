package com.yuna.netty.rocketmq.common;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by yuna430 on 2018/3/19 0019.
 */
public class RemotingUtil {
    private static final Logger log = LoggerFactory.getLogger(RemotingUtil.class);

    public static SocketAddress string2SocketAddress(final String addr) {
        String[] s = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
        return isa;
    }

    public static void closeChannel(Channel channel) {
        final String addrRemote = parseChannelRemoteAddr(channel);
        channel.close().addListener(
                (ChannelFutureListener) future -> log.info(
                        "closeChannel: close the connection to remote address[{}] result: {}",
                        addrRemote,
                        future.isSuccess()));
    }

    public static String parseChannelRemoteAddr(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }
            return addr;
        }
        return "";
    }
}
