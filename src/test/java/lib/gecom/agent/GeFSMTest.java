package lib.gecom.agent;

import lib.gecom.agent.state.FSMState;
import lib.gecom.agent.state.IdleState;
import lib.gecom.agent.state.MoveToState;
import lib.gecom.agent.state.PerformActionState;
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

        idleState = new IdleState(agent);
        performActionState = new PerformActionState(agent);
        moveToState = new MoveToState(agent);

        fsmToTest = new GeFSM(agent);

        final List<FSMState> states = List.of(idleState, performActionState, moveToState);
        fsmToTest.getStates().addAll(states);
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
    void start_failedDueToNoStartableState() {
        // Arrange
        fsmToTest.getStates().remove(idleState);

        // Act
        // exception accepted:
        assertThrows(IllegalStateException.class, () -> fsmToTest.start());
    }

    @Test
    void stop_failedDueToNoStoppableState() {
        // Arrange
        fsmToTest.start();
        fsmToTest.transitionToState(performActionState);
        fsmToTest.getStates().remove(idleState);

        // Act
        // exception accepted:
        assertThrows(IllegalStateException.class, () -> fsmToTest.stop());
    }

    @Test
    void stop() {
        // Pre-Assert
        assertFalse(fsmToTest.isRunning());
        assertFalse(fsmToTest.isStopped());

        // Act
        fsmToTest.start();
        fsmToTest.transitionToState(performActionState);
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
        fsmToTest.transitionToState(performActionState);
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
        fsmToTest.requestTransition(fsmToTest.getMaybeCurrentState().get(), performActionState);
        assertTrue(fsmToTest.getMaybeCurrentState().get() instanceof PerformActionState);
    }

    @Test
    void getStates() {
        assertEquals(3, fsmToTest.getStates().size());
        assertTrue(fsmToTest.getStates().containsAll(List.of(idleState, performActionState, moveToState)));
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