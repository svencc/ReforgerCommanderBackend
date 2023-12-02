package com.recom.service.messagebus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.dto.message.MessageType;
import com.recom.entity.map.GameMap;
import com.recom.model.message.MessageContainer;
import com.recom.model.message.SingleMessage;
import com.recom.observer.Subjective;
import com.recom.persistence.message.MessagePersistenceLayer;
import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageBusServiceTest {

    @Mock
    private MessagePersistenceLayer messagePersistenceLayer;
    @InjectMocks
    private MessageBusService serviceUnderTest;


    @Test
    void sendMessage() {
        // Arrange
        final GameMap gameMap = GameMap.builder().name("mapName").build();

        // Prepare ObjectMapper / StaticObjectMapperProvider
        final ObjectMapper objectMapper = new ObjectMapper();
        final StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(objectMapper);
        staticObjectMapperProvider.postConstruct();

        try (final MessageLongPollObserver observer = new MessageLongPollObserver(10000L)) {
            observer.observe(serviceUnderTest.getSubject());

            // Act
            serviceUnderTest.sendMessage(MessageContainer.builder()
                    .gameMap(gameMap)
                    .messages(List.of(SingleMessage.builder()
                            .messageType(MessageType.TEST)
                            .payload("test-payload")
                            .build()
                    ))
                    .build());

            // Assert
            verify(messagePersistenceLayer, times(1)).saveAll(any());
        }
    }

    @Test
    void listMessagesSince() {
        // Arrange
        final GameMap gameMap = GameMap.builder().name("mapName").build();

        // Act
        final MessageBusResponseDto responseToTest = serviceUnderTest.listMessagesSince(gameMap, 0L);

        // Assert
        assertNotNull(responseToTest);
        assertEquals("mapName", responseToTest.getMapName());
        assertNotNull(responseToTest.getMessages());
        assertTrue(responseToTest.getMessages().isEmpty());
    }

    @Test
    void getSubject() {
        // Arrange
        // Act
        final Subjective<MessageBusResponseDto> resultToTest = serviceUnderTest.getSubject();
        // Assert
        assertNotNull(resultToTest);
    }

}