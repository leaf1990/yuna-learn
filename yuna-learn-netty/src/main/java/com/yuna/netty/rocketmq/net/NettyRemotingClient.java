package com.yuna.netty.rocketmq.net;

import com.yuna.common.thread.ThreadFactoryImpl;
import com.yuna.netty.rocketmq.ChannelEventListener;
import com.yuna.netty.rocketmq.InvokeCallback;
import com.yuna.netty.rocketmq.RemotingClient;
import com.yuna.netty.rocketmq.common.Pair;
import com.yuna.netty.rocketmq.common.RemotingUtil;
import com.yuna.netty.rocketmq.exception.RemotingConnectException;
import com.yuna.netty.rocketmq.exception.RemotingSendRequestException;
import com.yuna.netty.rocketmq.exception.RemotingTimeoutException;
import com.yuna.netty.rocketmq.exception.RemotingTooMuchRequestException;
import com.yuna.netty.rocketmq.protocol.RemotingCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Netty客户端
 * <p>
 * Created by yuna430 on 2018/3/19 0019.
 */
public class NettyRemotingClient extends NettyRemotingAbstract implements RemotingClient {
    private static final Logger log = LoggerFactory.getLogger(NettyRemotingServer.class);
    private static final long LOCK_TIMEOUT_MILLIS = 3000;
    // netty boot
    private final Bootstrap bootstrap = new Bootstrap();
    private final NettyClientConfig nettyClientConfig;
    private final EventLoopGroup eventLoopGroupWorker;
    private final ChannelEventListener channelEventListener;
    private final ExecutorService publicExecutor;

    private final Timer timer = new Timer("ClientHouseKeepingService", true);
    private final ConcurrentHashMap<String /* addr */, ChannelWrapper> channelTables = new ConcurrentHashMap<>();
    private final Lock lockNamesrvChannel = new ReentrantLock();
    private final Lock lockChannelTables = new ReentrantLock();
    private final AtomicReference<List<String>> namesrvAddrList = new AtomicReference<>();
    private final AtomicReference<String> namesrvAddrChoosed = new AtomicReference<>();
    private final AtomicInteger namesrvIndex = new AtomicInteger(initValueIndex());
    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    public NettyRemotingClient(final NettyClientConfig nettyClientConfig) {
        this(nettyClientConfig, null);
    }

