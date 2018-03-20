package test.com.yuna.netty.rocketmq;

import com.yuna.netty.rocketmq.net.NettyRemotingServer;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public class NettyRemotingServerTest {
    private final static int ECHO_CODE = 0;

    public static void main(String[] args) throws InterruptedException {
        NettyRemotingServer nettyRemotingServer = new NettyRemotingServer();
        nettyRemotingServer.registerProcessor(ECHO_CODE, new EchoProcessor(), null);
        nettyRemotingServer.start();

        synchronized (NettyRemotingServerTest.class) {
            NettyRemotingServerTest.class.wait();
        }
    }
}
