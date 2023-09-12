package lib.gecom;

import lib.gecom.action.GeAction;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

public class GeAgent {

    @Getter
    @NonNull
    private final List<GeAction> possibleActions = new ArrayList<>();
    @NonNull
    private final PriorityQueue<GeSubgoal> prioritizedSubgoals = new PriorityQueue<>();
    @NonNull
    private final GePlanner planner; // Get Rid of Planner in Agent? Or assign one global Planner instance vie Constructor DI?
    @NonNull
    private final Queue<GeAction> plan = new LinkedList<>();
    @NonNull
    private final Stack<GeAction> currentActionStack = new Stack<>();
    private GeAction currentAction;
    private GeSubgoal currentGoal;

    private boolean isExecutingAction = false;

    public GeAgent(final List<GeAction> possibleActions) {
        if (possibleActions.isEmpty()) {
            this.possibleActions.addAll(possibleActions);
        }

        planner = new GePlanner();
    }

    public void addSubgoal(@NonNull final GeSubgoal subgoal) {
        prioritizedSubgoals.add(subgoal);
    }

    // Agents seem to implement these methods (Unity; maybe this will be useful for us later)
    public void Start() {
        // This is where the agent is initially set up.
        // This can include initializing variables or setting up components.
    }

    public void Update() {
        // Here updates are performed for the agent in every frame.
        // This can include moving the agent, checking for collisions, or other ongoing operations.

        // i think moving has to be in the agents FSM and should not be part of the action (currentAction.isMoving())
        if (!currentActionStack.isEmpty() && currentAction.isMoving()) {
            // agent is on target position and has to run the action
            if (false) {
                // TODO ::::
//            if (currentAction.getAgent().hasPath() && currentAction.getAgent().remainingDistance() <= 1f) {
                if (!isExecutingAction) {
                    isExecutingAction = true;
                    currentAction.prePerform();
                    currentAction.setMoving(true);

                    // ------ wait action duration ------ //
                    // so it sounds like we have to wait for the action duration and trigger than a listener!

                    currentAction.setMoving(false);
                    currentAction.postPerform();
                    isExecutingAction = false;

                    currentActionStack.pop();
                }
            }

            // agent is not on target position and has to move
            return;
        }

//        final GeWorldStates agentsBelieves = GeWorld.getInstance().getWorldStates();
        final HashMap<String, Integer> agentsBelieves = new HashMap<>();
        if (currentActionStack.isEmpty()) {
            for (GeSubgoal subgoal : prioritizedSubgoals) {
                final Optional<Queue<GeAction>> plan = planner.plan(agentsBelieves, possibleActions, subgoal.getStatesToReach());
                if (plan.isPresent()) {
                    currentActionStack.addAll(plan.get());
                    currentGoal = subgoal;
                    break;
                }
            }
        }

        // move to target position
        if (!currentActionStack.isEmpty()) {
            if (currentAction.prePerform()) {
                currentAction.setMoving(true);
//                currentActionStack.pop();
                currentAction = currentActionStack.peek();
                currentAction.setMoving(true);
//                currentAction.getAgent().setDestination(currentAction.getTarget().getTargetPosition());
            } else {
                // ------ wait action duration ------ //
                // so it sounds like we have to wait for the action duration and trigger than a listener!

                currentAction.setMoving(false);
                currentAction.postPerform();
                currentActionStack.pop();
            }
        }
    }

}
