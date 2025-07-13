package Server.ratelimit.provider;

import Server.ratelimit.RateLimit;
import Server.ratelimit.impl.TokenBucketRateLimitImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/7/13 19:24
 */
public class RateLimitProvider {

    private Map<String, RateLimit> rateLimitMap = new ConcurrentHashMap<>();

    public RateLimit getRateLimit(String interfaceName){
        if(!rateLimitMap.containsKey(interfaceName)){
            // 令牌桶配置先写死
            RateLimit rateLimit= new TokenBucketRateLimitImpl(100,10);
            return rateLimitMap.putIfAbsent(interfaceName, rateLimit);
        }
        return rateLimitMap.get(interfaceName);
    }
}
