package com.pb.springbootlesson5.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;

@Configuration
// 启用缓存配置
@EnableCaching
public class CacheConfiguration {

    @Bean(name = "simpleCache")
    public CacheManager simpleCacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        // 配置缓存名
        ConcurrentMapCache cache = new ConcurrentMapCache("cache-simple");
        simpleCacheManager.setCaches(Collections.singleton(cache));

        return simpleCacheManager;
    }

    // 多个 CacheManager 情况下，需要设置一个主的 CacheManager
    @Primary
    @Bean(name = "redisCache")
    public CacheManager redisCacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        redisCacheManager.setCacheNames(Collections.singleton("cache-redis"));
        redisCacheManager.setLoadRemoteCachesOnStartup(true);

        return redisCacheManager;
    }
}
