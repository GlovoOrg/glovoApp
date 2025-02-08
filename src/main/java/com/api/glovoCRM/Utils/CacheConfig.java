package com.api.glovoCRM.Utils;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Value("${cash.duration}")
    private int duration;
    @Value("${cash.maxSize}")
    private int maxSize;
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                        .maximumSize(maxSize)
                        .expireAfterWrite(duration, TimeUnit.MINUTES));
        return cacheManager;
    }
}
