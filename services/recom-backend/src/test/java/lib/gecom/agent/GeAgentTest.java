package lib.gecom.agent;

import lib.gecom.TestAction;
import lib.gecom.agent.state.PerformActionState;
import lib.gecom.plan.GePlanner;
import lib.gecom.stuff.GeGoal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeAgentTest {

    @Test
    public void simpleStartStopTest() {
        // Arrange
        final GePlanner gePlanner = new GePlanner();
        final GeAgent agentToTest = GeAgentFactory.createAgent(gePlanner);

        // Act && Assert
        assertFalse(agentToTest.wasStarted());
        assertFalse(agentToTest.wasStopped());
        agentToTest.start();
        assertTrue(agentToTest.wasStarted());

        // Act && Assert
        assertTrue(agentToTest.wasStarted());
        assertFalse(agentToTest.wasStopped());
        agentToTest.stop();
        assertTrue(agentToTest.wasStarted());
        assertTrue(agentToTest.wasStopped());
    }


    @Test
    public void testAgentToEat_withRecognizingHungry_generatePlan_andPerformPlan() {
        // Arrange
        final TestAction eat = new TestAction("eat");
        eat.getPreconditions().put("hungry", 1);
        eat.getEffects().put("hungry", 0);

        final GeGoal getFull = GeGoal.builder().build();
        getFull.getStatesToReach().put("hungry", 0);

        final GePlanner gePlanner = new GePlanner();

        final GeAgent agentToTest = GeAgentFactory.createAgent(gePlanner);
        agentToTest.getPossibleActions().add(eat);
        agentToTest.getAgentsBelieves().put("hungry", 1);
        agentToTest.getGoals().add(getFull);

        // Act && Assert
        agentToTest.start();

        // Act && Assert
        agentToTest.update(); // remain in idle (after start); nothing happens in IdleState
        agentToTest.calculateActionPlan();
        agentToTest.update(); // with an action plan present, fsm should switch to PerformActionState
        assertNotNull(agentToTest.getFsm());
        assertTrue(agentToTest.getFsm().getMaybeCurrentState().isPresent());
        assertEquals(PerformActionState.class, agentToTest.getFsm().getMaybeCurrentState().get().getClass());

        // Act && Assert
        agentToTest.stop();
        assertFalse(agentToTest.getFsm().getMaybeCurrentState().isPresent());

        // Assert
        assertEquals(0, agentToTest.getAgentsBelieves().get("hungry"));
    }

}