package Server.provider;

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

    public ServiceProvider(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.interfaceProvider = new HashMap<String, Object>();
        this.registerCenter = new ZkRegisterCenter();
    }

    public void register(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        InetSocketAddress address = new InetSocketAddress(ip, port);

        for (Class<?> clazz : interfaces) {
            interfaceProvider.put(clazz.getName(), service);
            registerCenter.register(clazz.getName(), address);
            log.info("register service:{}, address:{}", clazz.getName(), address);
        }
    }

    public Object getService(String serviceName) {
        return interfaceProvider.get(serviceName);
    }
}
