package com.yuna.netty.rocketmq.net;

import com.yuna.common.thread.ThreadFactoryImpl;
import com.yuna.netty.rocketmq.ChannelEventListener;
import com.yuna.netty.rocketmq.InvokeCallback;
import com.yuna.netty.rocketmq.RemotingServer;
import com.yuna.netty.rocketmq.common.Pair;
import com.yuna.netty.rocketmq.common.RemotingUtil;
import com.yuna.netty.rocketmq.exception.RemotingSendRequestException;
import com.yuna.netty.rocketmq.exception.RemotingTimeoutException;
import com.yuna.netty.rocketmq.exception.RemotingTooMuchRequestException;
import com.yuna.netty.rocketmq.protocol.RemotingCommand;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Netty服务端
 * <p>
 * Created by yuna430 on 2018/3/19 0019.
 */
public class NettyRemotingServer extends NettyRemotingAbstract implements RemotingServer {
    private static final Logger log = LoggerFactory.getLogger(NettyRemotingServer.class);
    private final NettyServerConfig nettyServerConfig;
    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup eventLoopGroupSelector;
    private final EventLoopGroup eventLoopGroupBoss;
    private final Timer timer = new Timer("ServerHouseKeepingService", true);
    private final ChannelEventListener channelEventListener;
    private final ExecutorService publicExecutor;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private int port;

    public NettyRemotingServer() {
        this(new NettyServerConfig());
    }


    public NettyRemotingServer(NettyServerConfig nettyServerConfig) {
        this(nettyServerConfig, null);
    }

    public NettyRemotingServer(NettyServerConfig nettyServerConfig, ChannelEventListener channelEventListener) {
        super(nettyServerConfig.getServerOnewaySemaphoreValue(), nettyServerConfig.getServerAsyncSemaphoreValue());

        this.nettyServerConfig = nettyServerConfig;
        this.channelEventListener = channelEventListener;
        this.serverBootstrap = new ServerBootstrap();
        this.eventLoopGroupBoss = new NioEventLoopGroup(1,
                new ThreadFactoryImpl("NettyBoss_"));
        int selectorTotalThread = nettyServerConfig.getServerSelectorThreads();
        this.eventLoopGroupSelector = new NioEventLoopGroup(selectorTotalThread,
                new ThreadFactoryImpl("NettyServerNIOSelector_" + selectorTotalThread + "_"));

        int publicThreadNums = nettyServerConfig.getServerCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }
        this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactoryImpl("NettyClientPublicExecutor_"));
    }

    /**
     * 启动Netty server
     */
    @Override
    public void start() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyServerConfig.getServerWorkerThreads(),
                new ThreadFactoryImpl("NettyServerCodecThread_"));

        this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.ALLOCATOR.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
                .option(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
                .localAddress(new InetSocketAddress(nettyServerConfig.getListenPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(
                                defaultEventExecutorGroup,
                                new NettyDecoder(),
                                new NettyEncoder(),
                                new IdleStateHandler(0, 0, nettyServerConfig.getServerChannelMaxIdleTimeSeconds()),
                                new NettyServerHandler(),
                                new NettyConnetManageHandler()
                        );
                    }
                });

        try {
            ChannelFuture sync = this.serverBootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
            this.port = addr.getPort();
            log.info("NettyRemotionServer start ok, port=" + port);
        } catch (InterruptedException e1) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
        }

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    NettyRemotingServer.this.scanResponseTable();
                } catch (Exception e) {
                    log.error("scanResponseTable exception", e);
                }
            }
        }, 1000 * 3, 1000);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void registerProcessor(int requestCode, NettyRequestProcessor processor, ExecutorService executor) {
        ExecutorService executorThis = executor;
        if (null == executor) {
            executorThis = this.publicExecutor;
        }

        Pair<NettyRequestProcessor, ExecutorService> pair = new Pair<>(processor, executorThis);
        this.processorTable.put(requestCode, pair);
    }

    @Override
    public Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(int requestCode) {
        return processorTable.get(requestCode);
    }

    @Override
    public void registerDefaultProcessor(NettyRequestProcessor processor, ExecutorService executor) {
        this.defaultRequestProcessor = new Pair<>(processor, executor);
    }

    @Override
    public int localListenPort() {
        return this.port;
    }


    @Override
    public RemotingCommand invokeSync(final Channel channel, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException {
        return this.invokeSyncImpl(channel, request, timeoutMillis);
    }

    @Override
    public void invokeAsync(Channel channel, RemotingCommand request, long timeoutMillis, InvokeCallback invokeCallback)
            throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException {
        this.invokeAsyncImpl(channel, request, timeoutMillis, invokeCallback);
    }

    @Override
    public void invokeOneway(Channel channel, RemotingCommand request, long timeoutMillis) throws InterruptedException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException {
        this.invokeOnewayImpl(channel, request, timeoutMillis);
    }

    @Override
    public ChannelEventListener getChannelEventListener() {
        return channelEventListener;
    }

    @Override
    public ExecutorService getCallbackExecutor() {
        return publicExecutor;
    }

    class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
            processMessageReceived(ctx, msg);
        }
    }

    class NettyConnetManageHandler extends ChannelDuplexHandler {
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY SERVER PIPELINE: channelRegistered {}", remoteAddress);
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY SERVER PIPELINE: channelUnregistered, the channel[{}]", remoteAddress);
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY SERVER PIPELINE: channelActive, the channel[{}]", remoteAddress);
            super.channelActive(ctx);

            if (NettyRemotingServer.this.channelEventListener != null) {
                NettyRemotingServer.this.putNettyEvent(new NettyEvent(NettyEventType.CONNECT, remoteAddress.toString(), ctx.channel()));
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY SERVER PIPELINE: channelInactive, the channel[{}]", remoteAddress);
            super.channelInactive(ctx);

            if (NettyRemotingServer.this.channelEventListener != null) {
                NettyRemotingServer.this.putNettyEvent(new NettyEvent(NettyEventType.CLOSE, remoteAddress.toString(), ctx.channel()));
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent evnet = (IdleStateEvent) evt;
                if (evnet.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
                    log.warn("NETTY SERVER PIPELINE: IDLE exception [{}]", remoteAddress);
                    RemotingUtil.closeChannel(ctx.channel());
                    if (NettyRemotingServer.this.channelEventListener != null) {
                        NettyRemotingServer.this.putNettyEvent(new NettyEvent(NettyEventType.IDLE, remoteAddress.toString(), ctx.channel()));
                    }
                }
            }
            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
            log.warn("NETTY SERVER PIPELINE: exceptionCaught {}", remoteAddress);
            log.warn("NETTY SERVER PIPELINE: exceptionCaught exception.", cause);

            if (NettyRemotingServer.this.channelEventListener != null) {
                NettyRemotingServer.this.putNettyEvent(new NettyEvent(NettyEventType.EXCEPTION, remoteAddress.toString(), ctx.channel()));
            }
            RemotingUtil.closeChannel(ctx.channel());
        }
    }
}
