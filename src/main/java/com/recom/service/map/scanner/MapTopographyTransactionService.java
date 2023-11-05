package com.recom.service.map.scanner;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import com.recom.event.event.async.map.addmappackage.AddMapTopographyPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTopographyTransactionAsyncEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


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