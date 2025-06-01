package Server.netty;

import Server.provider.ServiceProvider;
import common.message.RpcRequest;
import common.message.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 12:18
 */
@AllArgsConstructor
@NoArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private ServiceProvider serviceProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        RpcResponse resp = (RpcResponse)getResponse(request);
        ctx.writeAndFlush(resp);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public Object getResponse(RpcRequest request)  {
        String serviceName = request.getInterfaceName();

        Method method = null;
        try {
            Object service = serviceProvider.getService(serviceName);
            method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            Object result = method.invoke(service, request.getParams());
            return RpcResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RpcResponse.fail();
        }
    }
}
