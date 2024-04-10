package com.recom.event.listener;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.structure.MapStructureDto;
import com.recom.dto.map.scanner.structure.TransactionalMapStructurePackageDto;
import com.recom.entity.map.ChunkStatus;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerStructureChunk;
import com.recom.event.event.async.map.addmappackage.AddMapPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTransactionAsyncEvent;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import com.recom.event.listener.util.ChunkCoordinate;
import com.recom.model.map.MapTransaction;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.chunk.structure.MapStructureChunkPersistenceLayer;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.map.MapTransactionValidatorService;
import com.recom.service.messagebus.chunkscanrequest.MapStructureChunkScanRequestNotificationService;
import jakarta.persistence.EntityManager;
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
public class MapStructureScannerTransactionEventListenerTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private MapStructureChunkPersistenceLayer mapStructureChunkPersistenceLayer;
    @Mock
    private MapStructureChunkScanRequestNotificationService mapStructureChunkScanRequestNotificationService;
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
    private MapStructureScannerTransactionEventListener eventListenerUnderTest;

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
        final String session1Identifier = "session1#####0,0";
        final String session1MapName = "session1";
        final GameMap gameMap = GameMap.builder().name(session1MapName).build();
        final SquareKilometerStructureChunk chunk = SquareKilometerStructureChunk.builder()
                .gameMap(gameMap)
                .squareCoordinateX(0L)
                .squareCoordinateY(0L)
                .status(ChunkStatus.OPEN)
                .build();

        packageDto.setSessionIdentifier(session1Identifier);
        final AddMapPackageAsyncEvent event = new AddMapPackageAsyncEvent(packageDto);
        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(true);
        when(gameMapPersistenceLayer.findByName(eq(session1MapName))).thenReturn(Optional.of(gameMap));

        final Optional<SquareKilometerStructureChunk> maybeChunk = Optional.of(chunk);
        when(mapStructureChunkPersistenceLayer.findByGameMapAndCoordinate(eq(gameMap), any(ChunkCoordinate.class))).thenReturn(maybeChunk);
        // Act
        // open transaction and send event/package
        final OpenMapTransactionAsyncEvent openTransactionEvent = new OpenMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1Identifier).build());
        eventListenerUnderTest.handleOpenTransactionEvent(openTransactionEvent);

        eventListenerUnderTest.handleAddMapPackageEvent(event);

        final CommitMapTransactionAsyncEvent commitEvent = new CommitMapTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1Identifier).build());
        eventListenerUnderTest.handleCommitTransactionEvent(commitEvent);

        // Assert
        verify(mapTransactionValidator, times(1)).isValidTransaction(eq(eventListenerUnderTest.getTransactions().get(session1Identifier)));
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
