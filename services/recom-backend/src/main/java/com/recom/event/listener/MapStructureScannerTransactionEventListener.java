package com.recom.event.listener;

import com.recom.dto.map.scanner.structure.MapStructureDto;
import com.recom.dto.map.scanner.structure.TransactionalMapStructurePackageDto;
import com.recom.entity.map.structure.MapStructureEntity;
import com.recom.event.event.async.map.addmappackage.AddMapPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTransactionAsyncEvent;
import com.recom.event.listener.generic.maplocated.TransactionalMapLocatedPackageEventListenerTemplate;
import com.recom.mapper.mapstructure.MapStructureEntitySuperMapper;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.chunk.structure.MapStructureChunkPersistenceLayer;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.map.MapTransactionValidatorService;
import com.recom.service.messagebus.chunkscanrequest.MapStructureChunkScanRequestNotificationService;
import jakarta.persistence.EntityManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
public class MapStructureScannerTransactionEventListener extends TransactionalMapLocatedPackageEventListenerTemplate<TransactionalMapStructurePackageDto, MapStructureEntity, MapStructureDto> {

    @NonNull
    private final MapStructureChunkScanRequestNotificationService mapStructureChunkScanRequestNotificationService;


    public MapStructureScannerTransactionEventListener(
            @NonNull final EntityManager entityManager,
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final MapStructurePersistenceLayer entityPersistenceLayer,
            @NonNull final MapTransactionValidatorService<MapStructureDto, TransactionalMapStructurePackageDto> mapTransactionValidator,
            @NonNull final GameMapPersistenceLayer gameMapPersistenceLayer,
            @NonNull final ApplicationEventPublisher applicationEventPublisher,
            @NonNull final MapStructureChunkPersistenceLayer mapStructureChunkPersistenceLayer,
            @NonNull final MapStructureChunkScanRequestNotificationService mapStructureChunkScanRequestNotificationService
    ) {
        super(entityManager, transactionTemplate, entityPersistenceLayer, mapTransactionValidator, new MapStructureEntitySuperMapper(entityPersistenceLayer), gameMapPersistenceLayer, applicationEventPublisher, mapStructureChunkPersistenceLayer);

        this.mapStructureChunkScanRequestNotificationService = mapStructureChunkScanRequestNotificationService;
    }

    @Async("AsyncMapStructureTransactionExecutor")
    @EventListener(classes = OpenMapTransactionAsyncEvent.class)
    public void handleOpenTransactionEvent(@NonNull final OpenMapTransactionAsyncEvent event) {
        infoEvent(event);
        handleOpenTransaction(event.getTransactionIdentifierDto());
    }

    @Async("AsyncMapStructureTransactionExecutor")
    @EventListener(classes = AddMapPackageAsyncEvent.class)
    public void handleAddMapPackageEvent(@NonNull final AddMapPackageAsyncEvent event) {
        traceEvent(event);
        handleAddMapPackage(event.getTransactionalMapEntityPackage());
    }

    @Async("AsyncMapStructureTransactionExecutor")
    @EventListener(classes = CommitMapTransactionAsyncEvent.class)
    public void handleCommitTransactionEvent(@NonNull final CommitMapTransactionAsyncEvent event) {
        infoEvent(event);
        handleCommitTransaction(event.getTransactionIdentifierDto());
        mapStructureChunkScanRequestNotificationService.requestMapStructureChunkScan(event.getTransactionIdentifierDto());
    }

}
