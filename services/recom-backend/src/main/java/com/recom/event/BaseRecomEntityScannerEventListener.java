package com.recom.event;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import com.recom.event.listener.generic.generic.MapDto;
import com.recom.event.listener.generic.generic.TransactionalMapEntityPackable;
import com.recom.model.map.MapTransaction;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseRecomEntityScannerEventListener<
        PACKAGE_TYPE extends TransactionalMapEntityPackable<DTO_TYPE>,
        DTO_TYPE extends MapDto
        > extends BaseRecomEventListener {

    @Getter
    @NonNull
    protected final Map<String, MapTransaction<DTO_TYPE, PACKAGE_TYPE>> transactions = new HashMap<>();
    @NonNull
    protected final ApplicationEventPublisher applicationEventPublisher;

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
            log.trace("Added com.recom.dto.map entity package to transaction named {}!", sessionIdentifier);

            // EDGE CASE: If transaction is already committed, process it immediately
            // Try to process already committed transaction, in case that transaction-commit-com.recom.dto.message took over a data package!
            if (existingTransaction.isCommitted()) {
                log.debug("Try to process transaction {}, in case that transaction-commit-com.recom.dto.message took over a data package!", sessionIdentifier);
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

    protected abstract boolean processTransaction(@NonNull final String sessionIdentifier);

}
