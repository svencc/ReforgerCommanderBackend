package com.recom.event.listener;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.TransactionalMapEntityPackage;
import com.recom.entity.MapEntity;
import com.recom.event.BaseRecomEventListener;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import com.recom.mapper.MapEntityMapper;
import com.recom.model.map.MapTransaction;
import com.recom.persistence.mapEntity.MapEntityPersistenceLayer;
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
abstract class TransactionalMapPackageBaseEventListener<PACKAGE_TYPE extends TransactionalMapEntityPackage> extends BaseRecomEventListener {

    @NonNull
    protected final TransactionTemplate transactionTemplate;
    @NonNull
    protected final ApplicationEventPublisher applicationEventPublisher;
    @NonNull
    protected final MapEntityPersistenceLayer mapEntityPersistenceLayer;
    @NonNull
    protected final MapTransactionValidatorService mapTransactionValidator;
    @Getter
    @NonNull
    protected final Map<String, MapTransaction<PACKAGE_TYPE>> transactions = new HashMap<>();

    protected void handleOpenTransaction(@NonNull final TransactionIdentifierDto transactionIdentifier) {
        final String sessionIdentifier = transactionIdentifier.getSessionIdentifier();
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction existingTransaction = transactions.get(sessionIdentifier);
            resetSession(existingTransaction);
            existingTransaction.setOpenTransactionIdentifier(transactionIdentifier);
            log.debug("Re-Open / clear transaction named {}!", transactionIdentifier.getSessionIdentifier());
        } else {
            final MapTransaction newTransaction = MapTransaction.builder()
                    .openTransactionIdentifier(transactionIdentifier)
                    .build();
            transactions.put(sessionIdentifier, newTransaction);
            log.debug("Open new transaction named {}!", transactionIdentifier.getSessionIdentifier());
        }
    }

    private static void resetSession(@NonNull final MapTransaction transaction) {
        transaction.setOpenTransactionIdentifier(null);
        transaction.setCommitTransactionIdentifier(null);
        transaction.getPackages().clear();
    }

    protected void handleAddMapPackage(@NonNull final PACKAGE_TYPE transactionalMapEntityPackage) {

        final String sessionIdentifier = transactionalMapEntityPackage.getSessionIdentifier();
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction existingTransaction = transactions.get(sessionIdentifier);
            existingTransaction.getPackages().add(transactionalMapEntityPackage.getEntities());
            log.debug("Added map entity package to transaction named {}!", sessionIdentifier);

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
            final MapTransaction existingTransaction = transactions.get(sessionIdentifier);
            if (existingTransaction.getCommitTransactionIdentifier() == null) {
                log.debug("Commit transaction named {}!", transactionIdentifier.getSessionIdentifier());
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
            final MapTransaction<PACKAGE_TYPE> existingTransaction = transactions.get(sessionIdentifier);
            if (mapTransactionValidator.isValidTransaction(existingTransaction)) {
                log.debug("Process transaction named {}!", sessionIdentifier);
                final List<MapEntity> distinctEntities = existingTransaction.getPackages().stream()
                        .flatMap(packageDto -> packageDto.getEntities().stream())
                        .distinct()
                        .map(MapEntityMapper.INSTANCE::toEntity)
                        .peek(mapEntity -> mapEntity.setMapName(sessionIdentifier))
                        .collect(Collectors.toList());

                final Boolean transactionExecuted = transactionTemplate.execute(status -> {
                    mapEntityPersistenceLayer.deleteMapEntities(sessionIdentifier);
                    mapEntityPersistenceLayer.saveAll(distinctEntities);
                    log.debug("Transaction named {} persisted!", sessionIdentifier);

                    return true;
                });

                return Optional.ofNullable(transactionExecuted).orElse(false);
            }
        }

        return false;
    }


}
