package com.recom.service.map;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.event.event.async.map.addmappackage.AddPackageAsyncEventBase;
import com.recom.event.event.async.map.commit.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTransactionAsyncEvent;
import com.recom.event.listener.generic.generic.TransactionalMapEntityPackable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public abstract class TransactionalMapBaseService<T extends TransactionalMapEntityPackable> {

    @NonNull
    protected final ApplicationEventPublisher applicationEventPublisher;

    public void openTransaction(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        applicationEventPublisher.publishEvent(new OpenMapTransactionAsyncEvent(transactionIdentifierDto));
    }

    public void addMapEntitiesPackage(@NonNull final T transactionalEntityPackageDto) {
        applicationEventPublisher.publishEvent(createMapPackageAsyncEvent(transactionalEntityPackageDto));
    }

    public abstract AddPackageAsyncEventBase<T> createMapPackageAsyncEvent(@NonNull final T transactionalEntityPackageDto);

    public void commitTransaction(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        applicationEventPublisher.publishEvent(new CommitMapTransactionAsyncEvent(transactionIdentifierDto));
    }

}
