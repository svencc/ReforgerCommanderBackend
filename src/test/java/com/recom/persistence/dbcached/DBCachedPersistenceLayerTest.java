package com.recom.persistence.dbcached;

import com.recom.entity.DBCachedItem;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DBCachedPersistenceLayerTest {

    @Mock
    private DatabasePersistentCacheRepository repository;
    @InjectMocks
    private DBCachedPersistenceLayer serviceUnderTest;

    @Captor
    private ArgumentCaptor<DBCachedItem> cacheItemCaptor;

    @Test
    public void testPut_withNonExistingCacheItem_thenItemIsStored() {
        // Arrange
        final String cacheName = "testCacheName";
        final String key = "testKey";
        final String value = "testValue";
        final byte[] serializedValue = serializeObjectHelper(value).toByteArray();

        when(repository.findByCacheNameAndCacheKey(eq(cacheName), eq(key))).thenReturn(Optional.empty());

        final ArgumentCaptor<DBCachedItem> captor = ArgumentCaptor.forClass(DBCachedItem.class);

        // Act
        serviceUnderTest.put(cacheName, key, value);

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
    public void testGet_withValidCacheData_thenReturnValue() {
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
        Optional<String> result = serviceUnderTest.get(cacheName, key);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(value, result.get());
    }

    @Test
    public void testDelete_withExistingCacheItem_thenDeleteCacheItem() {
        // Arrange
        final String cacheName = "testCacheName";
        final String key = "testKey";

        final DBCachedItem cachedItem = DBCachedItem.builder()
                .cacheName(cacheName)
                .cacheKey(key)
                .build();

        when(repository.findByCacheNameAndCacheKey(eq(cacheName), eq(key))).thenReturn(Optional.of(cachedItem));

        // Act
        serviceUnderTest.delete(cacheName, key);

        // Assert
        verify(repository, times(1)).delete(cacheItemCaptor.capture());

        final DBCachedItem capturedCacheItem = cacheItemCaptor.getValue();
        assertNotNull(capturedCacheItem);
        assertEquals(cacheName, capturedCacheItem.getCacheName());
        assertEquals(key, capturedCacheItem.getCacheKey());
    }

    @Test
    public void testDelete_withNonExistingCacheItem_thenDeleteNothing() {
        // Arrange
        final String cacheName = "testCacheName";
        final String key = "testKey";

        when(repository.findByCacheNameAndCacheKey(eq(cacheName), eq(key))).thenReturn(Optional.empty());

        // Act
        serviceUnderTest.delete(cacheName, key);

        // Assert
        verify(repository, never()).delete(any());
    }

    @Test
    public void testIsInDBCache_withExistingCacheItem_thenReturnTrue() {
        // Arrange
        final String cacheName = "testCacheName";
        final String key = "testKey";
        final DBCachedItem cachedItem = DBCachedItem.builder()
                .cacheName(cacheName)
                .cacheKey(key)
                .build();

        when(repository.findByCacheNameAndCacheKey(eq(cacheName), eq(key))).thenReturn(Optional.of(cachedItem));

        // Act
        boolean result = serviceUnderTest.isInDBCache(cacheName, key);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsInDBCache_withNonExistingCacheItem_thenReturnFalse() {
        // Arrange
        final String cacheName = "testCacheName";
        final String key = "testKey";

        when(repository.findByCacheNameAndCacheKey(eq(cacheName), eq(key))).thenReturn(Optional.empty());

        // Act
        boolean result = serviceUnderTest.isInDBCache(cacheName, key);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testClearAll_success() {
        // Arrange

        // Act
        serviceUnderTest.clearAll();

        // Assert
        verify(repository, times(1)).deleteAll();
    }

}