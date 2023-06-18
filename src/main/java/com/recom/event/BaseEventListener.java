package com.recom.event;

import com.recom.event.event.async.RecomAsyncEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class BaseEventListener {

    protected void logEvent(@NonNull final RecomAsyncEvent event) {
        log.debug("****** handle {} {} ", event, event.getCreationDate());
    }

}
