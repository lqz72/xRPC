package Client.serviceCenter;

import Client.cache.ServiceCache;
import Client.serviceCenter.ZKWatcher.WatchZK;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import utils.InetAddressUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 17:14
 */
@Slf4j
public class ZkServiceCenter implements ServiceCenter {

    private CuratorFramework client;
    private ServiceCache cache;
    private static final String ROOT_PATH = "xRPC";
    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    public ZkServiceCenter() throws InterruptedException {
        this.client = CuratorFrameworkFactory.builder()
                .connectString(ZK_ADDRESS).namespace(ROOT_PATH)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .sessionTimeoutMs(5000).build();

        this.client.start();
        log.info("zookeeper连接成功");

        this.cache = new ServiceCache();
        WatchZK watcher = new WatchZK(this.client, this.cache);
        watcher.watchToUpdate();
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            List<String> addressList = cache.getServiceAddress(serviceName);
            if (addressList == null) {
                addressList = this.client.getChildren().forPath("/" + serviceName);
                log.info("服务缓存命中，serviceName:{}", serviceName);
            }
            String address = addressList.get(0);
            return InetAddressUtil.String2InetAddress(address);
        } catch (Exception e) {
            log.error("获取服务失败", e);
        }
        return null;
    }
}
