package com.recom.event;

import com.recom.event.event.async.RecomAsyncEvent;
import com.recom.event.event.sync.RecomSyncEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseRecomEventListener {

    protected void logEvent(@NonNull final RecomAsyncEvent event) {
        log.debug("****** handle ASYNCHRONOUS {} created {}", event.getClass().getSimpleName(), event.getCreationDate());
    }

    protected void logEvent(@NonNull final RecomSyncEvent event) {
        log.debug("****** handle SYNCHRONOUS {} created {}", event.getClass().getSimpleName(), event.getCreationDate());
    }

}
