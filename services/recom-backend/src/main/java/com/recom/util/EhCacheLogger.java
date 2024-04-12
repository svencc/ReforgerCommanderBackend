package com.recom.util;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

@Slf4j
public class EhCacheLogger implements CacheEventListener<Object, Object> {

    @Override
    public void onEvent(final CacheEvent<?, ?> cacheEvent) {
        log.info(
                "Key: {} | EventType: {} | Old value: {} | New value: {}",
                cacheEvent.getKey(), cacheEvent.getType(), cacheEvent.getOldValue(), cacheEvent.getNewValue()
        );
    }

}
