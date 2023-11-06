package com.recom.event.listener;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.topography.MapTopographyEntityDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyEntityPackageDto;
import com.recom.event.event.async.map.addmappackage.AddMapTopographyPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTopographyTransactionAsyncEvent;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import com.recom.model.map.MapTransaction;
import com.recom.persistence.mapTopographyEntity.MapTopographyEntityPersistenceLayer;
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
public class MapTopographyEntityScannerTransactionEventListenerTest {

    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private MapTopographyEntityPersistenceLayer mapEntityPersistenceLayer;
    @Mock
    private MapTransactionValidatorService<MapTopographyEntityDto, TransactionalMapTopographyEntityPackageDto> mapTransactionValidator;
    @InjectMocks
    private MapTopographyEntityScannerTransactionEventListener eventListener;

    @Captor
    private ArgumentCaptor<CacheResetSyncEvent> cacheResetEventCaptor;


    @Test
    public void testHandleOpenTransaction() {
        // Arrange
        final TransactionIdentifierDto transactionIdentifierDto = new TransactionIdentifierDto();
        transactionIdentifierDto.setSessionIdentifier("session1");
        final OpenMapTopographyTransactionAsyncEvent event = new OpenMapTopographyTransactionAsyncEvent(transactionIdentifierDto);

        final @NonNull Map<String, MapTransaction<MapTopographyEntityDto, TransactionalMapTopographyEntityPackageDto>> transactions = eventListener.getTransactions();

        // Act
        eventListener.handleOpenTransactionEvent(event);

        // Assert
        assert transactions.containsKey("session1");
        final MapTransaction<MapTopographyEntityDto, TransactionalMapTopographyEntityPackageDto> transaction = transactions.get("session1");
        assert transaction.getOpenTransactionIdentifier().equals(transactionIdentifierDto);
    }

    @Test
    public void testHandleAddMapPackage_whenTransactionExists_shouldIgnoreEventAndDoNothing() {
        // Arrange
        final TransactionalMapTopographyEntityPackageDto packageDto = new TransactionalMapTopographyEntityPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapTopographyPackageAsyncEvent event = new AddMapTopographyPackageAsyncEvent(packageDto);

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
        final TransactionalMapTopographyEntityPackageDto packageDto = new TransactionalMapTopographyEntityPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapTopographyPackageAsyncEvent event = new AddMapTopographyPackageAsyncEvent(packageDto);

        // Act
        // open transaction and send event/package
        final OpenMapTopographyTransactionAsyncEvent openSessionEvent = new OpenMapTopographyTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
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
        final TransactionalMapTopographyEntityPackageDto packageDto = new TransactionalMapTopographyEntityPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapTopographyPackageAsyncEvent event = new AddMapTopographyPackageAsyncEvent(packageDto);
        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(true);

        // Act
        // open transaction and send event/package
        final OpenMapTopographyTransactionAsyncEvent openTransactionEvent = new OpenMapTopographyTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListener.handleOpenTransactionEvent(openTransactionEvent);

        eventListener.handleAddMapPackageEvent(event);

        final CommitMapTopographyTransactionAsyncEvent commitEvent = new CommitMapTopographyTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListener.handleCommitTransactionEvent(commitEvent);

        // Assert
        verify(mapTransactionValidator, times(1)).isValidTransaction(eq(eventListener.getTransactions().get(session1)));
        verify(transactionTemplate, times(1)).execute(any());
    }


    @Test
    public void testHandleCommitTransaction_whenTransactionIsInvalid_shouldNotProcessTransaction() {
        // Arrange
        final TransactionalMapTopographyEntityPackageDto packageDto = new TransactionalMapTopographyEntityPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapTopographyPackageAsyncEvent event = new AddMapTopographyPackageAsyncEvent(packageDto);
        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(false);

        // Act
        // open transaction and send event/package
        final OpenMapTopographyTransactionAsyncEvent openTransactionEvent = new OpenMapTopographyTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListener.handleOpenTransactionEvent(openTransactionEvent);

        eventListener.handleAddMapPackageEvent(event);

        final CommitMapTopographyTransactionAsyncEvent commitEvent = new CommitMapTopographyTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListener.handleCommitTransactionEvent(commitEvent);

        // Assert
        verify(mapTransactionValidator, times(1)).isValidTransaction(eq(eventListener.getTransactions().get(session1)));
        verify(transactionTemplate, never()).execute(any());
    }

}
