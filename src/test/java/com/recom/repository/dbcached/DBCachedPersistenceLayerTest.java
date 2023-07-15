package com.recom.repository.dbcached;

import com.recom.entity.DBCachedItem;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DBCachedPersistenceLayerTest {

    @Mock
    private DatabasePersistentCacheRepository repository;

    @InjectMocks
    private DBCachedPersistenceLayer cachePersistenceLayer;

    @Test
    public void testPut() {
        // Arrange
        final String cacheName = "testCacheName";
        final String key = "testKey";
        final String value = "testValue";
        final byte[] serializedValue = serializeObjectHelper(value).toByteArray();

        when(repository.findByCacheNameAndCacheKey(eq(cacheName), eq(key))).thenReturn(Optional.empty());

        final ArgumentCaptor<DBCachedItem> captor = ArgumentCaptor.forClass(DBCachedItem.class);

        // Act
        cachePersistenceLayer.put(cacheName, key, value);

        // Assert
        verify(repository, times(1)).save(captor.capture());

        final DBCachedItem capturedCacheItem = captor.getValue();
        assertEquals(cacheName, capturedCacheItem.getCacheName());
        assertEquals(key, capturedCacheItem.getCacheKey());
        assertTrue(Arrays.equals(serializedValue, capturedCacheItem.getCachedValue()));
    }

    @NonNull
    @SneakyThrows
    private ByteArrayOutputStream serializeObjectHelper(@NonNull final Serializable object) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
        }
        return byteArrayOutputStream;
    }

    @Test
    public void testGet() {
        // Arrange
        final String cacheName = "testCacheName";
        final String key = "testKey";
        final String value = "testValue";

        final DBCachedItem cacheItem = DBCachedItem.builder()
                .cacheName(cacheName)
                .cacheKey(key)
                .cachedValue(serializeObjectHelper(value).toByteArray())
                .build();

        when(repository.findByCacheNameAndCacheKey(eq(cacheName), eq(key))).thenReturn(Optional.of(cacheItem));

        // Act
        Optional<String> result = cachePersistenceLayer.get(cacheName, key);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(value, result.get());
    }

}