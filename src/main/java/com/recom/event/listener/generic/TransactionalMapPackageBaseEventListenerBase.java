package com.recom.event.listener.generic;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.event.BaseRecomEventListener;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import com.recom.model.map.MapTransaction;
import com.recom.service.map.MapTransactionValidatorService;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/*
    @TODO: Missing scheduler which removes open transactions after time x
 */
@Slf4j
@RequiredArgsConstructor
public abstract class TransactionalMapPackageBaseEventListenerBase<
        PACKAGE_TYPE extends TransactionalMapEntityPackable<DTO_TYPE>,
        ENTITY_TYPE extends MapLocatedEntity,
        DTO_TYPE extends MapLocatedDto>
        extends BaseRecomEventListener {

    @NonNull
    protected final TransactionTemplate transactionTemplate;
    @NonNull
    protected final ApplicationEventPublisher applicationEventPublisher;
    @NonNull
    protected final MapEntityPersistable<ENTITY_TYPE> entityPersistenceLayer;
    @NonNull
    protected final MapTransactionValidatorService<DTO_TYPE, PACKAGE_TYPE> mapTransactionValidator;
    @Getter
    @NonNull
    protected final Map<String, MapTransaction<DTO_TYPE, PACKAGE_TYPE>> transactions = new HashMap<>();
    @NonNull
    protected final TransactionalMapEntityMappable<ENTITY_TYPE, DTO_TYPE> entityMapper;

    protected void handleOpenTransaction(@NonNull final TransactionIdentifierDto transactionIdentifier) {
        final String sessionIdentifier = transactionIdentifier.getSessionIdentifier();
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction<DTO_TYPE, PACKAGE_TYPE> existingTransaction = transactions.get(sessionIdentifier);
            resetSession(existingTransaction);
            existingTransaction.setOpenTransactionIdentifier(transactionIdentifier);
            log.info("Re-Open / clear transaction named {}!", transactionIdentifier.getSessionIdentifier());
        } else {
            final MapTransaction<DTO_TYPE, PACKAGE_TYPE> newTransaction = MapTransaction.<DTO_TYPE, PACKAGE_TYPE>builder()
                    .openTransactionIdentifier(transactionIdentifier)
                    .build();
            transactions.put(sessionIdentifier, newTransaction);
            log.info("Open new transaction named {}!", transactionIdentifier.getSessionIdentifier());
        }
    }

    private void resetSession(@NonNull final MapTransaction<DTO_TYPE, PACKAGE_TYPE> transaction) {
        transaction.setOpenTransactionIdentifier(null);
        transaction.setCommitTransactionIdentifier(null);
        transaction.getPackages().clear();
    }

    protected void handleAddMapPackage(@NonNull final PACKAGE_TYPE transactionalMapEntityPackage) {
        final String sessionIdentifier = transactionalMapEntityPackage.getSessionIdentifier();
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction<DTO_TYPE, PACKAGE_TYPE> existingTransaction = transactions.get(sessionIdentifier);
            existingTransaction.getPackages().add(transactionalMapEntityPackage);
            log.trace("Added map entity package to transaction named {}!", sessionIdentifier);

            // EDGE CASE: If transaction is already committed, process it immediately
            // Try to process already committed transaction, in case that transaction-commit-message took over a data package!
            if (existingTransaction.isCommitted()) {
                log.debug("Try to process transaction {}, in case that transaction-commit-message took over a data package!", sessionIdentifier);
                boolean isProcessed = processTransaction(sessionIdentifier);
                if (isProcessed) {
                    transactions.remove(sessionIdentifier);
                    log.debug("Transaction named {} committed and removed from stack!", sessionIdentifier);
                    applicationEventPublisher.publishEvent(new CacheResetSyncEvent());
                }
            }
        } else {
            log.warn("No transaction named {} found to append data!", sessionIdentifier);
        }
    }

    protected void handleCommitTransaction(@NonNull final TransactionIdentifierDto transactionIdentifier) {
        final String sessionIdentifier = transactionIdentifier.getSessionIdentifier();
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction<DTO_TYPE, PACKAGE_TYPE> existingTransaction = transactions.get(sessionIdentifier);
            if (existingTransaction.getCommitTransactionIdentifier() == null) {
                log.info("Commit transaction named {}!", transactionIdentifier.getSessionIdentifier());
                existingTransaction.setCommitTransactionIdentifier(transactionIdentifier);
                processTransaction(sessionIdentifier);
            } else {
                log.warn("Transaction named {} is already committed!", transactionIdentifier.getSessionIdentifier());
            }
        } else {
            log.warn("No transaction named {} to commit!", transactionIdentifier.getSessionIdentifier());
        }
    }

    private boolean processTransaction(@NonNull final String sessionIdentifier) {
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction<DTO_TYPE, PACKAGE_TYPE> existingTransaction = transactions.get(sessionIdentifier);
            if (mapTransactionValidator.isValidTransaction(existingTransaction)) {
                log.info("Process transaction named {}!", sessionIdentifier);
                final List<ENTITY_TYPE> distinctEntities = existingTransaction.getPackages().stream()
                        .flatMap(packageDto -> packageDto.getEntities().stream())
                        .distinct()
                        .map(entityMapper::toEntity)
                        .peek(mapEntity -> mapEntity.setMapName(sessionIdentifier))
                        .collect(Collectors.toList());

                log.info("... persist {} entities.", distinctEntities.size());

                final Boolean transactionExecuted = transactionTemplate.execute(status -> {
                    entityPersistenceLayer.deleteMapEntities(sessionIdentifier);
                    entityPersistenceLayer.saveAll(distinctEntities);
                    log.info("Transaction named {} persisted!", sessionIdentifier);

                    return true;
                });

                return Optional.ofNullable(transactionExecuted).orElse(false);
            }
        }

        return false;
    }

}
