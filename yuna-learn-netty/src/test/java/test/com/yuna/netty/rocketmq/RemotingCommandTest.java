package test.com.yuna.netty.rocketmq;

import com.yuna.netty.rocketmq.protocol.RemotingCommand;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by yuna430 on 2018/3/20 0020.
 */
public class RemotingCommandTest {
    public static void main(String[] args) {
        RemotingCommand command = new RemotingCommand();
        command.setBody(new String("hello").getBytes(Charset.forName("UTF-8")));
        ByteBuffer byteBuffer = command.encode();
        System.out.println(byteBuffer);
        System.out.println(command);
        RemotingCommand newCommond = RemotingCommand.decode(byteBuffer);
        System.out.println(newCommond);
    }
}
