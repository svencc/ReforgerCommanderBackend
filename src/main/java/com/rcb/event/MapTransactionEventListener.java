package com.rcb.event;

import com.rcb.event.event.async.RefComAsyncEvent;
import com.rcb.event.event.async.map.AddMapPackageAsyncEvent;
import com.rcb.event.event.async.map.CommitMapTransactionAsyncEvent;
import com.rcb.event.event.async.map.OpenMapTransactionAsyncEvent;
import com.rcb.model.MapTransaction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MapTransactionEventListener {

    private Map<String, MapTransaction> transactions = new HashMap<>();

    @Async("AsyncMapExecutor")
    @EventListener(classes = OpenMapTransactionAsyncEvent.class)
    public void handleOpenTransaction(@NonNull final OpenMapTransactionAsyncEvent event) {
        logEvent(event);
        final String sessionIdentifier = event.getTransactionIdentifierDto().getSessionIdentifier();
        if (transactions.containsKey(sessionIdentifier)) {
            MapTransaction transaction = transactions.get(sessionIdentifier);
            resetSession(transaction);
            transaction.setOpenTransactionIdentifier(event.getTransactionIdentifierDto());
        } else {
            transactions.get(sessionIdentifier).setOpenTransactionIdentifier(event.getTransactionIdentifierDto());
        }
    }

    private static void resetSession(MapTransaction transaction) {
        transaction.setOpenTransactionIdentifier(null);
        transaction.setCommitTransactionIdentifier(null);
        transaction.getPackages().clear();
    }

    private void logEvent(@NonNull RefComAsyncEvent event) {
        log.warn("****** handle {} {} ", event, event.getCreationDate());
    }

    @Async("AsyncMapExecutor")
    @EventListener(classes = AddMapPackageAsyncEvent.class)
    public void handleAddMapPackage(@NonNull final AddMapPackageAsyncEvent event) {
        logEvent(event);
        final String sessionIdentifier = event.getEntityPackageDto().getSessionIdentifier();
        if (transactions.containsKey(sessionIdentifier)) {
            transactions.get(sessionIdentifier).getPackages().add(event.getEntityPackageDto());
        } else {
            // ignore
        }
    }

    @Async("AsyncMapExecutor")
    @EventListener(classes = CommitMapTransactionAsyncEvent.class)
    public void handleCommitTransaction(@NonNull final CommitMapTransactionAsyncEvent event) {
        logEvent(event);
        if (transactions.containsKey(event.getTransactionIdentifierDto().getSessionIdentifier())) {

        } else {

        }
    }

}
