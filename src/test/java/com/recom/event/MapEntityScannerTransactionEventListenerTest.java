package com.recom.event;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.TransactionalEntityPackageDto;
import com.recom.entity.MapEntity;
import com.recom.event.event.async.map.AddMapPackageAsyncEvent;
import com.recom.event.event.async.map.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.OpenMapTransactionAsyncEvent;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import com.recom.model.map.MapTransaction;
import com.recom.repository.mapEntity.MapEntityPersistenceLayer;
import com.recom.service.map.scanner.MapTransactionValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/*
@ExtendWith(MockitoExtension.class)
public class MapEntityScannerTransactionEventListenerTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private MapEntityPersistenceLayer mapEntityPersistenceLayer;
    @Mock
    private MapTransactionValidatorService mapTransactionValidator;
    @InjectMocks
    private MapEntityScannerTransactionEventListener eventListener;

    @Captor
    private ArgumentCaptor<CacheResetSyncEvent> cacheResetEventCaptor;


    @Test
    public void testHandleOpenTransaction() {
        // Arrange
        final TransactionIdentifierDto transactionIdentifierDto = new TransactionIdentifierDto();
        transactionIdentifierDto.setSessionIdentifier("session1");
        final OpenMapTransactionAsyncEvent event = new OpenMapTransactionAsyncEvent(transactionIdentifierDto);

        final Map<String, MapTransaction> transactions = eventListener.getTransactions();

        // Act
        eventListener.handleOpenTransaction(event);

        // Assert
        assert transactions.containsKey("session1");
        final MapTransaction transaction = transactions.get("session1");
        assert transaction.getOpenTransactionIdentifier().equals(transactionIdentifierDto);
    }

//    @Test
//    public void testHandleAddMapPackage_ExistingTransaction() {
//        // Arrange
//        final TransactionalEntityPackageDto packageDto = new TransactionalEntityPackageDto();
//        packageDto.setSessionIdentifier("session1");
//        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);
//
//        final Map<String, MapTransaction> transactions = eventListener.getTransactions();
//
//        // Act
//        eventListener.handleAddMapPackage(event);
//
//        // Assert
////        verify(mapEntityPersistenceLayer, times(1)).deleteMapEntities("session1");
//        verify(mapEntityPersistenceLayer, times(1)).saveAll(anyList());
//        verify(applicationEventPublisher, times(1)).publishEvent(any(CacheResetSyncEvent.class));
//    }

    @Test
    public void testHandleAddMapPackage_NewTransaction() {
        // Arrange
        final TransactionalEntityPackageDto packageDto = new TransactionalEntityPackageDto();
        packageDto.setSessionIdentifier("session1");
        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);

        // Act
        eventListener.handleAddMapPackage(event);

        // Assert
        verify(mapEntityPersistenceLayer, never()).deleteMapEntities(anyString());
        verify(mapEntityPersistenceLayer, never()).saveAll(anyList());
        verify(applicationEventPublisher, never()).publishEvent(any(CacheResetSyncEvent.class));
    }

//    @Test
//    public void testProcessTransaction_ValidTransaction() {
//        // Arrange
//        final TransactionalEntityPackageDto packageDto = new TransactionalEntityPackageDto();
//        final MapTransaction transaction = MapTransaction.builder().build();;
//        packageDto.setEntities(new ArrayList<>());
//        transaction.getPackages().add(packageDto);
//        final List<MapEntity> entities = new ArrayList<>();
//        when(mapEntityPersistenceLayer.saveAll(anyList())).thenReturn(entities);
//        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(true);
//
//        // Act
//        boolean isProcessed = eventListener.processTransaction("session1");
//
//        // Assert
//        assert isProcessed;
//        verify(mapEntityPersistenceLayer, times(1)).deleteMapEntities("session1");
//        verify(mapEntityPersistenceLayer, times(1)).saveAll(anyList());
//    }
//
//    @Test
//    public void testProcessTransaction_InvalidTransaction() {
//        // Arrange
//        final MapTransaction transaction = new MapTransaction();
//        TransactionalEntityPackageDto packageDto = new TransactionalEntityPackageDto();
//        packageDto.setEntities(new ArrayList<>());
//        transaction.getPackages().add(packageDto);
//        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(false);
//
//        // Act
//        boolean isProcessed = eventListener.processTransaction("session1");
//
//        // Assert
//        assert !isProcessed;
//        verify(mapEntityPersistenceLayer, never()).deleteMapEntities(anyString());
//        verify(mapEntityPersistenceLayer, never()).saveAll(anyList());
//    }
//
//    @Test
//    public void testHandleCommitTransaction_ExistingTransaction() {
//        // Arrange
//        CommitMapTransactionAsyncEvent event = new CommitMapTransactionAsyncEvent();
//        TransactionIdentifierDto transactionIdentifierDto = new TransactionIdentifierDto();
//        transactionIdentifierDto.setSessionIdentifier("session1");
//        event.setTransactionIdentifierDto(transactionIdentifierDto);
//
//        Map<String, MapTransaction> transactions = new HashMap<>();
//        MapTransaction transaction = new MapTransaction();
//        transactions.put("session1", transaction);
//        eventListener.setTransactions(transactions);
//
//        // Act
//        eventListener.handleCommitTransaction(event);
//
//        // Assert
//        assert transaction.getCommitTransactionIdentifier().equals(transactionIdentifierDto);
//        verify(mapEntityPersistenceLayer, times(1)).deleteMapEntities("session1");
//        verify(mapEntityPersistenceLayer, times(1)).saveAll(anyList());
//    }
//
//    @Test
//    public void testHandleCommitTransaction_NoTransactionFound() {
//        // Arrange
//        CommitMapTransactionAsyncEvent event = new CommitMapTransactionAsyncEvent();
//        TransactionIdentifierDto transactionIdentifierDto = new TransactionIdentifierDto();
//        transactionIdentifierDto.setSessionIdentifier("session1");
//        event.setTransactionIdentifierDto(transactionIdentifierDto);
//
//        Map<String, MapTransaction> transactions = new HashMap<>();
//        eventListener.setTransactions(transactions);
//
//        // Act
//        eventListener.handleCommitTransaction(event);
//
//        // Assert
//        assert !transactions.containsKey("session1");
//        verify(mapEntityPersistenceLayer, never()).deleteMapEntities(anyString());
//        verify(mapEntityPersistenceLayer, never()).saveAll(anyList());
//    }

}

 */