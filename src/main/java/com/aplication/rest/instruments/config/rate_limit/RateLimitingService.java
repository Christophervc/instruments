package com.aplication.rest.instruments.config.rate_limit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class RateLimitingService {

    private final LettuceBasedProxyManager<String> proxyManager;

    // Inject dependencies - redis connection
    public RateLimitingService() {
        // 1. create redis client
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        // 2. define connection, codecs explicitly
        // Key: String (user IP)
        // Value: byte[] (Bucket4j serialized data)
        StatefulRedisConnection<String, byte[]> redisConnection = redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
        ClientSideConfig clientSideConfig = ClientSideConfig.getDefault();
        // 3. create proxy manager pass connection and config
        this.proxyManager = LettuceBasedProxyManager.builderFor(redisConnection)
                .withClientSideConfig(clientSideConfig
                        .withExpirationAfterWriteStrategy(ExpirationAfterWriteStrategy
                                .basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(10))))
                        .build(); //buckets expires after 10 minutes of inactivity
    }

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
