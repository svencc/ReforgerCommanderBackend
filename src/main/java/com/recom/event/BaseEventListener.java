package com.recom.event;

import com.recom.event.event.async.RecomAsyncEvent;
import com.recom.event.event.sync.RecomSyncEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class BaseEventListener {

    protected void logEvent(@NonNull final RecomAsyncEvent event) {
        log.info("****** handle ASYNCHRONOUS {} created {}", event.getClass().getSimpleName(), event.getCreationDate());
    }

    protected void logEvent(@NonNull final RecomSyncEvent event) {
        log.info("****** handle SYNCHRONOUS {} created {}", event.getClass().getSimpleName(), event.getCreationDate());
    }

}
