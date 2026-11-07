package com.esporteclubepelotas.api.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class RateLimiterService {

    private static class WindowCounter {
        final long windowStartMs;
        final AtomicInteger count;

        WindowCounter(long windowStartMs) {
            this.windowStartMs = windowStartMs;
            this.count = new AtomicInteger(1);
        }
    }

    private final Map<String, WindowCounter> buckets = new ConcurrentHashMap<>();

    public boolean tryAcquire(String key, int maxRequests, long windowMs) {
        long now = System.currentTimeMillis();

        if (buckets.size() > 50000) {
            cleanupExpired(now);
        }

        WindowCounter counter = buckets.compute(key, (k, existing) -> {
            if (existing == null || (now - existing.windowStartMs) >= windowMs) {
                return new WindowCounter(now);
            }
            existing.count.incrementAndGet();
            return existing;
        });

        return counter.count.get() <= maxRequests;
    }

    private void cleanupExpired(long now) {
        buckets.entrySet().removeIf(entry -> (now - entry.getValue().windowStartMs) >= 300000);
    }
}

