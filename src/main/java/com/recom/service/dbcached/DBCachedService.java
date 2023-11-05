package com.recom.service.dbcached;

import com.recom.exception.DBCachedDeserializationException;
import com.recom.persistence.dbcached.DBCachedPersistenceLayer;
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
    public <V extends Serializable> V proxyToDBCacheSafe(
            @NonNull final String cacheName,
            @NonNull final String cacheKey,
            @NonNull final Supplier<? extends V> cacheLoader
    ) {
        try {
            return proxyToDBCacheUnsafe(cacheName, cacheKey, cacheLoader);
        } catch (final DBCachedDeserializationException e) {
            log.debug("Executing cacheLoader (due to deserialization error) {} - {} ", cacheName, cacheKey);
            log.debug("Delete {} - {} ", cacheName, cacheKey);
            dbCachedPersistenceLayer.delete(cacheName, cacheKey);
            V valueToRecache = cacheLoader.get();
            dbCachedPersistenceLayer.put(cacheName, cacheKey, valueToRecache);

            return valueToRecache;
        }
    }

    @NonNull
    public <V extends Serializable> V proxyToDBCacheUnsafe(
            @NonNull final String cacheName,
            @NonNull final String cacheKey,
            @NonNull final Supplier<? extends V> cacheLoader
    ) throws DBCachedDeserializationException {
        return dbCachedPersistenceLayer.<V>get(cacheName, cacheKey)
                .orElseGet(() -> {
                            log.debug("Executing cacheLoader {} - {} ", cacheName, cacheKey);
                            final V value = cacheLoader.get();
                            dbCachedPersistenceLayer.put(cacheName, cacheKey, value);

                            return value;
                        }
                );
    }

}