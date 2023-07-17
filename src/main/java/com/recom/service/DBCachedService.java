package com.recom.service;

import com.recom.repository.dbcached.DBCachedPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class DBCachedService {

    @NonNull
    private final DBCachedPersistenceLayer dbCachedPersistenceLayer;


    @NonNull
    public <V extends Serializable> V proxyToDBCache(
            @NonNull final String cacheName,
            @NonNull final String cacheKey,
            @NonNull final Supplier<? extends V> cacheLoader
    ) {
        return dbCachedPersistenceLayer.<V>get(cacheName, cacheKey)
                .orElseGet(() -> {
                            log.info("Cache item {} - {} not found, executing supplier!", cacheName, cacheKey);
                            final V value = cacheLoader.get();
                            dbCachedPersistenceLayer.put(cacheName, cacheKey, value);
                            return value;
                        }
                );
    }

}