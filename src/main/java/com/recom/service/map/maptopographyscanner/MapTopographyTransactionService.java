package com.recom.service.map.maptopographyscanner;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import com.recom.event.event.async.map.commit.CommitMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.addmappackage.AddMapTopographyPackageAsyncEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/*
@Service
public class MapTopographyTransactionService extends TransactionalMapBaseService<TransactionalMapTopographyEntityPackageDto> {

    public MapTopographyTransactionService(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public AddTransactionalMapPackageAsyncEvent<TransactionalMapTopographyEntityPackageDto> createMapPackageAsyncEvent(@NonNull TransactionalMapTopographyEntityPackageDto transactionalEntityPackageDto) {
        return new AddMapTopographyPackageAsyncEvent(transactionalEntityPackageDto);
    }

}
 */

@Service
@RequiredArgsConstructor
public class MapTopographyTransactionService {

    @NonNull
    private final ApplicationEventPublisher applicationEventPublisher;

    public void openTransaction(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        applicationEventPublisher.publishEvent(new OpenMapTopographyTransactionAsyncEvent(transactionIdentifierDto));
    }
    public void addMapEntitiesPackage(@NonNull final TransactionalMapTopographyEntityPackageDto transactionalMapEntityPackageDto) {
        applicationEventPublisher.publishEvent(new AddMapTopographyPackageAsyncEvent(transactionalMapEntityPackageDto));
    }
    public void commitTransaction(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        applicationEventPublisher.publishEvent(new CommitMapTopographyTransactionAsyncEvent(transactionIdentifierDto));
    }

}