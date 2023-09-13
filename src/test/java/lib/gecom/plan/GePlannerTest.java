package lib.gecom.plan;

import lib.gecom.GeAgent;
import lib.gecom.action.GeAction;
import lib.gecom.plan.GePlan;
import lib.gecom.plan.GePlanner;
import lombok.experimental.SuperBuilder;
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
        final Optional<GePlan> planToTest = planner.planCheapest(agentsBelieves, possibleActions, goal);

        // Assert
        assertTrue(planToTest.isEmpty());
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
        final Optional<GePlan> planToTest = planner.planCheapest(agentsBelieves, possibleActions, goal);

        // Assert
        assertFalse(planToTest.isEmpty());
        assertEquals(1, planToTest.get().getActions().size());
        assertTrue(planToTest.get().getActions().contains(action));
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
        final GeAgent agent = new GeAgent(possibleActions);
        final GeAction findSomethingToEat = TestAction.builder()
                .name("find-something-to-eat")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .build();
        findSomethingToEat.getPreconditions().put("has-something-to-eat", 0);
        findSomethingToEat.getAfterEffects().put("has-something-to-eat", 1);
        possibleActions.add(findSomethingToEat);

        final GeAction eat = TestAction.builder()
                .name("eat")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .build();
        eat.getPreconditions().put("has-something-to-eat", 1);
        eat.getAfterEffects().put("i-am-hungry", 0);
        eat.getAfterEffects().put("has-something-to-eat", 0);
        possibleActions.add(eat);

        // Act
        final GePlanner planner = new GePlanner();
        final Optional<GePlan> planToTest = planner.planCheapest(agentsBelieves, possibleActions, goal);

        // Assert
        assertFalse(planToTest.isEmpty());
        assertEquals(2, planToTest.get().getActions().size());
        assertTrue(planToTest.get().getActions().containsAll(possibleActions));
        assertEquals(findSomethingToEat, planToTest.get().getActions().poll());
        assertEquals(eat, planToTest.get().getActions().poll());
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
        final GeAgent agent = new GeAgent(possibleActions);
        final GeAction findSomethingToEat = TestAction.builder()
                .name("find-something-to-eat")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .build();
        findSomethingToEat.getPreconditions().put("has-something-to-eat", 0);
        findSomethingToEat.getAfterEffects().put("has-something-to-eat", 1);
        possibleActions.add(findSomethingToEat);

        final GeAction eat = TestAction.builder()
                .name("eat")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .build();
        eat.getPreconditions().put("has-something-to-eat", 1);
        eat.getAfterEffects().put("i-am-hungry", 0);
        eat.getAfterEffects().put("has-something-to-eat", 0);
        possibleActions.add(eat);

        final GeAction action3 = TestAction.builder()
                .name("hunt")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .cost(10.0f)
                .build();
        action3.getPreconditions().put("has-something-to-eat", 0);
        action3.getAfterEffects().put("has-something-to-eat", 1);
        possibleActions.add(action3);

        // Act
        final GePlanner planner = new GePlanner();
        final Optional<GePlan> planToTest = planner.planCheapest(agentsBelieves, possibleActions, goal);

        // Assert
        assertFalse(planToTest.isEmpty());
        assertEquals(2, planToTest.get().getActions().size());
        assertTrue(planToTest.get().getActions().containsAll(List.of(findSomethingToEat, eat)));
        assertEquals(findSomethingToEat, planToTest.get().getActions().poll());
        assertEquals(eat, planToTest.get().getActions().poll());
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
        final GeAgent agent = new GeAgent(possibleActions);
        final GeAction findSomethingToEat = TestAction.builder()
                .name("find-something-to-eat")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .build();
        findSomethingToEat.getPreconditions().put("has-something-to-eat", 0);
        findSomethingToEat.getAfterEffects().put("has-something-to-eat", 1);
        possibleActions.add(findSomethingToEat);

        final GeAction eat = TestAction.builder()
                .name("eat")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .build();
        eat.getPreconditions().put("has-something-to-eat", 1);
        eat.getAfterEffects().put("i-am-hungry", 0);
        eat.getAfterEffects().put("has-something-to-eat", 0);
        possibleActions.add(eat);

        final GeAction hunt = TestAction.builder()
                .name("hunt")
                .agent(agent) // @TODO dependency of action to agent is not good. Decouple it. I dont know if the action should be bound to an agent Makes sense if action has an internal state!
                .cost(10.0f)
                .build();
        hunt.getPreconditions().put("has-something-to-eat", 0);
        hunt.getAfterEffects().put("has-something-to-eat", 1);
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