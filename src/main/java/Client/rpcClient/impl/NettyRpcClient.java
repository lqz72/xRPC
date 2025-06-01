package Client.rpcClient.impl;

import Client.netty.NettyClientInitializer;
import Client.rpcClient.RpcClient;
import Client.serviceCenter.ServiceCenter;
import Client.serviceCenter.ZkServiceCenter;
import common.message.RpcRequest;
import common.message.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/5/25 21:08
 */
@Slf4j
public class NettyRpcClient implements RpcClient {

    private static Bootstrap bootstrap;
    private static EventLoopGroup group;

    private ServiceCenter serviceCenter;

    public NettyRpcClient() {
        serviceCenter = new ZkServiceCenter();
    }

    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            String interfaceName = request.getInterfaceName();
            InetSocketAddress inetSocketAddress = serviceCenter.serviceDiscovery(interfaceName);
            String ip = inetSocketAddress.getHostName();
            int port = inetSocketAddress.getPort();

            // 以阻塞的方式等待建立连接
            ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
            Channel channel = channelFuture.channel();
            // 发送数据
            channel.writeAndFlush(request);
            // 以阻塞的方式等待返回结果
            channel.closeFuture().sync();
            // 从channel中获取返回的结果
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse resp = channel.attr(key).get();
            log.info("RPC调用结果:{}", resp);
            return resp;
        } catch (InterruptedException e) {
            log.error("服务端出错", e);
        }
        return null;
    }
}
