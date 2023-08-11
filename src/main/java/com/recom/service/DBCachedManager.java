package com.recom.service;

import com.recom.persistence.dbcached.DBCachedPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DBCachedManager {

    @NonNull
    private final DBCachedPersistenceLayer dbCachedPersistenceLayer;
    @NonNull
    private final CacheManager cacheManager;

    public boolean isCached(
            @NonNull final String cacheName,
            @NonNull final String cacheKey
    ) {
        return Optional.ofNullable(cacheManager.getCache(cacheName))
                .map(cache -> cache.get(cacheKey))
                .map(cacheValue -> cacheValue.get() != null)
                .orElseGet(() -> dbCachedPersistenceLayer.isInDBCache(cacheName, cacheKey));
    }

    @NonNull
    public <V extends Serializable> Optional<V> get(
            @NonNull final String cacheName,
            @NonNull final String cacheKey
    ) {
        return Optional.ofNullable(Optional.ofNullable(cacheManager.getCache(cacheName))
                .map(cache -> cache.get(cacheKey))
                .filter(cacheValue -> cacheValue.get() != null)
                .filter(cacheValue -> cacheValue.get() instanceof Serializable)
                .map(cacheValue -> {
                    try {
                        return (V) cacheValue.get();
                    } catch (ClassCastException e) {
                        return null;
                    }
                })
                .orElseGet(() -> dbCachedPersistenceLayer.<V>get(cacheName, cacheKey).orElse(null)));
    }

    public <V extends Serializable> void put(
            @NonNull final String cacheName,
            @NonNull final String cacheKey,
            @NonNull final V valueToCache
    ) {
        try {
            cacheManager.getCache(cacheName).put(cacheKey, valueToCache);
        } catch (Exception e) {
            log.error("Cache {} not found", cacheName);
        }
        dbCachedPersistenceLayer.put(cacheName, cacheKey, valueToCache);
    }

}