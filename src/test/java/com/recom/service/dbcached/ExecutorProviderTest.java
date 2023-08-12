package com.recom.service.dbcached;

import com.recom.service.dbcached.ExecutorProvider;
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
    private ExecutorProvider serviceUnderTest;

    @Test
    public void testProvideClusterGeneratorExecutor() {
        // Act
        ThreadPoolTaskExecutor resultExecutor = serviceUnderTest.provideClusterGeneratorExecutor();

        // Assert
        assertEquals(threadPoolTaskExecutor, resultExecutor);
    }

}