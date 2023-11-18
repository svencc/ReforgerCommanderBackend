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
import com.recom.model.map.TopographyData;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
            @NonNull final GameMap gameMap,
            @NonNull final List<TransactionalMapTopographyEntityPackageDto> packages
    ) {
        final Float stepSize = packages.get(0).getEntities().get(0).getStepSize();
        final Integer scanIterationsX = packages.get(0).getEntities().get(0).getScanIterationsX();
        final Integer scanIterationsZ = packages.get(0).getEntities().get(0).getScanIterationsZ();
        final Float oceanBaseHeight = packages.get(0).getEntities().get(0).getOceanBaseHeight();

        final float[][] surfaceData = new float[scanIterationsX][scanIterationsZ];

        packages.stream()
                .flatMap((entityPackage) -> entityPackage.getEntities().stream())
                .forEach((final MapTopographyEntityDto packageDto) -> {
                    surfaceData[packageDto.getIterationX()][packageDto.getIterationZ()] = packageDto.getCoordinates().get(1).floatValue();
                });

        final TopographyData topograpyModel = TopographyData.builder()
                .stepSize(stepSize)
                .scanIterationsX(scanIterationsX)
                .scanIterationsZ(scanIterationsZ)
                .oceanBaseHeight(oceanBaseHeight)
                .surfaceData(surfaceData)
                .build();

        try {
            return MapTopography.builder()
                    .gameMap(gameMap)
                    .data(serializeObject(topograpyModel).toByteArray())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private ByteArrayOutputStream serializeObject(@NonNull final Serializable object) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
        }

        return byteArrayOutputStream;
    }
}
