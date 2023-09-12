package lib.gecom;

import lib.gecom.action.GeAction;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GePlannerTest {

    @Test
    void plan_testIfIsRunnable_givesEmptyPlan() {
        // Arrange
        final HashMap<String, Integer> agentsBelieves = new HashMap<>();
        final List<GeAction> possibleActions = new ArrayList<>();
        final HashMap<String, Integer> goal = new HashMap<>();

        final GePlanner planner = new GePlanner();

        // Act
        final Optional<Queue<GeAction>> planToTest = planner.plan(agentsBelieves, possibleActions, goal);

        // Assert
        assertTrue(planToTest.isEmpty());
    }

    @Test
    void plan_whenAllIsEmpty_givesEmptyPlan() {
        // Arrange
        final HashMap<String, Integer> goal = new HashMap<>();
        final List<GeAction> possibleActions = new ArrayList<>();
        final HashMap<String, Integer> agentsBelieves = new HashMap<>();

        final GePlanner planner = new GePlanner();

        // Act
        final Optional<Queue<GeAction>> planToTest = planner.plan(agentsBelieves, possibleActions, goal);

        // Assert
        assertTrue(planToTest.isEmpty());
    }

    @Test
    void plan_withOneSimpleAchievableAction_givesOnePlan() {
        // Arrange
        // Believes:
        final HashMap<String, Integer> agentsBelieves = new HashMap<>();
        agentsBelieves.put("goal", 0);

        // List of possible actions:
        final List<GeAction> possibleActions = new ArrayList<>();

        // Goal
        final HashMap<String, Integer> goal = new HashMap<>();
        goal.put("goal", 1);

        // Create an action that fulfills the goal
        final GeAgent agent = new GeAgent(possibleActions);
        final GeAction action = TestAction.builder()
                .name("SetGoalToOneAction")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .build();
        action.getPreconditions().put("goal", 0);
        action.getAfterEffects().put("goal", 1);
        possibleActions.add(action);

        // Act
        final GePlanner planner = new GePlanner();
        final Optional<Queue<GeAction>> planToTest = planner.plan(agentsBelieves, possibleActions, goal);

        // Assert
        assertFalse(planToTest.isEmpty());
        assertEquals(1, planToTest.get().size());
        assertTrue(planToTest.get().contains(action));
    }

    @Test
    void plan_withTwoSimpleAchievableAction_givesOnePlan() {
        // Arrange
        // Believes:
        final HashMap<String, Integer> agentsBelieves = new HashMap<>();
        agentsBelieves.put("i-am-hungry", 1);
        agentsBelieves.put("has-something-to-eat", 0);

        // List of possible actions:
        final List<GeAction> possibleActions = new ArrayList<>();

        // Goal
        final HashMap<String, Integer> goal = new HashMap<>();
        goal.put("i-am-hungry", 0);

        // Create an action that fulfills the goal
        final GeAgent agent = new GeAgent(possibleActions);
        final GeAction action1 = TestAction.builder()
                .name("find-something-to-eat")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .build();
        action1.getPreconditions().put("has-something-to-eat", 0);
        action1.getAfterEffects().put("has-something-to-eat", 1);
        possibleActions.add(action1);

        final GeAction action2 = TestAction.builder()
                .name("eat")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .build();
        action2.getPreconditions().put("has-something-to-eat", 1);
        action2.getAfterEffects().put("i-am-hungry", 0);
        action2.getAfterEffects().put("has-something-to-eat", 0);
        possibleActions.add(action2);

        // Act
        final GePlanner planner = new GePlanner();
        final Optional<Queue<GeAction>> planToTest = planner.plan(agentsBelieves, possibleActions, goal);

        // Assert
        assertFalse(planToTest.isEmpty());
        assertEquals(2, planToTest.get().size());
        assertTrue(planToTest.get().containsAll(possibleActions));
        assertEquals(action1, planToTest.get().poll());
        assertEquals(action2, planToTest.get().poll());
    }

    @SuperBuilder
    public static class TestAction extends GeAction {
        @Override
        public boolean prePerform() {
            return false;
        }

        @Override
        public boolean postPerform() {
            return false;
        }
    }

    // @TODO Responsibilities:
        /*
            Agent has a list of actions
            Agent has a list of states - known has the agents believes
            Agent should have a list of (prioritized) goals

            Actions have a Relationship with their agent. (Default NullAgent? or Optional<Agent> agent?)
                -> There should be anonymous Actions (without dependency to an agent)
            Actions have a list of preconditions
            Actions have a list of afterEffects
         */

}