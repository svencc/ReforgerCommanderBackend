package com.recom.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExecutorProviderTest {

    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @InjectMocks
    private ExecutorProvider serviceToTest;

    @Test
    public void testProvideClusterGeneratorExecutor() {
        // Act
        ThreadPoolTaskExecutor resultExecutor = serviceToTest.provideClusterGeneratorExecutor();

        // Assert
        assertEquals(threadPoolTaskExecutor, resultExecutor);
    }

}