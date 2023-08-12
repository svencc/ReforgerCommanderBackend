package com.recom.service.dbcached;

import com.recom.service.MutexService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncCacheableRequestProcessorTest {

    @Mock
    private MutexService mutexService;
    @Mock
    private DBCachedManager dbCachedManager;
    @Mock
    private ExecutorProvider executorProvider;
    @InjectMocks
    private AsyncCacheableRequestProcessor serviceUnderTest;

    @Test
    void testProcessRequestWithAsyncCache_whenCacheHit_returnOKWithResult() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";
        final Supplier<Optional<String>> cacheLoader = mock(Supplier.class);


        when(dbCachedManager.isCached(cacheName, cacheKey)).thenReturn(true);
        when(dbCachedManager.get(cacheName, cacheKey)).thenReturn(Optional.of("CachedValue"));

        // Act
        final ResponseEntity<String> responseEntityToTest = serviceUnderTest.processRequestWithAsyncCache(cacheName, cacheKey, cacheLoader);

        // Assert
        assertEquals(HttpStatus.OK, responseEntityToTest.getStatusCode());
        assertEquals("CachedValue", responseEntityToTest.getBody());
        verify(cacheLoader, never()).get();
    }

    @Test
    void testProcessRequestWithAsyncCache_whenCacheMiss_returnAccepted() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";
        final Supplier<Optional<String>> cacheLoader = mock(Supplier.class);
        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.initialize();

        when(cacheLoader.get()).thenReturn(Optional.of("NewValue"));
        when(dbCachedManager.isCached(cacheName, cacheKey)).thenReturn(false);
        when(mutexService.claim(anyString())).thenReturn(true);
        when(executorProvider.provideClusterGeneratorExecutor()).thenReturn(threadPoolTaskExecutor);

        // Act
        final ResponseEntity<String> responseEntityToTest = serviceUnderTest.processRequestWithAsyncCache(cacheName, cacheKey, cacheLoader);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, responseEntityToTest.getStatusCode());
        assertNull(responseEntityToTest.getBody());

        // Verify that the cache put method was called
        verify(dbCachedManager, times(1)).put(eq(cacheName), eq(cacheKey), eq("NewValue"));
        verify(cacheLoader, times(1)).get();
    }

    @Test
    void testProcessRequestWithAsyncCache_whenMutexClaimFailed_returnAccepted() {
        // Arrange
        final String cacheName = "testCache";
        final String cacheKey = "testKey";
        final Supplier<Optional<String>> cacheLoader = mock(Supplier.class);

        when(dbCachedManager.isCached(cacheName, cacheKey)).thenReturn(false);
        when(mutexService.claim(anyString())).thenReturn(false);

        // Act
        final ResponseEntity<String> responseEntityToTest = serviceUnderTest.processRequestWithAsyncCache(cacheName, cacheKey, cacheLoader);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, responseEntityToTest.getStatusCode());
        assertNull(responseEntityToTest.getBody());

        // Verify that the cache put method and cacheLoader were not called
        verify(dbCachedManager, never()).put(anyString(), anyString(), any());
        verify(cacheLoader, never()).get();
    }

}