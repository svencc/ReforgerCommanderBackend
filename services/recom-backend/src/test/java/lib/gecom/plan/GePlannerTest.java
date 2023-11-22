package lib.gecom.plan;

import lib.gecom.TestAction;
import lib.gecom.action.GeAction;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GePlannerTest {

    @Test
    void planCheapest_testIfIsRunnable_givesEmptyPlan() {
        // Arrange
        final HashMap<String, Integer> agentsBelieves = new HashMap<>();
        final List<GeAction> possibleActions = new ArrayList<>();
        final HashMap<String, Integer> goal = new HashMap<>();

        final GePlanner planner = new GePlanner();

        // Act
        final Optional<GePlan> planToTest = planner.planCheapest(agentsBelieves, possibleActions, goal);

        // Assert
        assertTrue(planToTest.isEmpty());
    }

    @Test
    void planCheapest_whenAllIsEmpty_givesEmptyPlan() {
        // Arrange
        final HashMap<String, Integer> goal = new HashMap<>();
        final List<GeAction> possibleActions = new ArrayList<>();
        final HashMap<String, Integer> agentsBelieves = new HashMap<>();

        final GePlanner planner = new GePlanner();

        // Act
        final Optional<GePlan> maybePlanToTest = planner.planCheapest(agentsBelieves, possibleActions, goal);

        // Assert
        assertTrue(maybePlanToTest.isEmpty());
    }

    @Test
    void planCheapest_withOneSimpleAchievableAction_givesOnePlan() {
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
        final GeAction action = new TestAction("SetGoalToOneAction");
        action.getPreconditions().put("goal", 0);
        action.getEffects().put("goal", 1);
        possibleActions.add(action);

        // Act
        final GePlanner planner = new GePlanner();
        final Optional<GePlan> maybePlanToTest = planner.planCheapest(agentsBelieves, possibleActions, goal);

        // Assert
        assertFalse(maybePlanToTest.isEmpty());
        assertEquals(1, maybePlanToTest.get().getActions().size());
        assertTrue(maybePlanToTest.get().getActions().contains(action));
    }

    @Test
    void planCheapest_withTwoSimpleAchievableAction_givesOnePlan() {
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
        final GeAction findSomethingToEat = new TestAction("find-something-to-eat");
        findSomethingToEat.getPreconditions().put("has-something-to-eat", 0);
        findSomethingToEat.getEffects().put("has-something-to-eat", 1);
        possibleActions.add(findSomethingToEat);

        final GeAction eat = new TestAction("eat");
        eat.getPreconditions().put("has-something-to-eat", 1);
        eat.getEffects().put("i-am-hungry", 0);
        eat.getEffects().put("has-something-to-eat", 0);
        possibleActions.add(eat);

        // Act
        final GePlanner planner = new GePlanner();
        final Optional<GePlan> maybePlanToTest = planner.planCheapest(agentsBelieves, possibleActions, goal);

        // Assert
        assertFalse(maybePlanToTest.isEmpty());
        assertEquals(2, maybePlanToTest.get().getActions().size());
        assertTrue(maybePlanToTest.get().getActions().containsAll(possibleActions));
        assertEquals(findSomethingToEat, maybePlanToTest.get().getActions().poll());
        assertEquals(eat, maybePlanToTest.get().getActions().poll());
    }

    @Test
    void planCheapest_withTwoConcurrentAchievableBranches_givesCheapestPlan() {
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
        final GeAction findSomethingToEat = new TestAction("find-something-to-eat");
        findSomethingToEat.getPreconditions().put("has-something-to-eat", 0);
        findSomethingToEat.getEffects().put("has-something-to-eat", 1);
        possibleActions.add(findSomethingToEat);

        final GeAction eat = new TestAction("eat");
        eat.getPreconditions().put("has-something-to-eat", 1);
        eat.getEffects().put("i-am-hungry", 0);
        eat.getEffects().put("has-something-to-eat", 0);
        possibleActions.add(eat);

        final GeAction action3 = new TestAction("hunt", 10.0f);
        action3.getPreconditions().put("has-something-to-eat", 0);
        action3.getEffects().put("has-something-to-eat", 1);
        possibleActions.add(action3);

        // Act
        final GePlanner planner = new GePlanner();
        final Optional<GePlan> maybePlanToTest = planner.planCheapest(agentsBelieves, possibleActions, goal);

        // Assert
        assertFalse(maybePlanToTest.isEmpty());
        assertEquals(2, maybePlanToTest.get().getActions().size());
        assertTrue(maybePlanToTest.get().getActions().containsAll(List.of(findSomethingToEat, eat)));
        assertEquals(findSomethingToEat, maybePlanToTest.get().getActions().poll());
        assertEquals(eat, maybePlanToTest.get().getActions().poll());
    }

    @Test
    void plan_withTwoConcurrentAchievableBranches_gives2Plans() {
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
        final GeAction findSomethingToEat = new TestAction("find-something-to-eat");
        findSomethingToEat.getPreconditions().put("has-something-to-eat", 0);
        findSomethingToEat.getEffects().put("has-something-to-eat", 1);
        possibleActions.add(findSomethingToEat);

        final GeAction eat = new TestAction("eat");
        eat.getPreconditions().put("has-something-to-eat", 1);
        eat.getEffects().put("i-am-hungry", 0);
        eat.getEffects().put("has-something-to-eat", 0);
        possibleActions.add(eat);

        final GeAction hunt = new TestAction("hunt", 10.0f);
        hunt.getPreconditions().put("has-something-to-eat", 0);
        hunt.getEffects().put("has-something-to-eat", 1);
        possibleActions.add(hunt);

        // Act
        final GePlanner planner = new GePlanner();
        final PriorityQueue<GePlan> multiplePlansToTest = planner.plan(agentsBelieves, possibleActions, goal);

        // Assert
        assertFalse(multiplePlansToTest.isEmpty());
        assertEquals(2, multiplePlansToTest.size());
        assertTrue(multiplePlansToTest.poll().getActions().containsAll(List.of(findSomethingToEat, eat)));
        assertTrue(multiplePlansToTest.poll().getActions().containsAll(List.of(hunt, eat)));
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