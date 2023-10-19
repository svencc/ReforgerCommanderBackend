package com.recom.service.messagebus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.configuration.AsyncConfiguration;
import com.recom.model.message.MessageContainer;
import com.recom.model.message.MessageType;
import com.recom.model.message.OneMessage;
import com.recom.observer.Subject;
import com.recom.persistence.message.MessagePersistenceLayer;
import com.recom.property.RECOMAsyncProperties;
import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.AsyncTaskExecutor;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageLongPollObserverTest {

    @Mock
    private AsyncTaskExecutor asyncTaskExecutor;
    @Mock
    private MessagePersistenceLayer messagePersistenceLayer;
    private MessageBusService messageBusService;
    private AsyncConfiguration asyncConfiguration;
    private MessageLongPollObserver observerUnderTest;

    @BeforeEach
    public void setUp() {
        // Prepare MessageLongPollObserver (instance under test)
        asyncTaskExecutor = mock(AsyncTaskExecutor.class);
        messagePersistenceLayer = mock(MessagePersistenceLayer.class);
        observerUnderTest = MessageLongPollObserver.builder()
                .timeout(10000L) // Set a reasonable timeout value
                .asyncTaskExecutor(asyncTaskExecutor)
                .messagePersistenceLayer(messagePersistenceLayer)
                .build();

        messageBusService = new MessageBusService(messagePersistenceLayer);

        // Prepare ObjectMapper / StaticObjectMapperProvider
        final ObjectMapper objectMapper = new ObjectMapper();
        final StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(objectMapper);
        staticObjectMapperProvider.postConstruct();

        // Prepare AsyncConfiguration
        final RECOMAsyncProperties properties = RECOMAsyncProperties.builder()
                .corePoolSize(1)
                .maxPoolSize(1)
                .build();
        asyncConfiguration = new AsyncConfiguration(properties);
    }

    @Test
    public void test() throws InterruptedException {
        // Arrange
        observerUnderTest.scheduleTestResponse("TestMap", Duration.ofMillis(5), new Subject<>(), asyncConfiguration);
        observerUnderTest.observe(messageBusService.getSubject());

        final OneMessage testMessage = OneMessage.builder()
                .messageType(MessageType.TEST)
                .payload("test-payload")
                .build();

        // Act
        messageBusService.sendMessage(
                "TestMap",
                MessageContainer.builder()
                        .mapName("test-map")
                        .messages(List.of(testMessage))
                        .build()
        );

        // Assert
        // Sleep for a duration longer than the scheduled task to complete
        Thread.sleep(200);
        verify(messagePersistenceLayer, times(1)).saveAll(anyList());
    }

}