package com.aplication.rest.instruments.config.rate_limit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {
    //store buckets for each ip in memory (IP, Bucket)
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> heavyBuckets = new ConcurrentHashMap<>();

    //General plan: 20 requests per minute for normal navigation
    public Bucket resolveBucket(String ip) {
        return generalBuckets.computeIfAbsent(ip, this::createNewGeneralBucket);
    }
    //Heavy plan: 3 requests per minute for heavy operations (export, orders)
    public Bucket resolveHeavyBucket(String ip) {
        return heavyBuckets.computeIfAbsent(ip, this::createNewHeavyBucket);
    }

    private Bucket createNewGeneralBucket(String ip) {
        //General 20 requests per minute
        Bandwidth limit = Bandwidth.builder()
                .capacity(20)
                .refillGreedy(20, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket createNewHeavyBucket(String ip) {
        //Heavy 3 requests per minute
        Bandwidth limit = Bandwidth.builder()
                .capacity(3)
                .refillGreedy(3, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
