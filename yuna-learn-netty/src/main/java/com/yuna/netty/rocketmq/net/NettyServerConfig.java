package com.yuna.netty.rocketmq.net;

/**
 * Created by yuna430 on 2018/3/19 0019.
 */
public class NettyServerConfig {
    private int listenPort = 8888;
    private int serverWorkerThreads = 8;
    private int serverCallbackExecutorThreads = 0;
    private int serverSelectorThreads = 3;
    private int serverOnewaySemaphoreValue = 256;
    private int serverAsyncSemaphoreValue = 64;
    private int serverChannelMaxIdleTimeSeconds = 120;

    private boolean serverPooledByteBufAllocatorEnable = true;
    private int serverSocketSndBufSize = 65535;
    private int serverSocketRcvBufSize = 65535;

    public int getListenPort() {
        return listenPort;
    }

    public NettyServerConfig setListenPort(int listenPort) {
        this.listenPort = listenPort;
        return this;
    }

    public int getServerWorkerThreads() {
        return serverWorkerThreads;
    }

    public NettyServerConfig setServerWorkerThreads(int serverWorkerThreads) {
        this.serverWorkerThreads = serverWorkerThreads;
        return this;
    }

    public int getServerCallbackExecutorThreads() {
        return serverCallbackExecutorThreads;
    }

    public NettyServerConfig setServerCallbackExecutorThreads(int serverCallbackExecutorThreads) {
        this.serverCallbackExecutorThreads = serverCallbackExecutorThreads;
        return this;
    }

    public int getServerSelectorThreads() {
        return serverSelectorThreads;
    }

    public NettyServerConfig setServerSelectorThreads(int serverSelectorThreads) {
        this.serverSelectorThreads = serverSelectorThreads;
        return this;
    }

    public int getServerOnewaySemaphoreValue() {
        return serverOnewaySemaphoreValue;
    }

    public NettyServerConfig setServerOnewaySemaphoreValue(int serverOnewaySemaphoreValue) {
        this.serverOnewaySemaphoreValue = serverOnewaySemaphoreValue;
        return this;
    }

    public int getServerAsyncSemaphoreValue() {
        return serverAsyncSemaphoreValue;
    }

    public NettyServerConfig setServerAsyncSemaphoreValue(int serverAsyncSemaphoreValue) {
        this.serverAsyncSemaphoreValue = serverAsyncSemaphoreValue;
        return this;
    }

    public int getServerChannelMaxIdleTimeSeconds() {
        return serverChannelMaxIdleTimeSeconds;
    }

    public NettyServerConfig setServerChannelMaxIdleTimeSeconds(int serverChannelMaxIdleTimeSeconds) {
        this.serverChannelMaxIdleTimeSeconds = serverChannelMaxIdleTimeSeconds;
        return this;
    }

    public boolean isServerPooledByteBufAllocatorEnable() {
        return serverPooledByteBufAllocatorEnable;
    }

    public NettyServerConfig setServerPooledByteBufAllocatorEnable(boolean serverPooledByteBufAllocatorEnable) {
        this.serverPooledByteBufAllocatorEnable = serverPooledByteBufAllocatorEnable;
        return this;
    }

    public int getServerSocketSndBufSize() {
        return serverSocketSndBufSize;
    }

    public NettyServerConfig setServerSocketSndBufSize(int serverSocketSndBufSize) {
        this.serverSocketSndBufSize = serverSocketSndBufSize;
        return this;
    }

    public int getServerSocketRcvBufSize() {
        return serverSocketRcvBufSize;
    }

    public NettyServerConfig setServerSocketRcvBufSize(int serverSocketRcvBufSize) {
        this.serverSocketRcvBufSize = serverSocketRcvBufSize;
        return this;
    }
}
