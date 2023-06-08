package com.rcb.service.map.scanner;

import com.rcb.dto.map.scanner.TransactionalEntityPackageDto;
import com.rcb.dto.map.scanner.TransactionIdentifierDto;
import com.rcb.event.event.async.map.AddMapPackageAsyncEvent;
import com.rcb.event.event.async.map.CommitMapTransactionAsyncEvent;
import com.rcb.event.event.async.map.OpenMapTransactionAsyncEvent;
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
