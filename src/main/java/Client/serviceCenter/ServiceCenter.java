package Client.serviceCenter;

import java.net.InetSocketAddress;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 17:07
 */
public interface ServiceCenter {
    InetSocketAddress serviceDiscovery(String serviceName);
}
