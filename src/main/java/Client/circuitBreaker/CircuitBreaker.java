package Client.circuitBreaker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yongfu
 * @version 1.0
 * @create 2025/7/13 22:33
 */
public class CircuitBreaker {

    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private AtomicInteger failureCount = new AtomicInteger(0);
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger requestCount = new AtomicInteger(0);

    // 失败次数阈值
    private final int failureThreshold;
    // 从半开启状态到关闭状态所需的成功次数比例
    private final double halfOpenSuccessRate;
    // 从开启状态到半开启状态的时间间隔
    private final long retryTimePeriod;
    // 上一次调用失败的时间戳
    private long lastFailureTime = 0;

    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate,long retryTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.retryTimePeriod = retryTimePeriod;
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        if (CircuitBreakerState.CLOSED.equals(state)) {
            return true;
        } else if (CircuitBreakerState.OPEN.equals(state)) {
            // 开启状态经过retryTimePeriod时间后 进入半开启状态
            if (now - lastFailureTime > retryTimePeriod) {
                state = CircuitBreakerState.HALF_OPEN;
                resetCounter();
                requestCount.incrementAndGet();
                return true;
            }
            return false;
        } else if (CircuitBreakerState.HALF_OPEN.equals(state)) {
            requestCount.incrementAndGet();
            return true;
        }

        return false;
    }

    public synchronized void successRecord() {
        // 只有半开启状态才统计成功次数
        if (CircuitBreakerState.HALF_OPEN.equals(state)) {
            successCount.incrementAndGet();
            if (successCount.get() >= requestCount.get() * failureThreshold) {
                state = CircuitBreakerState.CLOSED;
                resetCounter();
            }
        }
    }

    public synchronized void failureRecord() {
        lastFailureTime = System.currentTimeMillis();

        // 如果关闭状态下失败次数超过阈值 转换为开启状态
        if (CircuitBreakerState.CLOSED.equals(state)) {
            failureCount.incrementAndGet();
            if (failureCount.get() >= failureThreshold) {
                state = CircuitBreakerState.OPEN;
                resetCounter();
            }
        }
        // 半开启状态下遇到失败 转换为开启状态
        else if (CircuitBreakerState.HALF_OPEN.equals(state)) {
            state = CircuitBreakerState.OPEN;
            resetCounter();
        }
    }

    public synchronized void resetCounter() {
        this.failureCount.set(0);
        this.successCount.set(0);
        this.requestCount.set(0);
    }
}

enum CircuitBreakerState {
    CLOSED,
    HALF_OPEN,
    OPEN
}
