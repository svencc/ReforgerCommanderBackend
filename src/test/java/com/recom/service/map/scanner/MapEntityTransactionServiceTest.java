package com.recom.service.map.scanner;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.TransactionalEntityPackageDto;
import com.recom.event.event.async.map.AddMapPackageAsyncEvent;
import com.recom.event.event.async.map.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.OpenMapTransactionAsyncEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MapEntityTransactionServiceTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @InjectMocks
    private MapEntityTransactionService serviceUnderTest;


    @Test
    public void testOpenTransaction() {
        // Arrange
        final TransactionIdentifierDto transactionIdentifierDto = TransactionIdentifierDto.builder().sessionIdentifier("transactionId").build();

        // Act
        serviceUnderTest.openTransaction(transactionIdentifierDto);

        // Assert
        verify(applicationEventPublisher).publishEvent(any(OpenMapTransactionAsyncEvent.class));
    }

    @Test
    public void testAddMapEntitiesPackage() {
        // Arrange
        final TransactionalEntityPackageDto entityPackageDto = TransactionalEntityPackageDto.builder().sessionIdentifier("transactionId").build();

        // Act
        serviceUnderTest.addMapEntitiesPackage(entityPackageDto);

        // Assert
        verify(applicationEventPublisher).publishEvent(any(AddMapPackageAsyncEvent.class));
    }

    @Test
    public void testCommitTransaction() {
        // Arrange
        final TransactionIdentifierDto transactionIdentifierDto = TransactionIdentifierDto.builder().sessionIdentifier("transactionId").build();

        // Act
        serviceUnderTest.commitTransaction(transactionIdentifierDto);

        // Assert
        verify(applicationEventPublisher).publishEvent(any(CommitMapTransactionAsyncEvent.class));
    }

}