package Server.registerCenter;

import java.net.InetSocketAddress;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 17:08
 */
public interface RegisterCenter {
    void register(String serviceName, InetSocketAddress address);
}
