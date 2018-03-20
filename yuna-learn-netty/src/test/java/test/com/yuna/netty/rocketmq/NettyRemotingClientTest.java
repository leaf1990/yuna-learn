package test.com.yuna.netty.rocketmq;

import com.yuna.netty.rocketmq.net.NettyClientConfig;
import com.yuna.netty.rocketmq.net.NettyRemotingClient;
import com.yuna.netty.rocketmq.net.NettyRemotingServer;
import com.yuna.netty.rocketmq.protocol.RemotingCommand;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public class NettyRemotingClientTest {

    public static void main(String[] args) {
        NettyRemotingClient nettyRemotingClient = new NettyRemotingClient(new NettyClientConfig());
        nettyRemotingClient.start();
        final Charset utf8 = Charset.forName("UTF-8");

        Scanner in = new Scanner(System.in);
        try {
            String line = null;
            while (!"quit".equals(line = in.nextLine())) {
                RemotingCommand command = new RemotingCommand();
                command.setBody(line.getBytes(utf8));
                RemotingCommand remotingCommand = nettyRemotingClient.invokeSync("127.0.0.1:8888", command, 5000);
                System.out.println(remotingCommand);
                nettyRemotingClient.invokeAsync("127.0.0.1:8888", command, 5000, (future) -> System.out.println(future.getResponseCommand()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
