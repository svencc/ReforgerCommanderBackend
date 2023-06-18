package com.recom.service.map.scanner;

import com.recom.dto.map.scanner.TransactionalEntityPackageDto;
import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.event.event.async.map.AddMapPackageAsyncEvent;
import com.recom.event.event.async.map.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.OpenMapTransactionAsyncEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapEntityTransactionService {

    @NonNull
    private final ApplicationEventPublisher applicationEventPublisher;

    public void openTransaction(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        applicationEventPublisher.publishEvent(new OpenMapTransactionAsyncEvent(transactionIdentifierDto));
    }
    public void addMapEntitiesPackage(@NonNull final TransactionalEntityPackageDto transactionalEntityPackageDto) {
        applicationEventPublisher.publishEvent(new AddMapPackageAsyncEvent(transactionalEntityPackageDto));
    }
    public void commitTransaction(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        applicationEventPublisher.publishEvent(new CommitMapTransactionAsyncEvent(transactionIdentifierDto));
    }

}
