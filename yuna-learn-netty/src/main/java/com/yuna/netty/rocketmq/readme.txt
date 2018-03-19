1. 参照RocketMQ通过使用netty实现网络通讯

NettyRemotingServer 服务端 -|
                            |=> 继承NettyRemotingAbstract => RemotingServer
NettyRemotingClient 客户端 -|

NettyRemotingClient, NettyRemotingServer 通过注册Processor来处理业务操作
