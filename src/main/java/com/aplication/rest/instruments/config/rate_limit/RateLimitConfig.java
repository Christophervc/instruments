package com.aplication.rest.instruments.config.rate_limit;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.time.Duration;
@Slf4j
@Configuration
public class RateLimitConfig implements DisposableBean {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, byte[]> redisConnection;

    @Bean
    public LettuceBasedProxyManager<String> proxyManager() {
        // 1. Create client
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        // 2. Config redis connection with explicit types
        StatefulRedisConnection<String, byte[]> redisConnection = redisClient
                .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
        // 3. Config Bucket4j client side config / TTL 10 minutes
        ClientSideConfig clientSideConfig = ClientSideConfig.getDefault()
                .withExpirationAfterWriteStrategy(ExpirationAfterWriteStrategy
                        .basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(10)));
        // 4. Build proxy manager and return bean
        return LettuceBasedProxyManager.builderFor(redisConnection)
                .withClientSideConfig(clientSideConfig)
                .build();
    }
    // 5. Implement DisposableBean to close resources
    @Override
    public void destroy() {
        if (redisConnection != null) {
            log.info("Closing Redis connection (Rate Limit)...");
            redisConnection.close();
        }
        if (redisClient != null) {
            log.info("Shutting down Redis client (Rate Limit)...");
            redisClient.shutdown();
        }
    }

}
