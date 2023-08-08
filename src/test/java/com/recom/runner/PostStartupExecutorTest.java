package com.recom.runner;

import com.recom.service.PostStartExecutable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostStartupExecutorTest {

    private PostStartupExecutor postStartupExecutor;

    @BeforeEach
    public void beforeEach() {
        postStartupExecutor = new PostStartupExecutor();
    }


    @Test
    public void testRegisterPostStartRunner() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        final PostStartExecutable runner = mock(PostStartExecutable.class);

        // Act
        postStartupExecutor.registerPostStartRunner(runner);

        // Access the private field using reflection
        final Field registeredPostStartersField = PostStartupExecutor.class.getDeclaredField("registeredPostStarters");
        registeredPostStartersField.setAccessible(true);
        final List<PostStartExecutable> registeredPostStarters = (List<PostStartExecutable>) registeredPostStartersField.get(postStartupExecutor);

        // Assert
        assertTrue(registeredPostStarters.contains(runner));
    }

    @Test
    public void testRun_NoRegisteredRunners() {
        // Arrange
        final ApplicationArguments applicationArguments = mock(ApplicationArguments.class);

        // Act & Assert
        assertDoesNotThrow(() -> postStartupExecutor.run(applicationArguments));
    }

    @Test
    public void testRun_WithRegisteredRunners() throws Exception {
        // Arrange
        final PostStartExecutable runner1 = mock(PostStartExecutable.class);
        final PostStartExecutable runner2 = mock(PostStartExecutable.class);
        postStartupExecutor.registerPostStartRunner(runner1);
        postStartupExecutor.registerPostStartRunner(runner2);

        final ApplicationArguments applicationArguments = mock(ApplicationArguments.class);

        // Access the private field using reflection
        final Field registeredPostStartersField = PostStartupExecutor.class.getDeclaredField("registeredPostStarters");
        registeredPostStartersField.setAccessible(true);
        final List<PostStartExecutable> registeredPostStarters = (List<PostStartExecutable>) registeredPostStartersField.get(postStartupExecutor);

        // Act
        postStartupExecutor.run(applicationArguments);

        // Assert
        verify(runner1).executePostStartRunner();
        verify(runner2).executePostStartRunner();
    }
}