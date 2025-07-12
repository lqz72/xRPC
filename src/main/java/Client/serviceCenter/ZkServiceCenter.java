package Client.serviceCenter;

import Client.cache.ServiceCache;
import Client.serviceCenter.ZKWatcher.WatchZK;
import Client.serviceCenter.balance.LoadBalance;
import Client.serviceCenter.balance.impl.ConsistencyHashBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
    private LoadBalance loadBalance;
    private static final String ROOT_PATH = "xRPC";
    private static final String RETRY_PATH = "canRetry";
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

        this.loadBalance = new ConsistencyHashBalance();
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            List<String> addressList = cache.getServiceAddress(serviceName);
            if (CollectionUtils.isEmpty(addressList)) {
                addressList = this.client.getChildren().forPath("/" + serviceName);
                log.info("服务缓存命中，serviceName:{}", serviceName);
            }

            String address = loadBalance.balance(addressList);
            if (address == null) {
                log.error("ZkServiceCenter: serviceDiscovery, 获取服务失败, serviceName={}", serviceName);
            }
            return InetAddressUtil.String2InetAddress(address);
        } catch (Exception e) {
            log.error("获取服务失败", e);
        }
        return null;
    }

    @Override
    public boolean checkRetry(String serviceName) {
        boolean canRetry = false;
        try {
            List<String> serverList = this.client.getChildren().forPath("/" + RETRY_PATH);

            for (String s : serverList) {
                if (serviceName.equals(s)) {
                    canRetry = true;
                    log.info("ZkServiceCenter: checkRetry is true, serviceName={}", serviceName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return canRetry;
    }

}
