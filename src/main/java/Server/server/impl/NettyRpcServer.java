package Server.server.impl;

import Server.netty.NettyServerInitializer;
import Server.provider.ServiceProvider;
import Server.server.RpcServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 12:19
 */
@Slf4j
public class NettyRpcServer implements RpcServer {

    private ServiceProvider serviceProvider;

    public NettyRpcServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start(int port) {
        NioEventLoopGroup bootGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        log.info("netty服务端启动 port:{}", port);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bootGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new NettyServerInitializer(serviceProvider));

        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务端出错", e);
        } finally {
            bootGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
