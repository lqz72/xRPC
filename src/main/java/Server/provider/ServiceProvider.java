package Server.provider;

import Server.ratelimit.RateLimit;
import Server.ratelimit.impl.TokenBucketRateLimitImpl;
import Server.ratelimit.provider.RateLimitProvider;
import Server.registerCenter.RegisterCenter;
import Server.registerCenter.ZkRegisterCenter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 10:48
 */
@Slf4j
public class ServiceProvider {

    private String ip;
    private int port;
    private Map<String, Object> interfaceProvider;
    private RegisterCenter registerCenter;
    private RateLimitProvider rateLimitProvider;

    public ServiceProvider(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.interfaceProvider = new HashMap<String, Object>();
        this.registerCenter = new ZkRegisterCenter();
        this.rateLimitProvider = new RateLimitProvider();
    }

    public void register(Object service, boolean canRetry) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        InetSocketAddress address = new InetSocketAddress(ip, port);

        for (Class<?> clazz : interfaces) {
            interfaceProvider.put(clazz.getName(), service);
            registerCenter.register(clazz.getName(), address, canRetry);
            log.info("register service:{}, address:{}", clazz.getName(), address);
        }
    }

    public Object getService(String serviceName) {
        return interfaceProvider.get(serviceName);
    }

    public RateLimit getRateLimit(String serviceName) {
        return rateLimitProvider.getRateLimit(serviceName);
    }

}
