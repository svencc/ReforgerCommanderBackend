package com.recom.event.listener.generic.maplocated;

import com.recom.entity.map.ChunkStatus;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerStructureChunk;
import com.recom.event.BaseRecomEntityScannerEventListener;
import com.recom.event.listener.generic.generic.MapLocatedEntityPersistable;
import com.recom.event.listener.generic.generic.TransactionalMapEntityPackable;
import com.recom.event.listener.util.ChunkCoordinate;
import com.recom.event.listener.util.ChunkHelper;
import com.recom.event.listener.util.MapScanSessionIdentifierData;
import com.recom.model.map.MapTransaction;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.chunk.structure.MapStructureChunkPersistenceLayer;
import com.recom.service.map.MapTransactionValidatorService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
    @TODO: Missing scheduler which removes open transactions after time x
 */
@Slf4j
public abstract class TransactionalMapLocatedPackageEventListenerTemplate<
        PACKAGE_TYPE extends TransactionalMapEntityPackable<DTO_TYPE>,
        ENTITY_TYPE extends MapLocatedEntity,
        DTO_TYPE extends MapLocatedDto>
        extends BaseRecomEntityScannerEventListener<PACKAGE_TYPE, DTO_TYPE> {

    @PersistenceContext
    private EntityManager entityManager;
    @NonNull
    protected final TransactionTemplate transactionTemplate;
    @NonNull
    protected final MapLocatedEntityPersistable<ENTITY_TYPE> entityPersistenceLayer;
    @NonNull
    protected final MapTransactionValidatorService<DTO_TYPE, PACKAGE_TYPE> mapTransactionValidator;
    @NonNull
    protected final TransactionalMapLocatedEntityMappable<ENTITY_TYPE, DTO_TYPE> entityMapper;
    @NonNull
    protected final GameMapPersistenceLayer gameMapPersistenceLayer;
    @NonNull
    protected final MapStructureChunkPersistenceLayer mapStructureChunkPersistenceLayer;


    public TransactionalMapLocatedPackageEventListenerTemplate(
            @NonNull final EntityManager entityManager,
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final MapLocatedEntityPersistable<ENTITY_TYPE> entityPersistenceLayer,
            @NonNull final MapTransactionValidatorService<DTO_TYPE, PACKAGE_TYPE> mapTransactionValidator,
            @NonNull final TransactionalMapLocatedEntityMappable<ENTITY_TYPE, DTO_TYPE> entityMapper,
            @NonNull final GameMapPersistenceLayer gameMapPersistenceLayer,
            @NonNull final ApplicationEventPublisher applicationEventPublisher,
            @NonNull final MapStructureChunkPersistenceLayer mapStructureChunkPersistenceLayer
    ) {
        super(applicationEventPublisher);
        this.entityManager = entityManager;
        this.transactionTemplate = transactionTemplate;
        this.entityPersistenceLayer = entityPersistenceLayer;
        this.mapTransactionValidator = mapTransactionValidator;
        this.entityMapper = entityMapper;
        this.gameMapPersistenceLayer = gameMapPersistenceLayer;
        this.mapStructureChunkPersistenceLayer = mapStructureChunkPersistenceLayer;
    }

    protected boolean processTransaction(@NonNull final String sessionIdentifier) {
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction<DTO_TYPE, PACKAGE_TYPE> existingTransaction = transactions.get(sessionIdentifier);
            if (mapTransactionValidator.isValidTransaction(existingTransaction)) {
                log.info("Process transaction named {}!", sessionIdentifier);

                final MapScanSessionIdentifierData mapScanSessionIdentifierData = ChunkHelper.extractFromSessionIdentifier(sessionIdentifier);
                final String mapName = mapScanSessionIdentifierData.mapName();
                final Optional<GameMap> maybeGameMap = gameMapPersistenceLayer.findByName(mapName);

                if (maybeGameMap.isPresent()) {
                    final ChunkCoordinate chunkCoordinate = ChunkHelper.extractChunkCoordinateFromSessionIdentifier(sessionIdentifier);
                    final Optional<SquareKilometerStructureChunk> maybeChunk = mapStructureChunkPersistenceLayer.findByGameMapAndCoordinate(maybeGameMap.get(), chunkCoordinate);
                    if (maybeChunk.isPresent()) {
                        log.info("... found existing chunk for transaction named {}!", sessionIdentifier);

                        // open new transaction
                        final Boolean transactionExecuted = transactionTemplate.execute(status -> {
                            final long startTransaction = System.currentTimeMillis();
                            log.debug("... execute transaction {}!", sessionIdentifier);
                            log.debug("... merge gameMap");
                            final GameMap gameMap = entityManager.merge(maybeGameMap.get());
                            log.debug("... merge mapChunk");
                            final SquareKilometerStructureChunk mapChunk = entityManager.merge(maybeChunk.get());

                            log.debug("... init entity mapper!");
                            entityMapper.init();
                            mapChunk.setStatus(ChunkStatus.CLOSED);

                            log.debug("... set gameMap and mapChunk to entities!");
                            final List<ENTITY_TYPE> distinctEntities = existingTransaction.getPackages().stream()
                                    .flatMap(packageDto -> packageDto.getEntities().stream())
                                    .distinct()
                                    .map(entityMapper::toEntity)
                                    .peek(mapEntity -> mapEntity.setGameMap(gameMap))
                                    .peek(mapEntity -> mapEntity.setSquareKilometerStructureChunk(mapChunk))
                                    .collect(Collectors.toList());

                            log.info("... persist {} entities.", distinctEntities.size());

                            entityPersistenceLayer.saveAll(distinctEntities);
                            mapStructureChunkPersistenceLayer.save(mapChunk);
                            log.info("Transaction named {} persisted in {}ms!", sessionIdentifier, System.currentTimeMillis() - startTransaction);

                            return true;
                        });

                        return Optional.ofNullable(transactionExecuted).orElse(false);
                    } else {
                        log.info("... no existing chunk found for transaction named {}!", sessionIdentifier);
                        return false;
                    }

                } else {
                    log.warn("No map meta found for transaction named {}!", sessionIdentifier);
                    return false;
                }
            } else {
                log.warn("Invalid Transaction {}!", sessionIdentifier);
                return false;
            }
        } else {
            log.warn("No transaction named {} found to process!", sessionIdentifier);
            return false;
        }
    }

}