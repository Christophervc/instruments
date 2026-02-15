package com.aplication.rest.instruments.config.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching // Enable caching annotation
public class CacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //Define polymorphic validator to allow saving objects of different types
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class) //note: allow root to be Object in Redis
                .allowIfBaseType("com.aplication.rest.instruments") //DTOs, result
                .allowIfBaseType("java.util") //ArrayList, HashMap
                .allowIfBaseType("java.time") //LocalDate, LocalTime, LocalDateTime
                .build();

        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule()) //locale date time created_at, updated_at
                .addModule(new Jdk8Module()) //Optionals
                .activateDefaultTyping(
                        ptv,
                        ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT,
                        JsonTypeInfo.As.PROPERTY)
                .build(); //Custom object mapper
        //create serializer
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // default config for all cache
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // ttl 10 min
                .disableCachingNullValues() // avoid saving null values
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new StringRedisSerializer()))// keys (strings)
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(serializer));// readable values (JSON) Jackson - redis instead of binary java

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
