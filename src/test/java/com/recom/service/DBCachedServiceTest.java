package com.recom.service;

import com.recom.persistence.dbcached.DBCachedPersistenceLayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DBCachedServiceTest {

    @Mock
    private DBCachedPersistenceLayer dbCachedPersistenceLayer;
    @InjectMocks
    private DBCachedService serviceUnderTest;

    @Test
    public void testProxyToDBCache_withCacheHit() {
        // Arrange
        final String cacheName = "cacheName";
        final String cacheKey = "cacheKey";
        final String cachedValue = "cachedValue";

        when(dbCachedPersistenceLayer.get(cacheName, cacheKey)).thenReturn(Optional.of(cachedValue));

        // Act
        final String resultToTest = serviceUnderTest.proxyToDBCache(cacheName, cacheKey, () -> "uncachedValue");

        // Assert
        assertEquals(cachedValue, resultToTest);
        verify(dbCachedPersistenceLayer, never()).put(anyString(), anyString(), any(Serializable.class));
    }

    @Test
    public void testProxyToDBCache_withCacheMiss() {
        // Arrange
        final String cacheName = "cacheName";
        final String cacheKey = "cacheKey";
        final String uncachedValue = "uncachedValue";

        when(dbCachedPersistenceLayer.get(cacheName, cacheKey)).thenReturn(Optional.empty());

        // Act
        final String resultToTest = serviceUnderTest.proxyToDBCache(cacheName, cacheKey, () -> uncachedValue);

        // Assert
        assertEquals(uncachedValue, resultToTest);
        verify(dbCachedPersistenceLayer).put(cacheName, cacheKey, uncachedValue);
    }

}