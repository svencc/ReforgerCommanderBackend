package com.recom.configuration;

import com.github.benmanes.caffeine.cache.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;


/*
    Caffein as hibernate second level cache example:
    https://github.com/ben-manes/caffeine/tree/master/examples/hibernate
    https://github.com/ben-manes/caffeine/issues/268
 */
@Slf4j
@Configuration
@EnableCaching
public class CaffeineCacheConfig {

    @NonNull
    private static final Duration ENTITY_CACHE_DURATION = Duration.ofMinutes(1);


    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        final CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache("default-query-results-region", buildQueryResultsReqionCache()); // fragwürdig ob das so richtig ist
        cacheManager.registerCustomCache("default-update-timestamps-region", buildInfiniteCache());

        cacheManager.registerCustomCache("second-level-entity-region", buildEntityCache());
        cacheManager.registerCustomCache("com.recom.entity.Account", buildEntityCache());

        cacheManager.registerCustomCache("UserPersistenceLayer.findByUUID", buildApplicationCache(100, 1000, Duration.ofSeconds(5)));

        return cacheManager;
    }

    @NonNull
    private Cache<Object, Object> buildApplicationCache(
            final int initialCapacity,
            final int maximumSize,
            @NonNull final Duration duration
    ) {
        return Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterAccess(duration.get(ChronoUnit.SECONDS), TimeUnit.SECONDS)
                .evictionListener(provideEvictionListener())
                .scheduler(Scheduler.systemScheduler())
                .recordStats()
                .build();
    }

    @NonNull
    private Cache<Object, Object> buildEntityCache() {
        return buildQueryResultsReqionCache();
    }

    @NonNull
    private Cache<Object, Object> buildQueryResultsReqionCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(ENTITY_CACHE_DURATION.get(ChronoUnit.SECONDS), TimeUnit.SECONDS)
                .evictionListener(provideEvictionListener())
                .removalListener(provideEvictionListener())
                .scheduler(Scheduler.systemScheduler())
                .recordStats()
                .build();
    }

    @NonNull
    private Cache<Object, Object> buildInfiniteCache() {
        return Caffeine.newBuilder()
                .build();
    }

    @NonNull
    private RemovalListener<Object, Object> provideEvictionListener() {
        final RemovalListener<Object, Object> evictionListener = (final Object key, final Object value, final RemovalCause cause) -> {
            final String valueType;
            if (value == null) {
                valueType = value.getClass().getSimpleName();
            } else {
                valueType = "<?>";
            }

            log.error(String.format("CAFFEINE: (%s):Key %s was evicted (%s)%n", valueType, key, cause));
        };

        return evictionListener;
    }

}