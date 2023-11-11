package com.recom.event.listener;

import com.recom.dto.map.scanner.topography.MapTopographyEntityDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import com.recom.entity.MapTopographyEntity;
import com.recom.event.event.async.map.addmappackage.AddMapTopographyPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTopographyTransactionAsyncEvent;
import com.recom.event.listener.generic.TransactionalMapPackageBaseEventListenerBase;
import com.recom.mapper.MapTopographyEntityMapper;
import com.recom.persistence.mapTopographyEntity.MapTopographyEntityPersistenceLayer;
import com.recom.service.map.MapTransactionValidatorService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
public class MapTopographyEntityScannerTransactionEventListener extends TransactionalMapPackageBaseEventListenerBase<TransactionalMapTopographyEntityPackageDto, MapTopographyEntity, MapTopographyEntityDto> {

    public MapTopographyEntityScannerTransactionEventListener(
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final ApplicationEventPublisher applicationEventPublisher,
            @NonNull final MapTopographyEntityPersistenceLayer entityPersistenceLayer,
            @NonNull final MapTransactionValidatorService<MapTopographyEntityDto, TransactionalMapTopographyEntityPackageDto> mapTransactionValidator
    ) {
        super(transactionTemplate, applicationEventPublisher, entityPersistenceLayer, mapTransactionValidator, MapTopographyEntityMapper.INSTANCE);
    }

    @Async("AsyncMapTopographyTransactionExecutor")
    @EventListener(classes = OpenMapTopographyTransactionAsyncEvent.class)
    public void handleOpenTransactionEvent(@NonNull final OpenMapTopographyTransactionAsyncEvent event) {
        debugEvent(event);
        handleOpenTransaction(event.getTransactionIdentifierDto());
    }

    @Async("AsyncMapTopographyTransactionExecutor")
    @EventListener(classes = AddMapTopographyPackageAsyncEvent.class)
    public void handleAddMapPackageEvent(@NonNull final AddMapTopographyPackageAsyncEvent event) {
        traceEvent(event);
        handleAddMapPackage(event.getTransactionalMapEntityPackage());
    }

    @Async("AsyncMapTopographyTransactionExecutor")
    @EventListener(classes = CommitMapTopographyTransactionAsyncEvent.class)
    public void handleCommitTransactionEvent(@NonNull final CommitMapTopographyTransactionAsyncEvent event) {
        debugEvent(event);
        handleCommitTransaction(event.getTransactionIdentifierDto());
    }

}
