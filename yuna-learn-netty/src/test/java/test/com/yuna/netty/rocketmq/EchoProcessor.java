package test.com.yuna.netty.rocketmq;

import com.yuna.netty.rocketmq.net.NettyRemotingServer;
import com.yuna.netty.rocketmq.net.NettyRequestProcessor;
import com.yuna.netty.rocketmq.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public class EchoProcessor extends NettyRequestProcessor {
    private static final Charset utf8 = Charset.forName("UTF-8");
    private NettyRemotingServer nettyRemotingServer;

    public EchoProcessor(NettyRemotingServer nettyRemotingServer) {
        this.nettyRemotingServer = nettyRemotingServer;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand remotingCommand = RemotingCommand.createResponseCommand(request.getCode(), request.getRemark());
        remotingCommand.setBody(request.getBody());
        return remotingCommand;
    }
}
