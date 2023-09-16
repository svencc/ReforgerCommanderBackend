package lib.gecom.agent;

import lib.gecom.action.GeAction;
import lib.gecom.plan.GePlan;
import lib.gecom.plan.GePlanner;
import lib.gecom.stuff.GeGoal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.*;

@Getter
public class GeAgent {

    @NonNull
    private final List<GeAction> possibleActions = new ArrayList<>();

    @NonNull
    private final PriorityQueue<GeGoal> goals = new PriorityQueue<>();

    @NonNull
    private final HashMap<String, Integer> agentsBelieves = new HashMap<>();
    @NonNull
    private final Queue<GeAction> plan = new LinkedList<>();
    @NonNull
    private final Stack<GeAction> currentActionStack = new Stack<>();
    @Nullable
    @Setter(AccessLevel.PACKAGE)
    private GePlanner planner;
    @Nullable
    @Setter(AccessLevel.PACKAGE)
    private GeFSM fsm;

    @Nullable
    private GeAction currentAction;

    @Nullable
    private GeGoal currentGoal;

    private boolean isExecutingAction = false;


//    GeAgent(@NonNull final GePlanner planner, @NonNull final GeFSM fsm) {
//        this.planner = planner;
//        this.fsm = fsm;
//    }

    // Agents seem to implement these methods (Unity; maybe this will be useful for us later)
    public void start() {
        fsm.start();
        // This is where the agent is initially set up.
        // This can include initializing variables or setting up components.
    }

    public void update() {
        // Here updates are performed for the agent in every frame.
        // This can include moving the agent, checking for collisions, or other ongoing operations.

        // i think moving has to be in the agents FSM and should not be part of the action (currentAction.isMoving())
        if (!currentActionStack.isEmpty() && currentAction.isActionRunning()) {
            // agent is on target position and has to run the action
            if (false) {
                // TODO ::::
//            if (currentAction.getAgent().hasPath() && currentAction.getAgent().remainingDistance() <= 1f) {
                if (!isExecutingAction) {
                    isExecutingAction = true;
                    currentAction.prePerform();
                    currentAction.setActionRunning(true);

                    // ------ wait action duration ------ //
                    // so it sounds like we have to wait for the action duration and trigger than a listener!

                    currentAction.setActionRunning(false);
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
            for (GeGoal subgoal : goals) {
                final Optional<GePlan> plan = planner.planCheapest(agentsBelieves, possibleActions, subgoal.getStatesToReach());
                if (plan.isPresent()) {
                    currentActionStack.addAll(plan.get().getActions());
                    currentGoal = subgoal;
                    break;
                }
            }
        }

        // move to target position
        if (!currentActionStack.isEmpty()) {
            if (currentAction.prePerform()) {
                currentAction.setActionRunning(true);
//                currentActionStack.pop();
                currentAction = currentActionStack.peek();
                currentAction.setActionRunning(true);
//                currentAction.getAgent().setDestination(currentAction.getTarget().getTargetPosition());
            } else {
                // ------ wait action duration ------ //
                // so it sounds like we have to wait for the action duration and trigger than a listener!

                currentAction.setActionRunning(false);
                currentAction.postPerform();
                currentActionStack.pop();
            }
        }
    }

}
