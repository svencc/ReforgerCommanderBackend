package com.recom.service.dbcached;

import com.recom.exception.DBCachedDeserializationException;
import com.recom.persistence.dbcached.DBCachedPersistenceLayer;
import com.recom.service.cache.dbcached.DBCachedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DBCachedServiceTest {

    @Mock
    private DBCachedPersistenceLayer dbCachedPersistenceLayer;
    @InjectMocks
    private DBCachedService serviceUnderTest;

    @Test
    public void testProxyToDBCacheSafe_withCacheHit() {
        // Arrange
        final String cacheName = "cacheName";
        final String cacheKey = "cacheKey";
        final String cachedValue = "cachedValue";

        when(dbCachedPersistenceLayer.get(cacheName, cacheKey)).thenReturn(Optional.of(cachedValue));

        // Act
        final String resultToTest = serviceUnderTest.proxyToDBCacheSafe(cacheName, cacheKey, () -> "uncachedValue");

        // Assert
        assertEquals(cachedValue, resultToTest);
        verify(dbCachedPersistenceLayer, never()).put(anyString(), anyString(), any(Serializable.class));
    }

    @Test
    public void testProxyToDBCacheSafe_withCacheMiss() {
        // Arrange
        final String cacheName = "cacheName";
        final String cacheKey = "cacheKey";
        final String uncachedValue = "uncachedValue";

        when(dbCachedPersistenceLayer.get(cacheName, cacheKey)).thenReturn(Optional.empty());

        // Act
        final String resultToTest = serviceUnderTest.proxyToDBCacheSafe(cacheName, cacheKey, () -> uncachedValue);

        // Assert
        assertEquals(uncachedValue, resultToTest);
        verify(dbCachedPersistenceLayer).put(cacheName, cacheKey, uncachedValue);
    }


    @Test
    public void testProxyToDBCacheUnsafe_CacheHit() throws DBCachedDeserializationException {
        // Arrange
        final String cacheName = "cacheName";
        final String cacheKey = "cacheKey";
        final String cachedValue = "cachedValue";

        when(dbCachedPersistenceLayer.get(cacheName, cacheKey)).thenReturn(Optional.of(cachedValue));

        // Act
        final String resultToTest = serviceUnderTest.proxyToDBCacheUnsafe(cacheName, cacheKey, () -> "fallbackValue");

        // Assert
        assertEquals(cachedValue, resultToTest);
        verify(dbCachedPersistenceLayer, times(1)).get(cacheName, cacheKey);
        verify(dbCachedPersistenceLayer, never()).put(anyString(), anyString(), any());
    }

    @Test
    public void testProxyToDBCacheUnsafe_CacheMiss() throws DBCachedDeserializationException {
        // Arrange
        final String cacheName = "cacheName";
        final String missingKey = "missingKey";
        final String fallbackValue = "fallbackValue";

        when(dbCachedPersistenceLayer.get(cacheName, missingKey)).thenReturn(Optional.empty());

        // Act
        final String resultToTest = serviceUnderTest.proxyToDBCacheUnsafe(cacheName, missingKey, () -> fallbackValue);

        // Assert
        assertEquals(fallbackValue, resultToTest);
        verify(dbCachedPersistenceLayer, times(1)).get(cacheName, missingKey);
        verify(dbCachedPersistenceLayer, times(1)).put(cacheName, missingKey, fallbackValue);
    }


    @Test
    public void testProxyToDBCache_withDBCachedDeserializationException() throws DBCachedDeserializationException {
        // Arrange
        final String cacheName = "cacheName";
        final String errorKey = "missingKey";

        when(dbCachedPersistenceLayer.get(cacheName, errorKey)).thenThrow(DBCachedDeserializationException.class);

        // Act
        assertThrows(DBCachedDeserializationException.class, () -> {
            serviceUnderTest.proxyToDBCacheUnsafe(cacheName, errorKey, () -> "value-never-returned");
        });

        // Assert
        verify(dbCachedPersistenceLayer, times(1)).get(cacheName, errorKey);
        verify(dbCachedPersistenceLayer, never()).put(anyString(), anyString(), any());
    }

}