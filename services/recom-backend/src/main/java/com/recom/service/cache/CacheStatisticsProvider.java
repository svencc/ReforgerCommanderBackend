package com.recom.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.recom.dto.cache.CacheInfoDto;
import com.recom.dto.cache.CacheStatisticsDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheStatisticsProvider {

    // see https://dev.to/noelopez/spring-cache-with-caffeine-384l -> spring actuator with cache info ...
    // see https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#caching

    @NonNull
    private final CacheManager cacheManager;


    @NonNull
    public CacheStatisticsDto provide() {
        final List<CacheInfoDto> caches = cacheManager.getCacheNames()
                .stream()
                .map(this::getCacheInfo)
                .toList();

        return CacheStatisticsDto.builder()
                .caches(caches)
                .build();
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private CacheInfoDto getCacheInfo(@NonNull final String cacheName) {
        final Cache<Object, Object> nativeCache = (Cache) cacheManager.getCache(cacheName).getNativeCache();
        final Set<Object> keys = nativeCache.asMap().keySet();
        final CacheStats stats = nativeCache.stats();
        final String concatenatedKeys = keys.stream()
                .map(Object::toString)
                .reduce("", (a, b) -> a + ", " + b);
        final Set<Object> stringifiedKeys = keys.stream()
                .map(Object::toString)
                .map(string -> (Object) string)
                .collect(Collectors.toSet());

        return CacheInfoDto.builder()
                .name(cacheName)
                .size(keys.size())
                .keys(stringifiedKeys)
                .stats(stats.toString())
                .build();
    }

}