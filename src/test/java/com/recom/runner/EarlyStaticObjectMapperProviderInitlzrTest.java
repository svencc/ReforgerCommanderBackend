package com.recom.runner;

import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EarlyStaticObjectMapperProviderInitlzrTest {

    @Mock
    private StaticObjectMapperProvider staticObjectMapperProvider;
    @InjectMocks
    private EarlyStaticObjectMapperProviderInitlzr runnerUnderTest;

    @Test
    public void testRun() {
        // Arrange
        ApplicationArguments applicationArguments = mock(ApplicationArguments.class);

        // Act
        assertDoesNotThrow(() -> runnerUnderTest.run(applicationArguments));

        // Assert
        verify(staticObjectMapperProvider).postConstruct();
    }

}