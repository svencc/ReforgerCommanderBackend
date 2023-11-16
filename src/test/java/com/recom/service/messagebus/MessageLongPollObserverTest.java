package com.recom.service.messagebus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.dto.message.MessageBusResponseDto;
import com.recom.model.message.MessageContainer;
import com.recom.model.message.MessageType;
import com.recom.model.message.OneMessage;
import com.recom.observer.Subjective;
import com.recom.persistence.message.MessagePersistenceLayer;
import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageLongPollObserverTest {

    @Mock
    private MessagePersistenceLayer messagePersistenceLayer;
    private MessageBusService messageBusService;
    private MessageLongPollObserver observerUnderTest;


    @BeforeEach
    public void setUp() {
        // Prepare MessageLongPollObserver (instance under test)
        messagePersistenceLayer = mock(MessagePersistenceLayer.class);
        observerUnderTest = new MessageLongPollObserver(10000L);
        messageBusService = new MessageBusService(messagePersistenceLayer);

        // Prepare ObjectMapper / StaticObjectMapperProvider
        final ObjectMapper objectMapper = new ObjectMapper();
        final StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(objectMapper);
        staticObjectMapperProvider.postConstruct();
    }

    @Test
    public void sendMessage_withOneMessage_messageIsPersisted() {
        // Arrange
        final Subjective<MessageBusResponseDto> messageBusSubjectSpy = spy(messageBusService.getSubject());
        observerUnderTest.observe(messageBusSubjectSpy);

        final OneMessage testMessage = OneMessage.builder()
                .messageType(MessageType.TEST)
                .payload("test-payload")
                .build();

        // Act
        messageBusService.sendMessage(
                MessageContainer.builder()
                        .gameMap("test-map")
                        .messages(List.of(testMessage))
                        .build()
        );

        // run autocloseable implementation;
        observerUnderTest.close();

        // Assert
        verify(messagePersistenceLayer, times(1)).saveAll(anyList());
        verify(messageBusSubjectSpy, times(1)).beObservedBy(observerUnderTest);
        verify(messageBusSubjectSpy, times(1)).observationStoppedThrough(any()); // executed by autocloseable implementation
    }

}