package com.recom.event;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.map.MapEntityDto;
import com.recom.dto.map.scanner.map.TransactionalMapEntityPackageDto;
import com.recom.event.event.async.map.addmappackage.AddMapPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTransactionAsyncEvent;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import com.recom.event.listener.MapEntityScannerTransactionEventListener;
import com.recom.model.map.MapTransaction;
import com.recom.persistence.mapEntity.MapEntityPersistenceLayer;
import com.recom.service.map.MapTransactionValidatorService;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MapEntityScannerTransactionEventListenerTest {

    @Mock
    private TransactionTemplate transactionTemplate;
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

        final @NonNull Map<String, MapTransaction<MapEntityDto, TransactionalMapEntityPackageDto>> transactions = eventListener.getTransactions();

        // Act
        eventListener.handleOpenTransactionEvent(event);

        // Assert
        assert transactions.containsKey("session1");
        final MapTransaction transaction = transactions.get("session1");
        assert transaction.getOpenTransactionIdentifier().equals(transactionIdentifierDto);
    }

    @Test
    public void testHandleAddMapPackage_whenTransactionExists_shouldIgnoreEventAndDoNothing() {
        // Arrange
        final TransactionalMapEntityPackageDto packageDto = new TransactionalMapEntityPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);

        // Act
        eventListener.handleAddMapPackageEvent(event);

        // Assert
        assertFalse(eventListener.getTransactions().containsKey(session1));
        verify(mapEntityPersistenceLayer, never()).deleteMapEntities(anyString());
        verify(mapEntityPersistenceLayer, never()).saveAll(anyList());
        verify(applicationEventPublisher, never()).publishEvent(any(CacheResetSyncEvent.class));
    }

    @Test
    public void testHandleAddMapPackage_whenTransactionIsOpenedJustBefore_shouldAddPackageAndDoNotPersist() {
        // Arrange
        final TransactionalMapEntityPackageDto packageDto = new TransactionalMapEntityPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);

        // Act
        // open transaction and send event/package
        final OpenMapTransactionAsyncEvent openSessionEvent = new OpenMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListener.handleOpenTransactionEvent(openSessionEvent);
        eventListener.handleAddMapPackageEvent(event);

        // Assert
        assertTrue(eventListener.getTransactions().containsKey(session1));
        assertEquals(1, eventListener.getTransactions().get(session1).getPackages().size());
        assertTrue(eventListener.getTransactions().get(session1).getPackages().contains(packageDto));
        verify(mapEntityPersistenceLayer, never()).deleteMapEntities(anyString());
        verify(mapEntityPersistenceLayer, never()).saveAll(anyList());
        verify(applicationEventPublisher, never()).publishEvent(any(CacheResetSyncEvent.class));
    }

    @Test
    public void testHandleCommitTransaction_whenTransactionIsValid_shouldProcessTransaction() {
        // Arrange
        final TransactionalMapEntityPackageDto packageDto = new TransactionalMapEntityPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);
        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(true);

        // Act
        // open transaction and send event/package
        final OpenMapTransactionAsyncEvent openTransactionEvent = new OpenMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListener.handleOpenTransactionEvent(openTransactionEvent);

        eventListener.handleAddMapPackageEvent(event);

        final CommitMapTransactionAsyncEvent commitEvent = new CommitMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListener.handleCommitTransactionEvent(commitEvent);

        // Assert
        verify(mapTransactionValidator, times(1)).isValidTransaction(eq(eventListener.getTransactions().get(session1)));
        verify(transactionTemplate, times(1)).execute(any());
    }


    @Test
    public void testHandleCommitTransaction_whenTransactionIsInvalid_shouldNotProcessTransaction() {
        // Arrange
        final TransactionalMapEntityPackageDto packageDto = new TransactionalMapEntityPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);
        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(false);

        // Act
        // open transaction and send event/package
        final OpenMapTransactionAsyncEvent openTransactionEvent = new OpenMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListener.handleOpenTransactionEvent(openTransactionEvent);

        eventListener.handleAddMapPackageEvent(event);

        final CommitMapTransactionAsyncEvent commitEvent = new CommitMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListener.handleCommitTransactionEvent(commitEvent);

        // Assert
        verify(mapTransactionValidator, times(1)).isValidTransaction(eq(eventListener.getTransactions().get(session1)));
        verify(transactionTemplate, never()).execute(any());
    }

}
