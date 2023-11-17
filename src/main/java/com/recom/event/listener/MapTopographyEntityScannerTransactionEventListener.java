package com.recom.event.listener;

import com.recom.dto.map.scanner.topography.MapTopographyEntityDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import com.recom.entity.GameMap;
import com.recom.entity.MapTopography;
import com.recom.event.event.async.map.addmappackage.AddMapTopographyPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTopographyTransactionAsyncEvent;
import com.recom.event.listener.generic.maprelated.TransactionalMapRelatedPackageEventListenerTemplate;
import com.recom.model.map.MapTransaction;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.topography.MapLocatedTopographyPersistenceLayer;
import com.recom.service.map.MapTransactionValidatorService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MapTopographyEntityScannerTransactionEventListener extends TransactionalMapRelatedPackageEventListenerTemplate<TransactionalMapTopographyEntityPackageDto, MapTopography, MapTopographyEntityDto> {

    public MapTopographyEntityScannerTransactionEventListener(
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final MapLocatedTopographyPersistenceLayer entityPersistenceLayer,
            @NonNull final MapTransactionValidatorService<MapTopographyEntityDto, TransactionalMapTopographyEntityPackageDto> mapTransactionValidator,
            @NonNull final GameMapPersistenceLayer gameMapPersistenceLayer,
            @NonNull final ApplicationEventPublisher applicationEventPublisher
    ) {
        super(transactionTemplate, entityPersistenceLayer, mapTransactionValidator, gameMapPersistenceLayer, applicationEventPublisher);
    }

    @Async("AsyncMapTopographyTransactionExecutor")
    @EventListener(classes = OpenMapTopographyTransactionAsyncEvent.class)
    public void handleOpenTransactionEvent(@NonNull final OpenMapTopographyTransactionAsyncEvent event) {
        debugEvent(event);
        handleOpenTransaction(event.getTransactionIdentifierDto());
        Map<String, MapTransaction<MapTopographyEntityDto, TransactionalMapTopographyEntityPackageDto>> transactions1 = getTransactions();
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

    @NonNull
    @Override
    protected MapTopography mapTransactionToEntity(
            @NonNull final GameMap mapMeta,
            @NonNull final List<TransactionalMapTopographyEntityPackageDto> packages
    ) {
        return null;
    }
}
