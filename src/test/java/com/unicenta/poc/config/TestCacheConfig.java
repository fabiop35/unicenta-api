package com.unicenta.poc.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestCacheConfig {

    /*@Bean
    public CacheManager cacheManager() {
        // A cache manager that does nothing – perfect for integration tests
        return new NoOpCacheManager();
    }*/
}