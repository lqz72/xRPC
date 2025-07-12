package Client.serviceCenter;

import java.net.InetSocketAddress;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 17:07
 */
public interface ServiceCenter {

    // 根据接口名获取服务地址
    InetSocketAddress serviceDiscovery(String serviceName);

    // 判断是否可重试
    boolean checkRetry(String serviceName);
}
