package com.unicenta.poc.config;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for setting up caching in the POS application. Uses
 * Caffeine as the in-memory cache provider for high performance.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates a custom CacheManager using Caffeine. Defines cache behavior such
     * as: - Maximum size: 500 entries - Expire entries 24 hours after write
     *
     * @return configured CaffeineCacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "categoriesNames",
                "allCategories",
                "taxesNames",
                "taxesRates",
                "allTaxes" // Cache for full tax map
        );

        // Customize Caffeine settings
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500) // Max 500 entries in cache
                .expireAfterWrite(24, TimeUnit.HOURS) // Evict after 24h
                .recordStats() // Enable statistics (optional)
        );

        return cacheManager;
    }
}
