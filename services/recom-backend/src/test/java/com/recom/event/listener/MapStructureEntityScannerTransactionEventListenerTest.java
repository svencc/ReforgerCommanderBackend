package com.recom.event.listener;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.structure.MapStructureDto;
import com.recom.dto.map.scanner.structure.TransactionalMapStructurePackageDto;
import com.recom.entity.map.GameMap;
import com.recom.event.event.async.map.addmappackage.AddMapPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTransactionAsyncEvent;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import com.recom.model.map.MapTransaction;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.map.MapTransactionValidatorService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MapStructureEntityScannerTransactionEventListenerTest {

    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @Mock
    private MapTransactionValidatorService<MapStructureDto, TransactionalMapStructurePackageDto> mapTransactionValidator;
    @Mock
    private GameMapPersistenceLayer gameMapPersistenceLayer;
    @InjectMocks
    private MapStructureEntityScannerTransactionEventListener eventListenerUnderTest;

    @Captor
    private ArgumentCaptor<CacheResetSyncEvent> cacheResetEventCaptor;


    @Test
    public void testHandleOpenTransaction() {
        // Arrange
        final TransactionIdentifierDto transactionIdentifierDto = new TransactionIdentifierDto();
        transactionIdentifierDto.setSessionIdentifier("session1");
        final OpenMapTransactionAsyncEvent event = new OpenMapTransactionAsyncEvent(transactionIdentifierDto);

        final Map<String, MapTransaction<MapStructureDto, TransactionalMapStructurePackageDto>> transactions = eventListenerUnderTest.getTransactions();

        // Act
        eventListenerUnderTest.handleOpenTransactionEvent(event);

        // Assert
        assertTrue(transactions.containsKey("session1"));
        final MapTransaction<MapStructureDto, TransactionalMapStructurePackageDto> transaction = transactions.get("session1");
        assertEquals(transactionIdentifierDto, transaction.getOpenTransactionIdentifier());
    }

    @Test
    public void testHandleAddMapPackage_whenTransactionExists_shouldIgnoreEventAndDoNothing() {
        // Arrange
        final TransactionalMapStructurePackageDto packageDto = new TransactionalMapStructurePackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);

        // Act
        eventListenerUnderTest.handleAddMapPackageEvent(event);

        // Assert
        assertFalse(eventListenerUnderTest.getTransactions().containsKey(session1));
        verify(mapStructurePersistenceLayer, never()).deleteMapEntities(any());
        verify(mapStructurePersistenceLayer, never()).saveAll(anyList());
        verify(applicationEventPublisher, never()).publishEvent(any(CacheResetSyncEvent.class));
    }

    @Test
    public void testHandleAddMapPackage_whenTransactionIsOpenedJustBefore_shouldAddPackageAndDoNotPersist() {
        // Arrange
        final TransactionalMapStructurePackageDto packageDto = new TransactionalMapStructurePackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);

        // Act
        // open transaction and send event/package
        final OpenMapTransactionAsyncEvent openSessionEvent = new OpenMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListenerUnderTest.handleOpenTransactionEvent(openSessionEvent);
        eventListenerUnderTest.handleAddMapPackageEvent(event);

        // Assert
        assertTrue(eventListenerUnderTest.getTransactions().containsKey(session1));
        assertEquals(1, eventListenerUnderTest.getTransactions().get(session1).getPackages().size());
        assertTrue(eventListenerUnderTest.getTransactions().get(session1).getPackages().contains(packageDto));
        verify(mapStructurePersistenceLayer, never()).deleteMapEntities(any());
        verify(mapStructurePersistenceLayer, never()).saveAll(anyList());
        verify(applicationEventPublisher, never()).publishEvent(any(CacheResetSyncEvent.class));
    }

    @Test
    public void testHandleCommitTransaction_whenTransactionIsValid_shouldProcessTransaction() {
        // Arrange
        final TransactionalMapStructurePackageDto packageDto = new TransactionalMapStructurePackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);
        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(true);
        when(gameMapPersistenceLayer.findByName(eq(session1))).thenReturn(Optional.of(GameMap.builder().name(session1).build()));

        // Act
        // open transaction and send event/package
        final OpenMapTransactionAsyncEvent openTransactionEvent = new OpenMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListenerUnderTest.handleOpenTransactionEvent(openTransactionEvent);

        eventListenerUnderTest.handleAddMapPackageEvent(event);

        final CommitMapTransactionAsyncEvent commitEvent = new CommitMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListenerUnderTest.handleCommitTransactionEvent(commitEvent);

        // Assert
        verify(mapTransactionValidator, times(1)).isValidTransaction(eq(eventListenerUnderTest.getTransactions().get(session1)));
        verify(transactionTemplate, times(1)).execute(any());
    }


    @Test
    public void testHandleCommitTransaction_whenTransactionIsInvalid_shouldNotProcessTransaction() {
        // Arrange
        final TransactionalMapStructurePackageDto packageDto = new TransactionalMapStructurePackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);
        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(false);

        // Act
        // open transaction and send event/package
        final OpenMapTransactionAsyncEvent openTransactionEvent = new OpenMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListenerUnderTest.handleOpenTransactionEvent(openTransactionEvent);

        eventListenerUnderTest.handleAddMapPackageEvent(event);

        final CommitMapTransactionAsyncEvent commitEvent = new CommitMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListenerUnderTest.handleCommitTransactionEvent(commitEvent);

        // Assert
        verify(mapTransactionValidator, times(1)).isValidTransaction(eq(eventListenerUnderTest.getTransactions().get(session1)));
        verify(transactionTemplate, never()).execute(any());
    }

}
