package com.yuna.netty.rocketmq.net;

import com.yuna.netty.rocketmq.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by yuna430 on 2018/3/19 0019.
 */
public abstract class NettyRemotingAbstract {

    /**
     * 检测Response信息
     */
    public void scanResponseTable() {

    }

    public void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
        final RemotingCommand cmd = msg;
        if (cmd != null) {
            switch (cmd.getType()) {
                case REQUEST_COMMAND:
                    processRequestCommand(ctx, cmd);
                    break;
                case RESPONSE_COMMAND:
                    processResponseCommand(ctx, cmd);
                    break;
                default:
                    break;
            }
        }
    }

    private void processResponseCommand(ChannelHandlerContext ctx, RemotingCommand msg) {
        System.out.println(msg);
    }

    private void processRequestCommand(ChannelHandlerContext ctx, RemotingCommand msg) {
        System.out.println(msg);
    }
}
