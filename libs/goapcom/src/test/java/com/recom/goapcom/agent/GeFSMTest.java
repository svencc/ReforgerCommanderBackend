package com.recom.goapcom.agent;

import com.recom.goapcom.agent.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeFSMTest {

    private GeAgent agent;
    private GeFSM fsmToTest;
    private IdleState idleState;
    private PerformActionState performActionState;
    private MoveToState moveToState;

    @BeforeEach
    void beforeEach() {
        agent = new GeAgent();

        idleState = new IdleState();
        performActionState = new PerformActionState();
        moveToState = new MoveToState();

        final List<FSMState> states = List.of(idleState, performActionState, moveToState);
        fsmToTest = new GeFSM(agent, states);
    }

    @Test
    void start() {
        // Pre-Assert
        assertFalse(fsmToTest.isRunning());
        assertFalse(fsmToTest.isStopped());

        // Act
        fsmToTest.start();

        // Assert
        assertTrue(fsmToTest.isRunning());
        assertFalse(fsmToTest.isStopped());
    }

    @Test
    void failedCreation_withoutStartableState() {
        // Arrange, Act & Assert
        assertThrows(IllegalStateException.class, () -> new GeFSM(agent, List.of(new PerformActionState(), new MoveToState())));
    }

    @Test
    void stop() {
        // Pre-Assert
        assertFalse(fsmToTest.isRunning());
        assertFalse(fsmToTest.isStopped());

        // Act
        fsmToTest.start();
//        fsmToTest.transitionToState(FSMStates.PERFORMABLE);
        fsmToTest.requestTransition(FSMStates.PERFORMABLE);
        fsmToTest.stop();

        // Assert
        assertFalse(fsmToTest.isRunning());
        assertTrue(fsmToTest.isStopped());
    }

    @Test
    void transitionToState() {
        // Act
        fsmToTest.start(); // starts and transitions into IdleState

        // Assert
        assertTrue(fsmToTest.getMaybeCurrentState().isPresent());
        fsmToTest.requestTransition(FSMStates.PERFORMABLE);
        assertTrue(fsmToTest.getMaybeCurrentState().get() instanceof PerformActionState);
    }

    @Test
    void process_withUpdateLastUpdateTime() {
        // Arrange
        double timeBeforeUpdate = System.currentTimeMillis() - 1;

        // Act
        fsmToTest.start();
        fsmToTest.process();

        // Assert
        assertTrue(fsmToTest.getLastUpdate() > timeBeforeUpdate);
    }

    @Test
    void requestTransition() {
        // Act
        fsmToTest.start(); // starts and transitions into IdleState

        // Assert
        assertTrue(fsmToTest.getMaybeCurrentState().isPresent());
        fsmToTest.requestTransition(FSMStates.PERFORMABLE);
        assertTrue(fsmToTest.getMaybeCurrentState().get() instanceof PerformActionState);
    }

    @Test
    void isRunning() {
        // Act
        fsmToTest.start();

        // Assert
        assertTrue(fsmToTest.isRunning());
    }

    @Test
    void isStopped() {
        // Act
        fsmToTest.start();
        fsmToTest.stop();

        // Assert
        assertFalse(fsmToTest.isRunning());
        assertTrue(fsmToTest.isStopped());
    }

}