package Client.serviceCenter.balance.impl;

import Client.serviceCenter.balance.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/7/10 23:45
 */
@Slf4j
public class RoundLoadBalance implements LoadBalance {

    private int choose = -1;

    @Override
    public String balance(List<String> addressList) {
        if (CollectionUtils.isEmpty(addressList)) {
            log.info("ConsistencyHashBalance: balance, addressList is empty");
            return null;
        }
        choose++;
        choose = choose % addressList.size();

        String chooseServer = addressList.get(choose);
        log.info("RoundLoadBalance: balance, choose server:{}", chooseServer);
        return chooseServer;
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
