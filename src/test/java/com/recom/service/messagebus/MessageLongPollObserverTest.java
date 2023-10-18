package com.recom.service.messagebus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.configuration.AsyncConfiguration;
import com.recom.dto.map.Point2DDto;
import com.recom.model.message.MessageContainer;
import com.recom.model.message.MessageType;
import com.recom.model.message.OneMessage;
import com.recom.observer.Subject;
import com.recom.observer.Subjective;
import com.recom.persistence.message.MessagePersistenceLayer;
import com.recom.property.RECOMAsyncProperties;
import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageLongPollObserverTest {

    @Mock
    private AsyncTaskExecutor asyncTaskExecutor;
    @Mock
    private MessagePersistenceLayer messagePersistenceLayer;
    private MessageLongPollObserver observer;

    @BeforeEach
    public void setUp() {
        asyncTaskExecutor = mock(AsyncTaskExecutor.class);
        messagePersistenceLayer = mock(MessagePersistenceLayer.class);
        observer = MessageLongPollObserver.builder()
                .timeout(10000L) // Set a reasonable timeout value
                .asyncTaskExecutor(asyncTaskExecutor)
                .messagePersistenceLayer(messagePersistenceLayer)
                .build();
    }

    @Test
    public void testScheduleTestResponse() throws InterruptedException, IOException {
        // Arrange
        final ObjectMapper objectMapper = new ObjectMapper();
        final StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(objectMapper);
        staticObjectMapperProvider.postConstruct();

        final RECOMAsyncProperties properties = RECOMAsyncProperties.builder()
                .corePoolSize(1)
                .maxPoolSize(1)
                .build();

        // Act
        observer.scheduleTestResponse("TestMap", Duration.ofMillis(5), new Subject<>(), new AsyncConfiguration(properties));

        // Assert
        // Sleep for a duration longer than the scheduled task to complete
        Thread.sleep(200);
        verify(messagePersistenceLayer, times(1)).saveAll(anyList());
    }

}