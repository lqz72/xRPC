package Client.circuitBreaker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/7/13 22:57
 */
public class CircuitBreakerProvider {

    private Map<String,CircuitBreaker> circuitBreakerMap = new HashMap<>();

    public synchronized CircuitBreaker getCircuitBreaker(String serviceName){
        if(!circuitBreakerMap.containsKey(serviceName)){
            CircuitBreaker circuitBreaker = new CircuitBreaker(1,0.5,10000);
            return circuitBreakerMap.putIfAbsent(serviceName, circuitBreaker);
        }
        return circuitBreakerMap.get(serviceName);
    }
}
