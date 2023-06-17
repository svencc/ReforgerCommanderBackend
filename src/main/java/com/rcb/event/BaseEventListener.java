package com.rcb.event;

import com.rcb.event.event.async.RefComAsyncEvent;
import com.rcb.event.event.async.map.OpenMapTransactionAsyncEvent;
import com.rcb.model.map.MapTransaction;
import com.rcb.repository.mapEntity.MapEntityPersistenceLayer;
import com.rcb.service.map.scanner.MapTransactionValidatorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
abstract class BaseEventListener {

    protected void logEvent(@NonNull final RefComAsyncEvent event) {
        log.debug("****** handle {} {} ", event, event.getCreationDate());
    }

}
