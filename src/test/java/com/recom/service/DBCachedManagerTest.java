package com.recom.service;

import com.recom.persistence.dbcached.DBCachedPersistenceLayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

}