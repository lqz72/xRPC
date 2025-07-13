package Server.ratelimit.impl;

import Server.ratelimit.RateLimit;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/7/13 19:15
 */
public class TokenBucketRateLimitImpl implements RateLimit {
    //令牌产生速率（单位为ms）
    private static  int RATE;
    //桶容量
    private static  int CAPACITY;
    //当前桶容量
    private volatile int curCapacity;
    //时间戳
    private volatile long timeStamp = System.currentTimeMillis();
    
    public TokenBucketRateLimitImpl(int rate, int capacity){
        RATE = rate;
        CAPACITY = capacity;
        curCapacity = capacity;
    }
    
    @Override
    public synchronized boolean getToken() {
        // 如果桶中还有剩余
        if(curCapacity > 0){
            curCapacity--;
            return true;
        }

        long span = System.currentTimeMillis() - timeStamp;
        if (span > RATE) {
            // 计算时间间隔内生成的令牌个数
            int newToken = (int)(span / RATE);
            if (newToken > 2) {
                curCapacity += newToken - 1;  // 当前请求消耗1个令牌
            }

            timeStamp = System.currentTimeMillis();
            return true;
        }

        return false;
    }
}
