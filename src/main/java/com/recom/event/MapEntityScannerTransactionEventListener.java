package com.recom.event;

import com.recom.entity.MapEntity;
import com.recom.event.event.async.map.AddMapPackageAsyncEvent;
import com.recom.event.event.async.map.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.OpenMapTransactionAsyncEvent;
import com.recom.mapper.MapEntityMapper;
import com.recom.model.map.MapTransaction;
import com.recom.repository.mapEntity.MapEntityPersistenceLayer;
import com.recom.service.map.scanner.MapTransactionValidatorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
    @TODO: Missing scheduler which removes open transactions after time x
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MapEntityScannerTransactionEventListener extends BaseEventListener {

    @NonNull
    private final MapEntityPersistenceLayer mapEntityPersistenceLayer;
    @NonNull
    private final MapTransactionValidatorService mapTransactionValidator;
    @NonNull
    private final Map<String, MapTransaction> transactions = new HashMap<>();

    @Async("AsyncMapTransactionExecutor")
    @EventListener(classes = OpenMapTransactionAsyncEvent.class)
    public void handleOpenTransaction(@NonNull final OpenMapTransactionAsyncEvent event) {
        logEvent(event);
        final String sessionIdentifier = event.getTransactionIdentifierDto().getSessionIdentifier();
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction existingTransaction = transactions.get(sessionIdentifier);
            resetSession(existingTransaction);
            existingTransaction.setOpenTransactionIdentifier(event.getTransactionIdentifierDto());
            log.info("Re-Open / clear transaction named {}!", event.getTransactionIdentifierDto().getSessionIdentifier());
        } else {
            final MapTransaction newTransaction = MapTransaction.builder()
                    .openTransactionIdentifier(event.getTransactionIdentifierDto())
                    .build();
            transactions.put(sessionIdentifier, newTransaction);
            log.info("Open new transaction named {}!", event.getTransactionIdentifierDto().getSessionIdentifier());
        }
    }

    private static void resetSession(@NonNull final MapTransaction transaction) {
        transaction.setOpenTransactionIdentifier(null);
        transaction.setCommitTransactionIdentifier(null);
        transaction.getPackages().clear();
    }

    @Async("AsyncMapTransactionExecutor")
    @EventListener(classes = AddMapPackageAsyncEvent.class)
    public void handleAddMapPackage(@NonNull final AddMapPackageAsyncEvent event) {
        logEvent(event);
        final String sessionIdentifier = event.getTransactionalEntityPackageDto().getSessionIdentifier();
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction existingTransaction = transactions.get(sessionIdentifier);
            existingTransaction.getPackages().add(event.getTransactionalEntityPackageDto());
            log.debug("Added map entitiy package to transaction named {}!", sessionIdentifier);

            if (existingTransaction.isCommitted()) {
                log.debug("Try to process transaction {}, in case that transaction-commit-message took over a data package!", sessionIdentifier);
                boolean isProcessed = processTransaction(sessionIdentifier);
                if (isProcessed) {
                    transactions.remove(sessionIdentifier);
                    log.info("Transaction named {} committed and removed from stack!", sessionIdentifier);
                }
            }
        } else {
            log.warn("No transaction named {} found to append data!", sessionIdentifier);
        }
    }

    private boolean processTransaction(@NonNull final String sessionIdentifier) {
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction existingTransaction = transactions.get(sessionIdentifier);
            if (mapTransactionValidator.isValidTransaction(existingTransaction)) {
                log.info("Process transaction named {}!", sessionIdentifier);
                final List<MapEntity> distinctEntities = existingTransaction.getPackages().stream()
                        .flatMap(packageDto -> packageDto.getEntities().stream())
                        .distinct()
                        .map(MapEntityMapper.INSTANCE::toEntity)
                        .peek(mapEntity -> mapEntity.setMapName(sessionIdentifier))
                        .collect(Collectors.toList());

                mapEntityPersistenceLayer.deleteMapEntities(sessionIdentifier);
                mapEntityPersistenceLayer.saveAll(distinctEntities);
                log.info("Transaction named {} persisted!", sessionIdentifier);

                return true;
            }
        }

        return false;
    }

    @Async("AsyncMapTransactionExecutor")
    @EventListener(classes = CommitMapTransactionAsyncEvent.class)
    public void handleCommitTransaction(@NonNull final CommitMapTransactionAsyncEvent event) {
        logEvent(event);
        final String sessionIdentifier = event.getTransactionIdentifierDto().getSessionIdentifier();
        if (transactions.containsKey(sessionIdentifier)) {
            final MapTransaction existingTransaction = transactions.get(sessionIdentifier);
            if (existingTransaction.getCommitTransactionIdentifier() == null) {
                log.info("Commit transaction named {}!", event.getTransactionIdentifierDto().getSessionIdentifier());
                existingTransaction.setCommitTransactionIdentifier(event.getTransactionIdentifierDto());
                processTransaction(sessionIdentifier);
            } else {
                log.warn("Transaction named {} is already committed!", event.getTransactionIdentifierDto().getSessionIdentifier());
            }
        } else {
            log.warn("No transaction named {} to commit!", event.getTransactionIdentifierDto().getSessionIdentifier());
        }
    }

}