package com.recom.event.listener.generic.maprelated;

import com.recom.entity.map.GameMap;
import com.recom.event.BaseRecomEntityScannerEventListener;
import com.recom.event.listener.generic.generic.MapRelatedEntityPersistable;
import com.recom.event.listener.generic.generic.TransactionalMapEntityPackable;
import com.recom.event.listener.util.ChunkHelper;
import com.recom.model.map.MapTransaction;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.service.map.MapTransactionValidatorService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

/*
    @TODO: Missing scheduler which removes open transactions after time x
 */
@Slf4j
public abstract class TransactionalMapRelatedPackageEventListenerTemplate<
        PACKAGE_TYPE extends TransactionalMapEntityPackable<DTO_TYPE>,
        ENTITY_TYPE extends MapRelatedEntity,
        DTO_TYPE extends MapRelatedDto>
        extends BaseRecomEntityScannerEventListener<PACKAGE_TYPE, DTO_TYPE> {

    @NonNull
    protected final TransactionTemplate transactionTemplate;
    @NonNull
    protected final MapRelatedEntityPersistable<ENTITY_TYPE> entityPersistenceLayer;
    @NonNull
    protected final MapTransactionValidatorService<DTO_TYPE, PACKAGE_TYPE> mapTransactionValidator;
    @NonNull
    protected final GameMapPersistenceLayer gameMapPersistenceLayer;


    public TransactionalMapRelatedPackageEventListenerTemplate(
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final MapRelatedEntityPersistable<ENTITY_TYPE> entityPersistenceLayer,
            @NonNull final MapTransactionValidatorService<DTO_TYPE, PACKAGE_TYPE> mapTransactionValidator,
            @NonNull final GameMapPersistenceLayer gameMapPersistenceLayer,
            @NonNull final ApplicationEventPublisher applicationEventPublisher
    ) {
        super(applicationEventPublisher);
        this.transactionTemplate = transactionTemplate;
        this.entityPersistenceLayer = entityPersistenceLayer;
        this.mapTransactionValidator = mapTransactionValidator;
        this.gameMapPersistenceLayer = gameMapPersistenceLayer;
    }

    //    $RECOMClient:worlds/Arland_CTI/Arland_CTI.ent#####0,4
    protected boolean processTransaction(@NonNull final String sessionIdentifier) {
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction<DTO_TYPE, PACKAGE_TYPE> existingTransaction = transactions.get(sessionIdentifier);
            if (mapTransactionValidator.isValidTransaction(existingTransaction)) {
                log.info("Process transaction named {}!", sessionIdentifier);
                final String mapName = ChunkHelper.extractFromSessionIdentifier(sessionIdentifier).mapName();
                final Optional<GameMap> maybeGameMap = gameMapPersistenceLayer.findByName(mapName);

                if (maybeGameMap.isPresent()) {
                    log.info("... found existing chunk for transaction named {}!", sessionIdentifier);
                    final ENTITY_TYPE entity = mapTransactionToEntity(sessionIdentifier, maybeGameMap.get(), existingTransaction.getPackages());

                    final Boolean transactionExecuted = transactionTemplate.execute(status -> {
                        final long startTransaction = System.currentTimeMillis();
                        log.debug("... execute transaction {}!", sessionIdentifier);
                        entityPersistenceLayer.save(entity);
                        log.info("Transaction named {} persisted in {}ms!", sessionIdentifier, System.currentTimeMillis() - startTransaction);

                        return true;
                    });

                    return Optional.ofNullable(transactionExecuted).orElse(false);
                } else {
                    log.warn("No map meta found for transaction named {}!", sessionIdentifier);
                    return false;
                }
            }
        }

        return false;
    }

    @NonNull
    protected abstract ENTITY_TYPE mapTransactionToEntity(
            @NonNull final String sessionIdentifier,
            @NonNull final GameMap mapMeta,
            @NonNull final List<PACKAGE_TYPE> packages
    );

}