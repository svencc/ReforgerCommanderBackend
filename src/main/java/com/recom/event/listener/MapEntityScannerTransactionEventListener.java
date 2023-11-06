package com.recom.event.listener;

import com.recom.dto.map.scanner.map.MapEntityDto;
import com.recom.dto.map.scanner.map.TransactionalMapEntityPackageDto;
import com.recom.entity.MapEntity;
import com.recom.event.event.async.map.addmappackage.AddMapPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTransactionAsyncEvent;
import com.recom.event.listener.generic.TransactionalMapPackageBaseEventListener;
import com.recom.mapper.MapEntityMapper;
import com.recom.persistence.mapEntity.MapEntityPersistenceLayer;
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
public class MapEntityScannerTransactionEventListener extends TransactionalMapPackageBaseEventListener<TransactionalMapEntityPackageDto, MapEntity, MapEntityDto> {

    public MapEntityScannerTransactionEventListener(
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final ApplicationEventPublisher applicationEventPublisher,
            @NonNull final MapEntityPersistenceLayer entityPersistenceLayer,
            @NonNull final MapTransactionValidatorService<MapEntityDto, TransactionalMapEntityPackageDto> mapTransactionValidator
    ) {
        super(transactionTemplate, applicationEventPublisher, entityPersistenceLayer, mapTransactionValidator, MapEntityMapper.INSTANCE);
    }

    @Async("AsyncMapTransactionExecutor")
    @EventListener(classes = OpenMapTransactionAsyncEvent.class)
    public void handleOpenTransactionEvent(@NonNull final OpenMapTransactionAsyncEvent event) {
        logEvent(event);
        handleOpenTransaction(event.getTransactionIdentifierDto());
    }

    @Async("AsyncMapTransactionExecutor")
    @EventListener(classes = AddMapPackageAsyncEvent.class)
    public void handleAddMapPackageEvent(@NonNull final AddMapPackageAsyncEvent event) {
        logEvent(event);
        handleAddMapPackage(event.getTransactionalMapEntityPackage());
    }

    @Async("AsyncMapTransactionExecutor")
    @EventListener(classes = CommitMapTransactionAsyncEvent.class)
    public void handleCommitTransactionEvent(@NonNull final CommitMapTransactionAsyncEvent event) {
        logEvent(event);
        handleCommitTransaction(event.getTransactionIdentifierDto());
    }

}
