package test.com.yuna.netty.rocketmq;

import com.yuna.netty.rocketmq.net.NettyRequestProcessor;
import com.yuna.netty.rocketmq.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public class EchoProcessor extends NettyRequestProcessor {
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand remotingCommand = RemotingCommand.createResponseCommand(request.getCode(), request.getRemark());
        remotingCommand.setBody(request.getBody());
        return remotingCommand;
    }
}
