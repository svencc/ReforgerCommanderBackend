package com.recom.service;

import com.recom.persistence.dbcached.DBCachedPersistenceLayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.Serializable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DBCachedManagerTest {

    @Mock
    private DBCachedPersistenceLayer dbCachedPersistenceLayer;
    @Mock
    private CacheManager cacheManager;
    @InjectMocks
    private DBCachedManager serviceUnderTest;


    @Test
    public void testIsCached_CacheHit_ReturnsTrue() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";

        final Cache cacheMock = mock(Cache.class);
        final Cache.ValueWrapper cacheValueWrapperMock = mock(Cache.ValueWrapper.class);

        when(cacheManager.getCache(eq(cacheName))).thenReturn(cacheMock);
        when(cacheMock.get(eq(cacheKey))).thenReturn(cacheValueWrapperMock);
        when(cacheValueWrapperMock.get()).thenReturn(new Object());

        DBCachedManager cachedManager = new DBCachedManager(dbCachedPersistenceLayer, cacheManager);

        // Act
        boolean result = cachedManager.isCached(cacheName, cacheKey);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsCached_CacheMiss_DBHit_ReturnsFalse() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";

        final Cache cacheMock = mock(Cache.class);
        final Cache.ValueWrapper cacheValueWrapperMock = mock(Cache.ValueWrapper.class);

        when(cacheManager.getCache(eq(cacheName))).thenReturn(cacheMock);
        when(cacheMock.get(eq(cacheKey))).thenReturn(null);
        when(dbCachedPersistenceLayer.isInDBCache(eq(cacheName), eq(cacheKey))).thenReturn(true);

        // Act
        final boolean resultToTest = serviceUnderTest.isCached(cacheName, cacheKey);

        // Assert
        assertTrue(resultToTest); // In this case, the combined result from cache miss and DB hit is true.
    }

    @Test
    public void testIsCached_CacheMiss_DBMiss_ReturnsFalse() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";

        final Cache cacheMock = mock(Cache.class);
        final Cache.ValueWrapper cacheValueWrapperMock = mock(Cache.ValueWrapper.class);

        when(cacheManager.getCache(eq(cacheName))).thenReturn(cacheMock);
        when(cacheMock.get(eq(cacheKey))).thenReturn(null);
        when(dbCachedPersistenceLayer.isInDBCache(eq(cacheName), eq(cacheKey))).thenReturn(false);

        // Act
        final boolean resultToTest = serviceUnderTest.isCached(cacheName, cacheKey);

        // Assert
        assertFalse(resultToTest); // In this case, both cache and DB miss, so the result should be false.
    }

    @Test
    void testGet_CacheHit_ReturnsValue() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";
        final Serializable cachedValue = "cachedValue";

        final Cache.ValueWrapper cacheValueWrapperMock = mock(Cache.ValueWrapper.class);
        final Cache cacheMock = mock(Cache.class);

        when(cacheManager.getCache(eq(cacheName))).thenReturn(cacheMock);
        when(cacheValueWrapperMock.get()).thenReturn(cachedValue);
        when(cacheMock.get(eq(cacheKey))).thenReturn(cacheValueWrapperMock);

        // Act
        Optional<Serializable> result = serviceUnderTest.get(cacheName, cacheKey);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(cachedValue, result.get());
    }

    @Test
    void testGet_CacheMiss_DBHit_ReturnsValueFromDB() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";
        final Serializable dbValue = "dbValue";

        when(cacheManager.getCache(eq(cacheName))).thenReturn(null);
        when(dbCachedPersistenceLayer.<Serializable>get(eq(cacheName), eq(cacheKey))).thenReturn(Optional.of(dbValue));

        // Act
        Optional<Serializable> result = serviceUnderTest.get(cacheName, cacheKey);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(dbValue, result.get());
    }

    @Test
    void testGet_CacheMissAndDBMiss_ReturnsEmptyOptional() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";

        when(cacheManager.getCache(eq(cacheName))).thenReturn(null);
        when(dbCachedPersistenceLayer.<Serializable>get(eq(cacheName), eq(cacheKey))).thenReturn(Optional.empty());

        // Act
        Optional<Serializable> result = serviceUnderTest.get(cacheName, cacheKey);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testPut_withCacheAndDBSuccessful() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";
        final Serializable valueToCache = "valueToCache";
        final Cache cacheMock = mock(Cache.class);

        when(cacheManager.getCache(eq(cacheName))).thenReturn(cacheMock);

        // Act
        serviceUnderTest.put(cacheName, cacheKey, valueToCache);

        // Assert
        verify(cacheMock).put(eq(cacheKey), eq(valueToCache));
        verify(dbCachedPersistenceLayer).put(eq(cacheName), eq(cacheKey), eq(valueToCache));
    }

    @Test
    void testPut_widthCacheNotExistAndDBSuccessful() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";
        final Serializable valueToCache = "valueToCache";

        when(cacheManager.getCache(eq(cacheName))).thenReturn(null);

        // Act
        serviceUnderTest.put(cacheName, cacheKey, valueToCache);

        // Assert
        verify(dbCachedPersistenceLayer).put(eq(cacheName), eq(cacheKey), eq(valueToCache));
    }

    @Test
    void testDelete_whenCacheExists_Successful() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";
        final Cache cacheMock = mock(Cache.class);

        when(cacheManager.getCache(eq(cacheName))).thenReturn(cacheMock);

        // Act
        serviceUnderTest.delete(cacheName, cacheKey);

        // Assert
        verify(cacheMock).evict(eq(cacheKey));
        verify(dbCachedPersistenceLayer).delete(eq(cacheName), eq(cacheKey));
    }

    @Test
    void testDelete_whenCacheNotExists_Successful() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";

        when(cacheManager.getCache(eq(cacheName))).thenReturn(null);

        // Act
        serviceUnderTest.delete(cacheName, cacheKey);

        // Assert
        verify(dbCachedPersistenceLayer).delete(eq(cacheName), eq(cacheKey));
    }

    @Test
    void testDelete_whenCacheManagerThrowsException_Successful() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";
        final Cache cacheMock = mock(Cache.class);

        when(cacheManager.getCache(eq(cacheName))).thenReturn(cacheMock);
        doThrow(new RuntimeException("Cache evict failed")).when(cacheMock).evict(eq(cacheKey));

        // Act
        serviceUnderTest.delete(cacheName, cacheKey);

        // Assert
        verify(dbCachedPersistenceLayer).delete(eq(cacheName), eq(cacheKey));
    }

}