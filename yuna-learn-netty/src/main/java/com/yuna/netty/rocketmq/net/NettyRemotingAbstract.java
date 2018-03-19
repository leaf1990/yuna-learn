package com.yuna.netty.rocketmq.net;

import com.yuna.netty.rocketmq.ChannelEventListener;
import com.yuna.netty.rocketmq.protocol.NettyEvent;
import com.yuna.netty.rocketmq.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuna430 on 2018/3/19 0019.
 */
public abstract class NettyRemotingAbstract {
    private static final Logger log = LoggerFactory.getLogger(NettyRemotingAbstract.class);
    private NettyEventExecuter nettyEventExecuter = new NettyEventExecuter();

    public abstract ChannelEventListener getChannelEventListener();

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

    public void putNettyEvent(NettyEvent nettyEvent) {
        nettyEventExecuter.putNettyEvent(nettyEvent);
    }

    class NettyEventExecuter extends Thread {
        private final LinkedBlockingQueue<NettyEvent> eventQueue = new LinkedBlockingQueue<>();
        private final int maxSize = 10000;

        public void putNettyEvent(final NettyEvent event) {
            if (this.eventQueue.size() <= maxSize) {
                this.eventQueue.add(event);
            } else {
                log.warn("event queue size[{}] enough, so drop this event {}", this.eventQueue.size(), event.toString());
            }
        }

        @Override
        public void run() {
            log.info(getServiceName() + " service start");
            final ChannelEventListener listener = NettyRemotingAbstract.this.getChannelEventListener();
            while (true) {
                try {
                    NettyEvent event = this.eventQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (event != null && listener != null) {
                        switch (event.getType()) {
                            case IDLE:
                                listener.onChannelIdle(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddr(), event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddr(), event.getChannel());
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    log.warn(this.getServiceName() + " service has exception. ", e);
                }
            }
        }

        public String getServiceName() {
            return NettyEventExecuter.class.getSimpleName();
        }
    }
}
