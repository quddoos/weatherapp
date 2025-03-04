package com.aqkassignment.com.weatherapp.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
class CacheConfig {
    @Value("${cache.forecast.expiration:30}")
    private long cacheExpirationMinutes;


        @Bean
        public CacheManager cacheManager() {
            SimpleCacheManager cacheManager = new SimpleCacheManager();
            ConcurrentMapCache forecastCache = new ConcurrentMapCache("forecast",
                    new ConcurrentHashMap<>(256),
                    false);

            cacheManager.setCaches(Collections.singletonList(forecastCache));
            Runnable cacheCleanup = () -> forecastCache.clear();
            java.util.concurrent.Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(cacheCleanup, cacheExpirationMinutes, cacheExpirationMinutes, TimeUnit.MINUTES);

        return cacheManager;
    }
}
