package lib.gecom.agent;

import lib.gecom.TestAction;
import lib.gecom.action.GeAction;
import lib.gecom.plan.GePlanner;
import lib.gecom.stuff.GeGoal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeAgentTest {

    @Test
    public void test() {
        // Arrange
        final TestAction eat = new TestAction("eat");
        eat.getPreconditions().put("hungry", 1);
        eat.getEffects().put("hungry", 0);

        final GeGoal getFull = GeGoal.builder().build();
        getFull.getStatesToReach().put("hungry", 0);

        final GePlanner gePlanner = new GePlanner();

        final GeAgent agentToTest = new GeAgent(gePlanner);
        agentToTest.getPossibleActions().add(eat);
        agentToTest.getAgentsBelieves().put("hungry", 1);
        agentToTest.getGoals().add(getFull);

        // Act
        agentToTest.start();
        agentToTest.update();

        // Assert
//        assertEquals(0, agentToTest.getAgentsBelieves().get("hungry"));
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

}