    public NettyRemotingClient(final NettyClientConfig nettyClientConfig, final ChannelEventListener channelEventListener) {
        super(nettyClientConfig.getClientOnewaySemaphoreValue(), nettyClientConfig.getClientAsyncSemaphoreValue());

        this.nettyClientConfig = nettyClientConfig;
        this.channelEventListener = channelEventListener;

        int publicThreadNums = nettyClientConfig.getClientCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }
        this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactoryImpl("NettyClientPublicExecutor_"));
        this.eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactoryImpl("NettyClientSelector_"));
    }

    private static int initValueIndex() {
        Random r = new Random();
        return Math.abs(r.nextInt() % 999) % 999;
    }

    @Override
    public void start() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyClientConfig.getClientWorkerThreads(),
                new ThreadFactoryImpl("NettyClientWorkerThread_"));

        this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)//
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis())
                .option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
                .option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                defaultEventExecutorGroup,
                                new NettyEncoder(),
                                new NettyDecoder(),
                                new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds()),
                                new NettyConnectManageHandler(),
                                new NettyClientHandler());
                    }
                });

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    NettyRemotingClient.this.scanResponseTable();
                } catch (Exception e) {
                    log.error("scanResponseTable exception", e);
                }
            }
        }, 1000 * 3, 1000);
    }

    @Override
    public void shutdown() {
        try {
            this.timer.cancel();

            for (ChannelWrapper cw : this.channelTables.values()) {
                this.closeChannel(null, cw.getChannel());
            }

            this.channelTables.clear();

            this.eventLoopGroupWorker.shutdownGracefully();

            if (this.nettyEventExecuter != null) {
                this.nettyEventExecuter.stop();// TODO see ServiceThread
            }

            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            log.error("NettyRemotingClient shutdown exception, ", e);
        }

        if (this.publicExecutor != null) {
            try {
                this.publicExecutor.shutdown();
            } catch (Exception e) {
                log.error("NettyRemotingServer shutdown exception, ", e);
            }
        }
    }

    @Override
    public void updateNameServerAddressList(List<String> addrs) {
        List<String> old = this.namesrvAddrList.get();
        boolean update = false;

        if (!addrs.isEmpty()) {
            if (null == old) {
                update = true;
            } else if (addrs.size() != old.size()) {
                update = true;
            } else {
                for (int i = 0; i < addrs.size() && !update; i++) {
                    if (!old.contains(addrs.get(i))) {
                        update = true;
                    }
                }
            }
            if (update) {
                Collections.shuffle(addrs);
                this.namesrvAddrList.set(addrs);
            }
        }
    }

    @Override
    public List<String> getNameServerAddressList() {
        return this.namesrvAddrList.get();
    }

    @Override
    public RemotingCommand invokeSync(String addr, final RemotingCommand request, long timeoutMillis)
            throws InterruptedException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try {
                RemotingCommand response = this.invokeSyncImpl(channel, request, timeoutMillis);
                return response;
            } catch (RemotingSendRequestException e) {
                log.warn("invokeSync: send request exception, so close the channel[{}]", addr);
                this.closeChannel(addr, channel);
                throw e;
            } catch (RemotingTimeoutException e) {
                if (nettyClientConfig.isClientCloseSocketIfTimeout()) {
                    this.closeChannel(addr, channel);
                    log.warn("invokeSync: close socket because of timeout, {}ms, {}", timeoutMillis, addr);
                }
                log.warn("invokeSync: wait response timeout exception, the channel[{}]", addr);
                throw e;
            }
        } else {
            this.closeChannel(addr, channel);
            throw new RemotingConnectException(addr);
        }
    }

    @Override
    public void invokeAsync(String addr, RemotingCommand request, long timeoutMillis, InvokeCallback invokeCallback)
            throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException, RemotingTimeoutException,
            RemotingSendRequestException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try {
                this.invokeAsyncImpl(channel, request, timeoutMillis, invokeCallback);
            } catch (RemotingSendRequestException e) {
                log.warn("invokeAsync: send request exception, so close the channel[{}]", addr);
                this.closeChannel(addr, channel);
                throw e;
            }
        } else {
            this.closeChannel(addr, channel);
            throw new RemotingConnectException(addr);
        }
    }

    @Override
    public void invokeOneway(String addr, RemotingCommand request, long timeoutMillis) throws InterruptedException,
            RemotingConnectException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try {
                this.invokeOnewayImpl(channel, request, timeoutMillis);
            } catch (RemotingSendRequestException e) {
                log.warn("invokeOneway: send request exception, so close the channel[{}]", addr);
                this.closeChannel(addr, channel);
                throw e;
            }
        } else {
            this.closeChannel(addr, channel);
            throw new RemotingConnectException(addr);
        }
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
    public boolean isChannelWriteable(String addr) {
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.isWriteable();
        }
        return true;
    }


    public void closeChannel(final String addr, final Channel channel) {
        if (channel == null) return;
        final String addrRemote = addr == null ? RemotingUtil.parseChannelRemoteAddr(channel) : addr;

        try {
            if (lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    ChannelWrapper prevCW = this.channelTables.get(addrRemote);
                    if (prevCW == null) {
                        log.info("closeChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    } else if (prevCW.getChannel() != channel) {
                        log.info("closeChannel: the channel[{}] has been closed before, and has been created again, nothing to do.", addrRemote);
                        removeItemFromTable = false;
                    }
                    if (removeItemFromTable) {
                        this.channelTables.remove(addrRemote);
                        log.info("closeChannel : the channel[{}] was removed from channel table", addrRemote);
                    }
                } catch (Exception e) {
                    log.error("closeChannel: close the channel exception", e);
                } finally {
                    lockChannelTables.unlock();
                }
            } else {
                log.warn("closeChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (Exception e) {
            log.error("closeChannel exception", e);
        }
    }

    public void closeChannel(final Channel channel) {
        if (null == channel) return;

        try {
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    ChannelWrapper prevCW = null;
                    String addrRemote = null;
                    for (Map.Entry<String, ChannelWrapper> entry : channelTables.entrySet()) {
                        String key = entry.getKey();
                        ChannelWrapper prev = entry.getValue();
                        if (prev.getChannel() != null && prev.getChannel() == channel) {
                            prevCW = prev;
                            addrRemote = key;
                            break;
                        }
                    }
                    if (null == prevCW) {
                        log.info("closeChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    }
                    if (removeItemFromTable) {
                        this.channelTables.remove(addrRemote);
                        log.info("closeChannel: the channel[{}] was removed from channel table", addrRemote);
                        RemotingUtil.closeChannel(channel);
                    }
                } catch (Exception e) {
                    log.error("closeChannel: close the channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                log.warn("closeChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
            log.error("closeChannel exception", e);
        }
    }

    private Channel getAndCreateChannel(final String addr) throws InterruptedException {
        if (null == addr)
            return getAndCreateNameserverChannel();
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }
        return this.createChannel(addr);
    }

    private Channel getAndCreateNameserverChannel() throws InterruptedException {
        String addr = this.namesrvAddrChoosed.get();
        if (addr != null) {
            ChannelWrapper cw = this.channelTables.get(addr);
            if (cw != null && cw.isOK()) {
                return cw.getChannel();
            }
        }
        final List<String> addrList = this.namesrvAddrList.get();
        if (this.lockNamesrvChannel.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
            try {
                addr = this.namesrvAddrChoosed.get();
                if (addr != null) {
                    ChannelWrapper cw = this.channelTables.get(addr);
                    if (cw != null && cw.isOK()) {
                        return cw.getChannel();
                    }
                }
                if (addrList != null && !addrList.isEmpty()) {
                    for (int i = 0; i < addrList.size(); i++) {
                        int index = this.namesrvIndex.incrementAndGet();
                        index = Math.abs(index);
                        index = index % addrList.size();
                        String newAddr = addrList.get(index);

                        this.namesrvAddrChoosed.set(newAddr);
                        Channel channelNew = this.createChannel(newAddr);
                        if (channelNew != null)
                            return channelNew;
                    }
                }
            } catch (Exception e) {
                log.error("getAndCreateNameserverChannel: create name server channel exception", e);
            } finally {
                this.lockNamesrvChannel.unlock();
            }
        } else {
            log.warn("getAndCreateNameserverChannel: try to lock name server, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
        }
        return null;
    }

    private Channel createChannel(final String addr) throws InterruptedException {
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }
        if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
            try {
                boolean createNewConnection = false;
                cw = this.channelTables.get(addr);
                if (cw != null) {
                    if (cw.isOK()) {
                        return cw.getChannel();
                    } else if (!cw.getChannelFuture().isDone()) {
                        createNewConnection = false;
                    } else {
                        this.channelTables.remove(addr);
                        createNewConnection = true;
                    }
                } else {
                    createNewConnection = true;
                }
                if (createNewConnection) {
                    ChannelFuture channelFuture = this.bootstrap.connect(RemotingUtil.string2SocketAddress(addr));
                    log.info("createChannel: begin to connect remote host[{}] asynchronously", addr);
                    cw = new ChannelWrapper(channelFuture);
                    this.channelTables.put(addr, cw);
                }
            } catch (Exception e) {
                log.error("createChannel: create channel exception", e);
            } finally {
                this.lockChannelTables.unlock();
            }
        } else {
            log.warn("createChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
        }
        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            if (channelFuture.awaitUninterruptibly(this.nettyClientConfig.getConnectTimeoutMillis())) {
                if (cw.isOK()) {
                    log.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture.toString());
                    return cw.getChannel();
                } else {
                    log.warn("createChannel: connect remote host[" + addr + "] failed, " + channelFuture.toString(), channelFuture.cause());
                }
            } else {
                log.warn("createChannel: connect remote host[{}] timeout {}ms, {}", addr, this.nettyClientConfig.getConnectTimeoutMillis(), channelFuture.toString());
            }
        }
        return null;
    }

    @Override
    public ChannelEventListener getChannelEventListener() {
        return channelEventListener;
    }

    @Override
    public ExecutorService getCallbackExecutor() {
        return publicExecutor;
    }

    static class ChannelWrapper {
        private final ChannelFuture channelFuture;

        public ChannelWrapper(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        public boolean isOK() {
            return this.channelFuture.channel() != null && this.channelFuture.channel().isActive();
        }

        public boolean isWriteable() {
            return this.channelFuture.channel().isWritable();
        }

        private Channel getChannel() {
            return this.channelFuture.channel();
        }

        public ChannelFuture getChannelFuture() {
            return channelFuture;
        }
    }

    class NettyClientHandler extends SimpleChannelInboundHandler<RemotingCommand> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
            processMessageReceived(ctx, msg);
        }
    }

    class NettyConnectManageHandler extends ChannelDuplexHandler {
        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
                            ChannelPromise promise) throws Exception {
            final String local = localAddress == null ? "UNKNOWN" : localAddress.toString();
            final String remote = remoteAddress == null ? "UNKNOWN" : remoteAddress.toString();
            log.info("NETTY CLIENT PIPELINE: CONNECT  {} => {}", local, remote);

            super.connect(ctx, remoteAddress, localAddress, promise);

            if (NettyRemotingClient.this.channelEventListener != null) {
                NettyRemotingClient.this.putNettyEvent(new NettyEvent(NettyEventType.CONNECT, remote, ctx.channel()));
            }
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY CLIENT PIPELINE: DISCONNECT {}", remoteAddress);
            closeChannel(ctx.channel());
            super.disconnect(ctx, promise);

            if (NettyRemotingClient.this.channelEventListener != null) {
                NettyRemotingClient.this.putNettyEvent(new NettyEvent(NettyEventType.CLOSE, remoteAddress.toString(), ctx.channel()));
            }
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY CLIENT PIPELINE: CLOSE {}", remoteAddress);
            closeChannel(ctx.channel());
            super.close(ctx, promise);

            if (NettyRemotingClient.this.channelEventListener != null) {
                NettyRemotingClient.this.putNettyEvent(new NettyEvent(NettyEventType.CLOSE, remoteAddress.toString(), ctx.channel()));
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
                    log.warn("NETTY CLIENT PIPELINE: IDLE exception [{}]", remoteAddress);
                    closeChannel(ctx.channel());
                    if (NettyRemotingClient.this.channelEventListener != null) {
                        NettyRemotingClient.this
                                .putNettyEvent(new NettyEvent(NettyEventType.IDLE, remoteAddress.toString(), ctx.channel()));
                    }
                }
            }

            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
            log.warn("NETTY CLIENT PIPELINE: exceptionCaught {}", remoteAddress);
            log.warn("NETTY CLIENT PIPELINE: exceptionCaught exception.", cause);
            closeChannel(ctx.channel());
            if (NettyRemotingClient.this.channelEventListener != null) {
                NettyRemotingClient.this.putNettyEvent(new NettyEvent(NettyEventType.EXCEPTION, remoteAddress.toString(), ctx.channel()));
            }
        }
    }
}
