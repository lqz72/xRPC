package Client.serviceCenter.balance.impl;

import Client.serviceCenter.balance.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/7/10 23:48
 */
@Slf4j
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String balance(List<String> addressList) {
        if (CollectionUtils.isEmpty(addressList)) {
            log.info("ConsistencyHashBalance: balance, addressList is empty");
            return null;
        }
        int size = addressList.size();
        int choose = ThreadLocalRandom.current().nextInt(size);  // [0, size)

        String chooseServer = addressList.get(choose);
        log.info("RandomLoadBalance: balance, choose server:{}", chooseServer);
        return chooseServer;
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
