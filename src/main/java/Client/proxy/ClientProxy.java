package Client.proxy;

import Client.rpcClient.RpcClient;
import Client.rpcClient.impl.NettyRpcClient;
import Client.rpcClient.impl.SimpleSocketRpcClient;
import common.message.RpcRequest;
import common.message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 10:25
 */
public class ClientProxy implements InvocationHandler {

    private RpcClient rpcClient;

    public ClientProxy(String ip, int port, int choose) {
        if (choose == 0) {
            rpcClient = new SimpleSocketRpcClient(ip, port);
        } else {
            rpcClient = new NettyRpcClient();
        }

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramTypes(method.getParameterTypes()).build();

        RpcResponse resp = rpcClient.sendRequest(request);
        return resp.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object obj = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) obj;
    }
}
