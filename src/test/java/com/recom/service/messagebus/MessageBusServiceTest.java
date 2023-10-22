package com.recom.service.messagebus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.model.message.MessageContainer;
import com.recom.model.message.MessageType;
import com.recom.model.message.OneMessage;
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

        // Prepare ObjectMapper / StaticObjectMapperProvider
        final ObjectMapper objectMapper = new ObjectMapper();
        final StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(objectMapper);
        staticObjectMapperProvider.postConstruct();

        try (final MessageLongPollObserver observer = new MessageLongPollObserver(10000L)) {
            observer.observe(serviceUnderTest.getSubject());

            // Act
            serviceUnderTest.sendMessage(MessageContainer.builder()
                    .mapName("mapName")
                    .messages(List.of(OneMessage.builder()
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
        // Act
        final MessageBusResponseDto responseToTest = serviceUnderTest.listMessagesSince("mapName", 0L);
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