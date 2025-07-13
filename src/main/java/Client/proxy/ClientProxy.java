package Client.proxy;

import Client.circuitBreaker.CircuitBreaker;
import Client.circuitBreaker.CircuitBreakerProvider;
import Client.retry.GuavaRetry;
import Client.rpcClient.RpcClient;
import Client.rpcClient.impl.NettyRpcClient;
import Client.rpcClient.impl.SimpleSocketRpcClient;
import Client.serviceCenter.ServiceCenter;
import Client.serviceCenter.ZkServiceCenter;
import common.message.RpcRequest;
import common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 10:25
 */
@Slf4j
public class ClientProxy implements InvocationHandler {

    private RpcClient rpcClient;

    private ServiceCenter serviceCenter;

    private CircuitBreakerProvider circuitBreakerProvider;

    public ClientProxy() throws InterruptedException {
        this.rpcClient = new NettyRpcClient();
        this.serviceCenter = new ZkServiceCenter();
        this.circuitBreakerProvider = new CircuitBreakerProvider();
    }

    public ClientProxy(String ip, int port, int choose) throws InterruptedException {
        if (choose == 0) {
            rpcClient = new SimpleSocketRpcClient(ip, port);
        } else {
            rpcClient = new NettyRpcClient();
        }

        this.serviceCenter = new ZkServiceCenter();
        this.circuitBreakerProvider = new CircuitBreakerProvider();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramTypes(method.getParameterTypes()).build();

        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(request.getInterfaceName());
        if (!circuitBreaker.allowRequest()) {
            log.info("ClientProxy: invoke, 服务器熔断");
            return null;
        }

        RpcResponse resp = null;
        if (serviceCenter.checkRetry(request.getInterfaceName())) {
            resp = new GuavaRetry(rpcClient).sendServiceWithRetry(request);
        } else {
            resp = rpcClient.sendRequest(request);
        }

        if (Objects.isNull(resp)) {
            log.error("ClientProxy: invoke, response is null");
            return null;
        }

        if (resp.getCode() == 200) {
            circuitBreaker.successRecord();
        } else if (resp.getCode() == 500) {
            circuitBreaker.failureRecord();
        }

        return resp.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object obj = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) obj;
    }
}
