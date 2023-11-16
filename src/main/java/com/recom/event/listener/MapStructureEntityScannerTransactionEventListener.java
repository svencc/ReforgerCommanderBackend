package com.recom.event.listener;

import com.recom.dto.map.scanner.structure.MapStructureEntityDto;
import com.recom.dto.map.scanner.structure.TransactionalMapStructureEntityPackageDto;
import com.recom.entity.MapStructureEntity;
import com.recom.event.event.async.map.addmappackage.AddMapPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTransactionAsyncEvent;
import com.recom.event.listener.generic.maplocated.TransactionalMapLocatedPackageEventListenerTemplate;
import com.recom.mapper.MapStructureEntityMapper;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
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
public class MapStructureEntityScannerTransactionEventListener extends TransactionalMapLocatedPackageEventListenerTemplate<TransactionalMapStructureEntityPackageDto, MapStructureEntity, MapStructureEntityDto> {

    public MapStructureEntityScannerTransactionEventListener(
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final ApplicationEventPublisher applicationEventPublisher,
            @NonNull final MapStructurePersistenceLayer entityPersistenceLayer,
            @NonNull final MapTransactionValidatorService<MapStructureEntityDto, TransactionalMapStructureEntityPackageDto> mapTransactionValidator,
            @NonNull final GameMapPersistenceLayer gameMapPersistenceLayer
    ) {
        super(transactionTemplate, applicationEventPublisher, entityPersistenceLayer, mapTransactionValidator, MapStructureEntityMapper.INSTANCE, gameMapPersistenceLayer);
    }

    @Async("AsyncMapTransactionExecutor")
    @EventListener(classes = OpenMapTransactionAsyncEvent.class)
    public void handleOpenTransactionEvent(@NonNull final OpenMapTransactionAsyncEvent event) {
        infoEvent(event);
        handleOpenTransaction(event.getTransactionIdentifierDto());
    }

    @Async("AsyncMapTransactionExecutor")
    @EventListener(classes = AddMapPackageAsyncEvent.class)
    public void handleAddMapPackageEvent(@NonNull final AddMapPackageAsyncEvent event) {
        traceEvent(event);
        handleAddMapPackage(event.getTransactionalMapEntityPackage());
    }

    @Async("AsyncMapTransactionExecutor")
    @EventListener(classes = CommitMapTransactionAsyncEvent.class)
    public void handleCommitTransactionEvent(@NonNull final CommitMapTransactionAsyncEvent event) {
        infoEvent(event);
        handleCommitTransaction(event.getTransactionIdentifierDto());
    }

}
