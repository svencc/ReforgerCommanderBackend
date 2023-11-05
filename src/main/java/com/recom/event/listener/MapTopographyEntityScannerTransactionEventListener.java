package com.recom.event.listener;

import com.recom.event.event.async.map.addmappackage.AddMapTopographyPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTransactionAsyncEvent;
import com.recom.event.event.async.map.addmappackage.AddMapPackageAsyncEvent;
import com.recom.persistence.mapEntity.MapEntityPersistenceLayer;
import com.recom.service.map.scanner.MapTransactionValidatorService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
public class MapTopographyEntityScannerTransactionEventListener extends TransactionalMapPackageBaseEventListener {

    public MapTopographyEntityScannerTransactionEventListener(
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final ApplicationEventPublisher applicationEventPublisher,
            @NonNull final MapEntityPersistenceLayer mapEntityPersistenceLayer,
            @NonNull final MapTransactionValidatorService mapTransactionValidator
    ) {
        super(transactionTemplate, applicationEventPublisher, mapEntityPersistenceLayer, mapTransactionValidator);
    }

    @Async("AsyncMapTopographyTransactionExecutor")
    @EventListener(classes = OpenMapTopographyTransactionAsyncEvent.class)
    public void handleOpenTransactionEvent(@NonNull final OpenMapTopographyTransactionAsyncEvent event) {
        logEvent(event);
//        handleOpenTransaction(event.getTransactionIdentifierDto());
    }

    @Async("AsyncMapTopographyTransactionExecutor")
    @EventListener(classes = AddMapTopographyPackageAsyncEvent.class)
    public void handleAddMapPackageEvent(@NonNull final AddMapTopographyPackageAsyncEvent event) {
        logEvent(event);
//        handleAddMapPackage(event.getTransactionalMapEntityPackageDto());
    }

    @Async("AsyncMapTopographyTransactionExecutor")
    @EventListener(classes = CommitMapTopographyTransactionAsyncEvent.class)
    public void handleCommitTransactionEvent(@NonNull final CommitMapTopographyTransactionAsyncEvent event) {
        logEvent(event);
//        handleCommitTransaction(event.getTransactionIdentifierDto());
    }

}
