package com.aplication.rest.instruments.config.rate_limit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RateLimitingService {

    private final LettuceBasedProxyManager<String> proxyManager;

    public Bucket resolveBucket(String ip) {
        Supplier<BucketConfiguration> configSupplier = this::getGeneralConfiguration;
        return proxyManager.builder().build("rate_limit:general:" + ip, configSupplier);
    }

    public Bucket resolveHeavyBucket(String ip) {
        Supplier<BucketConfiguration> configSupplier = this::getHeavyConfiguration;
        return proxyManager.builder().build("rate_limit:heavy:" + ip, configSupplier);
    }

    private BucketConfiguration getGeneralConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(20)
                        .refillIntervally(20, Duration.ofMinutes(1)).build())
                .build();
    }

    private BucketConfiguration getHeavyConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(3)
                        .refillIntervally(3, Duration.ofMinutes(1)).build())
                .build();
    }
}
