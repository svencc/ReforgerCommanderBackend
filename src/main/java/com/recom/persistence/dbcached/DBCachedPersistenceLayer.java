package com.recom.persistence.dbcached;

import com.recom.entity.DBCachedItem;
import com.recom.exception.DBCachedDeserializationException;
import com.recom.service.SerializationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DBCachedPersistenceLayer {

    @NonNull
    private final DatabasePersistentCacheRepository databasePersistentCacheRepository;
    @NonNull
    private final SerializationService serializationService;


    public boolean isInDBCache(
            @NonNull final String cacheName,
            @NonNull final String cacheKey
    ) {
        return databasePersistentCacheRepository.findByCacheNameAndCacheKey(cacheName, cacheKey).isPresent();
    }

    public void delete(
            @NonNull final String cacheName,
            @NonNull final String cacheKey
    ) {
        databasePersistentCacheRepository.findByCacheNameAndCacheKey(cacheName, cacheKey)
                .ifPresentOrElse(cacheItem -> {
                            log.debug("Delete cache item {} - {}", cacheName, cacheKey);
                            databasePersistentCacheRepository.delete(cacheItem);
                        },
                        () -> log.debug("Cache item {} - {} not found for deletion", cacheName, cacheKey)
                );
    }

    public <V extends Serializable> void put(
            @NonNull final String cacheName,
            @NonNull final String cacheKey,
            @NonNull final V valueToCache
    ) {
        Optional.ofNullable(databasePersistentCacheRepository.findByCacheNameAndCacheKey(cacheName, cacheKey)
                .map(existingCacheItem -> updateExistingCacheItem(existingCacheItem, valueToCache))
                .orElseGet(() -> createNewCacheItem(cacheName, cacheKey, valueToCache))
        ).ifPresent(databasePersistentCacheRepository::save);
    }

    @Nullable
    private <V extends Serializable> DBCachedItem updateExistingCacheItem(
            @NonNull final DBCachedItem existingCacheItem,
            @NonNull final V value
    ) {
        log.debug("Updating existing cache item {} - {}", existingCacheItem.getCacheName(), existingCacheItem.getCacheKey());
        try (ByteArrayOutputStream byteArrayOutputStream = serializationService.serializeObject(value)) {
            existingCacheItem.setCachedValue(byteArrayOutputStream.toByteArray());
            return existingCacheItem;
        } catch (final Exception e) {
            log.error("Error serializing cache value with key {}!", existingCacheItem.getCacheKey(), e);
            return null;
        }
    }

    @Nullable
    private <V extends Serializable> DBCachedItem createNewCacheItem(
            @NonNull final String cacheName,
            @NonNull final String cacheKey,
            @NonNull final V valueToCache
    ) {
        log.debug("Creating new cache item {} - {}.", cacheName, cacheKey);
        try (ByteArrayOutputStream byteArrayOutputStream = serializationService.serializeObject(valueToCache)) {
            return DBCachedItem.builder()
                    .cacheName(cacheName)
                    .cacheKey(cacheKey)
                    .cachedValue(byteArrayOutputStream.toByteArray())
                    .build();
        } catch (final Exception e) {
            log.error("Error serializing cache value with key {}!", cacheKey, e);
            return null;
        }
    }

    @NonNull
    public <V extends Serializable> Optional<V> get(
            @NonNull final String cacheName,
            @NonNull final String cacheKey
    ) throws DBCachedDeserializationException {
        final Optional<DBCachedItem> maybeCachedItem = databasePersistentCacheRepository.findByCacheNameAndCacheKey(cacheName, cacheKey);

        maybeCachedItem.ifPresentOrElse(
                (__) -> log.debug("Cache hit {} - {}", cacheName, cacheKey),
                () -> log.debug("Cache miss {} - {}", cacheName, cacheKey)
        );

        final Optional<V> maybeDeserializedObject = maybeCachedItem.flatMap(cacheItem -> serializationService.deserializeObject(cacheItem.getCachedValue()));
        if (maybeDeserializedObject.isEmpty()) {
            log.error("Error deserializing cache value with key {}", cacheKey);
        }

        return maybeDeserializedObject;
    }

    public void clearAll() {
        databasePersistentCacheRepository.deleteAll();
    }

}