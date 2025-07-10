package Client.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/6/1 23:33
 */
@Slf4j
public class ServiceCache {

    private Map<String, List<String>> cache = new HashMap();

    public void addAddressToCache(String serviceName, String address) {
        if (cache.containsKey(serviceName)) {
            cache.get(serviceName).add(address);
        } else {
            List<String> addressList = new ArrayList<>();
            addressList.add(address);
            cache.put(serviceName, addressList);
        }
    }

    public void updateServiceAddress(String serviceName, String oldAddress, String newAddress) {
        if (cache.containsKey(serviceName)) {
            List<String> addressList = cache.get(serviceName);
            addressList.remove(oldAddress);
            addressList.add(newAddress);
        } else {
            log.error("更新失败，服务不存在");
        }
    }

    public void removeAddressFromCache(String serviceName, String address) {
        if (cache.containsKey(serviceName)) {
            List<String> addressList = cache.get(serviceName);
            addressList.remove(address);
            log.info("将服务名为{}，地址为{}的服务从本地缓存移除", serviceName, address);
        }
    }

    public List<String> getServiceAddress(String serviceName) {
        if (cache.containsKey(serviceName)) {
            return cache.get(serviceName);
        }
        return null;
    }
}
