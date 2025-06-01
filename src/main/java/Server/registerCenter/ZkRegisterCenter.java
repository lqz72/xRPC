package Server.registerCenter;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import utils.InetAddressUtil;

import java.net.InetSocketAddress;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 17:26
 */
@Slf4j
public class ZkRegisterCenter implements RegisterCenter {

    private CuratorFramework client;
    private static final String ROOT_PATH = "xRPC";
    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    public ZkRegisterCenter() {
        this.client = CuratorFrameworkFactory.builder()
                .connectString(ZK_ADDRESS).namespace(ROOT_PATH)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .sessionTimeoutMs(5000).build();

        this.client.start();
    }

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try {
            // serviceName创建为永久节点 服务上下线只影响子节点
            if(client.checkExists().forPath("/" + serviceName) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
            }
            // 子节点创建为临时节点
            String path = "/" + serviceName + "/" + InetAddressUtil.InetAddress2String(address);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            log.error("服务注册失败", e);
        }
    }
}
