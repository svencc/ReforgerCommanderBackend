package com.recom.event.listener;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.topography.MapTopographyDto;
import com.recom.dto.map.scanner.topography.TransactionalMapTopographyPackageDto;
import com.recom.entity.map.ChunkStatus;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerTopographyChunk;
import com.recom.event.event.async.map.addmappackage.AddMapTopographyPackageAsyncEvent;
import com.recom.event.event.async.map.commit.CommitMapTopographyTransactionAsyncEvent;
import com.recom.event.event.async.map.open.OpenMapTopographyTransactionAsyncEvent;
import com.recom.event.event.sync.cache.CacheResetSyncEvent;
import com.recom.event.listener.util.ChunkCoordinate;
import com.recom.model.map.MapTransaction;
import com.recom.model.map.TopographyData;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.chunk.topography.MapTopographyChunkPersistenceLayer;
import com.recom.service.SerializationService;
import com.recom.service.map.MapTransactionValidatorService;
import com.recom.service.messagebus.chunkscanrequest.MapTopographyChunkScanRequestNotificationService;
import com.recom.testhelper.SerializeObjectHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MapTopographyScannerTransactionEventListenerTest {

    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private MapTopographyChunkPersistenceLayer mapEntityPersistenceLayer;
    @Mock
    private MapTransactionValidatorService<MapTopographyDto, TransactionalMapTopographyPackageDto> mapTransactionValidator;
    @Mock
    private GameMapPersistenceLayer gameMapPersistenceLayer;
    @Mock
    private SerializationService serializationService;
    @Mock
    private MapTopographyChunkScanRequestNotificationService mapTopographyChunkScanRequestNotificationService;
    @InjectMocks
    private MapTopographyScannerTransactionEventListener eventListenerUnderTest;

    @Captor
    private ArgumentCaptor<CacheResetSyncEvent> cacheResetEventCaptor;


    @Test
    public void testHandleOpenTransaction() {
        // Arrange
        final TransactionIdentifierDto transactionIdentifierDto = new TransactionIdentifierDto();
        transactionIdentifierDto.setSessionIdentifier("session1");
        final OpenMapTopographyTransactionAsyncEvent event = new OpenMapTopographyTransactionAsyncEvent(transactionIdentifierDto);

        final Map<String, MapTransaction<MapTopographyDto, TransactionalMapTopographyPackageDto>> transactions = eventListenerUnderTest.getTransactions();

        // Act
        eventListenerUnderTest.handleOpenTransactionEvent(event);

        // Assert
        assertTrue(transactions.containsKey("session1"));
        final MapTransaction<MapTopographyDto, TransactionalMapTopographyPackageDto> transaction = transactions.get("session1");
        assertEquals(transactionIdentifierDto, transaction.getOpenTransactionIdentifier());
    }

    @Test
    public void testHandleAddMapPackage_whenTransactionExists_shouldIgnoreEventAndDoNothing() {
        // Arrange
        final TransactionalMapTopographyPackageDto packageDto = new TransactionalMapTopographyPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapTopographyPackageAsyncEvent event = new AddMapTopographyPackageAsyncEvent(packageDto);

        // Act
        eventListenerUnderTest.handleAddMapPackageEvent(event);

        // Assert
        assertFalse(eventListenerUnderTest.getTransactions().containsKey(session1));
        verify(mapEntityPersistenceLayer, never()).deleteMapEntities(any());
        verify(mapEntityPersistenceLayer, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any(CacheResetSyncEvent.class));
    }

    @Test
    public void testHandleAddMapPackage_whenTransactionIsOpenedJustBefore_shouldAddPackageAndDoNotPersist() {
        // Arrange
        final TransactionalMapTopographyPackageDto packageDto = new TransactionalMapTopographyPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapTopographyPackageAsyncEvent event = new AddMapTopographyPackageAsyncEvent(packageDto);

        // Act
        // open transaction and send event/package
        final OpenMapTopographyTransactionAsyncEvent openSessionEvent = new OpenMapTopographyTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListenerUnderTest.handleOpenTransactionEvent(openSessionEvent);
        eventListenerUnderTest.handleAddMapPackageEvent(event);

        // Assert
        assertTrue(eventListenerUnderTest.getTransactions().containsKey(session1));
        assertEquals(1, eventListenerUnderTest.getTransactions().get(session1).getPackages().size());
        assertTrue(eventListenerUnderTest.getTransactions().get(session1).getPackages().contains(packageDto));
        verify(mapEntityPersistenceLayer, never()).deleteMapEntities(any());
        verify(mapEntityPersistenceLayer, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any(CacheResetSyncEvent.class));
    }

    @Test
    public void testHandleCommitTransaction_whenTransactionIsValid_shouldProcessTransaction() throws IOException {

        // Arrange
        final List<MapTopographyDto> entities = List.of(MapTopographyDto.builder()
                .coordinates(List.of(BigDecimal.valueOf(0.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(0.0)))
                .build());
        final TransactionalMapTopographyPackageDto packageDto = TransactionalMapTopographyPackageDto.builder()
                .entities(entities)
                .build();

        final String session1Identifier = "session1#####0,0";
        final String session1MapName = "session1";
        final GameMap gameMap = GameMap.builder().name(session1MapName).build();
        final SquareKilometerTopographyChunk chunk = SquareKilometerTopographyChunk.builder()
                .gameMap(gameMap)
                .squareCoordinateX(0L)
                .squareCoordinateY(0L)
                .status(ChunkStatus.OPEN)
                .build();

        packageDto.setSessionIdentifier(session1Identifier);
        final AddMapTopographyPackageAsyncEvent event = new AddMapTopographyPackageAsyncEvent(packageDto);
        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(true);
        when(gameMapPersistenceLayer.findByName(eq(session1MapName))).thenReturn(Optional.of(gameMap));

        final Optional<SquareKilometerTopographyChunk> maybeChunk = Optional.of(chunk);
        when(mapEntityPersistenceLayer.findByGameMapAndCoordinate(eq(gameMap), any(ChunkCoordinate.class))).thenReturn(maybeChunk);

        final TopographyData topographyData = TopographyData.builder()
                .stepSize(1)
                .scanIterationsX(1)
                .scanIterationsZ(1)
                .oceanBaseHeight(0.0f)
                .surfaceData(new float[][]{{2.0f}})
                .build();
        when(serializationService.serializeObject(any())).thenReturn(SerializeObjectHelper.serializeObjectHelper(topographyData));


        // Act
        // open transaction and send event/package
        final OpenMapTopographyTransactionAsyncEvent openTransactionEvent = new OpenMapTopographyTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1Identifier).build());
        eventListenerUnderTest.handleOpenTransactionEvent(openTransactionEvent);

        eventListenerUnderTest.handleAddMapPackageEvent(event);

        final CommitMapTopographyTransactionAsyncEvent commitEvent = new CommitMapTopographyTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1Identifier).build());
        eventListenerUnderTest.handleCommitTransactionEvent(commitEvent);

        // Assert
        verify(mapTransactionValidator, times(1)).isValidTransaction(eq(eventListenerUnderTest.getTransactions().get(session1Identifier)));
        verify(transactionTemplate, times(1)).execute(any());
    }


    @Test
    public void testHandleCommitTransaction_whenTransactionIsInvalid_shouldNotProcessTransaction() {

        // Arrange
        final TransactionalMapTopographyPackageDto packageDto = new TransactionalMapTopographyPackageDto();
        final String session1 = "session1";
        packageDto.setSessionIdentifier(session1);
        final AddMapTopographyPackageAsyncEvent event = new AddMapTopographyPackageAsyncEvent(packageDto);
        when(mapTransactionValidator.isValidTransaction(any(MapTransaction.class))).thenReturn(false);

        // Act
        // open transaction and send event/package
        final OpenMapTopographyTransactionAsyncEvent openTransactionEvent = new OpenMapTopographyTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListenerUnderTest.handleOpenTransactionEvent(openTransactionEvent);

        eventListenerUnderTest.handleAddMapPackageEvent(event);

        final CommitMapTopographyTransactionAsyncEvent commitEvent = new CommitMapTopographyTransactionAsyncEvent(TransactionIdentifierDto.builder().sessionIdentifier(session1).build());
        eventListenerUnderTest.handleCommitTransactionEvent(commitEvent);

        // Assert
        verify(mapTransactionValidator, times(1)).isValidTransaction(eq(eventListenerUnderTest.getTransactions().get(session1)));
        verify(transactionTemplate, never()).execute(any());
    }

}
