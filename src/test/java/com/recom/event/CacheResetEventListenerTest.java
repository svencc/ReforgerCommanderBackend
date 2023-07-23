package com.recom.event;

import com.recom.event.event.async.cache.CacheResetAsyncEvent;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheResetEventListenerTest {

    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;
    @InjectMocks
    private CacheResetEventListener eventListenerToTest;

    @Captor
    private ArgumentCaptor<String> cacheNameCaptor;


    @Test
    public void testHandleCacheResetAsyncEvent_WithCacheResetAsyncEvent_shouldClearsAllCaches() throws InterruptedException {
        // Arrange
        final CacheResetAsyncEvent event = new CacheResetAsyncEvent();

        final List<String> cacheNames = List.of("cache1", "cache2");
        when(cacheManager.getCacheNames()).thenReturn(cacheNames);
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        // Act
        eventListenerToTest.handleCacheResetAsyncEvent(event);

        // wait 1 second here to give async event listener time to finish
        Thread.sleep(1000);

        // Assert
        verify(cacheManager, times(cacheNames.size())).getCache(cacheNameCaptor.capture());
        verify(cache, times(cacheNames.size())).clear();

        final List<String> capturedCacheNames = cacheNameCaptor.getAllValues();
        for (final String cacheName : cacheNames) {
            assert capturedCacheNames.contains(cacheName);
        }
    }

    @Test
    public void testHandleCacheResetSyncEvent_WithCacheResetSyncEvent_shouldClearsAllCaches() {
        // Arrange
        CacheResetSyncEvent event = new CacheResetSyncEvent();

        final List<String> cacheNames = List.of("cache1", "cache2");
        when(cacheManager.getCacheNames()).thenReturn(cacheNames);
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        // Act
        eventListenerToTest.handleCacheResetSyncEvent(event);

        // Assert
        verify(cacheManager, times(cacheNames.size())).getCache(cacheNameCaptor.capture());
        verify(cache, times(cacheNames.size())).clear();

        final List<String> capturedCacheNames = cacheNameCaptor.getAllValues();
        for (String cacheName : cacheNames) {
            assert capturedCacheNames.contains(cacheName);
        }
    }

}