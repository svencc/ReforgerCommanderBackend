package com.recom.service;

import com.recom.persistence.dbcached.DBCachedPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

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

}