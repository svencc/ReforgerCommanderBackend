package com.recom.event.listener;

import com.recom.entity.Account;
import com.recom.event.BaseRecomEventListener;
import com.recom.event.event.async.cache.CacheResetAsyncEvent;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import com.recom.service.dbcached.DBCachedManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheResetEventListener extends BaseRecomEventListener {

    @NonNull
    private final CacheManager cacheManager;
    @NonNull
    private final DBCachedManager dbCachedManager;
    @NonNull
    private final EntityManagerFactory entityManagerFactory;


    @Async("CacheResetExecutor")
    @EventListener(classes = CacheResetAsyncEvent.class)
    public void handleCacheResetAsyncEvent(@NonNull final CacheResetAsyncEvent event) {
        clearAllCaches();
        debugEvent(event);
    }

    private void clearAllCaches() {
        // Clear Application Caches
        cacheManager.getCacheNames().forEach(cacheName -> Optional.ofNullable(cacheManager.getCache(cacheName)).ifPresent(Cache::clear));

        // Clear Hibernate Level 2 Entity Caches
        entityManagerFactory.getCache().unwrap(org.hibernate.Cache.class).evictAllRegions(); // does not work?
//        entityManagerFactory.getCache().evictAll();
//        entityManagerFactory.getCache().evict(Account.class);

        // Clear DBCached Caches
        dbCachedManager.clearAll();
    }

    @EventListener(classes = CacheResetSyncEvent.class)
    public void handleCacheResetSyncEvent(@NonNull final CacheResetSyncEvent event) {
        clearAllCaches();
        debugEvent(event);
    }

}
