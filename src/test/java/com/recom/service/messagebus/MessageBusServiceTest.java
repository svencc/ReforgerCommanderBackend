package com.recom.service.messagebus;

import com.recom.model.message.MessageContainer;
import com.recom.observer.Subjective;
import com.recom.persistence.message.MessagePersistenceLayer;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessageBusServiceTest {

    @Mock
    @NonNull
    private MessagePersistenceLayer messagePersistenceLayer;
    @InjectMocks
    private MessageBusService serviceUnderTest;

    @Test
    void sendMessage() {
        // Arrange
        // Act
        serviceUnderTest.sendMessage("mapName", new MessageContainer());
        // Assert
    }

    @Test
    void listMessagesSince() {
        // Arrange
        // Act
        serviceUnderTest.listMessagesSince("mapName", 0L);
        // Assert
    }

    @Test
    void getSubject() {
        // Arrange
        // Act
        final Subjective<MessageContainer> resultToTest = serviceUnderTest.getSubject();
        // Assert
        assertNotNull(resultToTest);
    }
}