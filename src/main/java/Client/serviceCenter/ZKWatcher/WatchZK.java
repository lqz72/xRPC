package Client.serviceCenter.ZKWatcher;

import Client.cache.ServiceCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

import java.util.List;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 23:43
 */
@Slf4j
public class WatchZK {

    private CuratorFramework client;

    private ServiceCache cache;

    public WatchZK(CuratorFramework client, ServiceCache cache) {
        this.client = client;
        this.cache = cache;
    }

    public void watchToUpdate () throws InterruptedException {
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        // 添加事件监听器 监听path节点及子节点的变化
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {
                switch (type.name()) {
                    case "NODE_CREATED":
                        // path = /serviceName/address
                        String[] pathList = parsePath(childData1);
                        if (pathList.length <= 2) break;
                        cache.addAddressToCache(pathList[1], pathList[2]);
                        log.info("节点新增，child1Data:{}", childData1.getData());
                        break;

                    case "NODE_UPDATED":
                        if (childData == null) {
                            log.info("节点第一次赋值");
                        } else {
                            log.info("节点更新，childData:{} childData1:{}", childData.getData(), childData1.getData());
                        }
                        String[] oldPathList = parsePath(childData);
                        String[] newPathList = parsePath(childData1);
                        cache.updateServiceAddress(oldPathList[1], oldPathList[2], newPathList[2]);
                        break;

                    case "NODE_DELETED":
                        String[] delPathList = parsePath(childData);
                        if (delPathList.length <= 2) break;
                        cache.removeAddressFromCache(delPathList[1], delPathList[2]);
                        log.info("节点被删除, childData:{}", childData.getData());
                        break;

                    default:
                        break;
                }
            }
        });

        curatorCache.start();
    }

    public String[] parsePath(ChildData childData) {
        String s = new String(childData.getPath());
        return s.split("/");
    }
}
