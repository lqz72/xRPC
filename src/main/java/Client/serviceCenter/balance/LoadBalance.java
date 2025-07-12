package Client.serviceCenter.balance;

import java.util.List;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/7/10 23:44
 */
public interface LoadBalance {
    String balance(List<String> addressList);
    void addNode(String node) ;
    void delNode(String node);
}
