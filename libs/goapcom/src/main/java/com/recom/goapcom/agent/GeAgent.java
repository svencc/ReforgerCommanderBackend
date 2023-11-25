package com.recom.goapcom.agent;

import com.recom.goapcom.action.GeAction;
import com.recom.goapcom.plan.GePlan;
import com.recom.goapcom.plan.GePlanner;
import com.recom.goapcom.stuff.GeGoal;
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
    @Setter
    @Nullable
    private GeAction currentAction;
    @Nullable
    private GeGoal currentGoal;

//    private boolean isExecutingAction = false; // @TODO -> von FSM abfragen!
    private boolean wasStarted = false;
    private boolean wasStopped = false;


    public boolean wasStarted() {
        return wasStarted;
    }

    public boolean wasStopped() {
        return wasStopped;
    }

    public boolean isRunning() throws IllegalStateException {
        checkPreconditions();
        return fsm.isRunning();
    }

    private void checkPreconditions() throws IllegalStateException {
        if (fsm == null) {
            throw new IllegalStateException("FSM is not set");
        }
        if (planner == null) {
            throw new IllegalStateException("Planner is not set");
        }
    }

    public void start() throws IllegalStateException {
        checkPreconditions();
        wasStarted = true;
        fsm.start();
    }

    public void stop() throws IllegalStateException {
        checkPreconditions();
        wasStopped = true;
        fsm.stop();
    }

    public void update() {
        boolean agentIsRunnable = wasStarted && !wasStopped && fsm != null && planner != null;
        if (!agentIsRunnable) {
            return;
        }
        if (currentAction == null && !currentActionStack.isEmpty()) {
            currentAction = currentActionStack.peek();
        }
        if (currentAction != null) {
            currentAction.setActionRunning(true);
            fsm.process();
        }


//
//
//        // i think moving has to be in the agents FSM and should not be part of the action (currentAction.isMoving())
//        if (!currentActionStack.isEmpty() && currentAction.isActionRunning()) {
//            // agent is on target position and has to run the action
//            if (false) {
//                // TODO ::::
////            if (currentAction.getAgent().hasPath() && currentAction.getAgent().remainingDistance() <= 1f) {
//                if (!isExecutingAction) {
//                    isExecutingAction = true;
//                    currentAction.prePerform();
//                    currentAction.setActionRunning(true);
//
//                    // ------ wait action duration ------ //
//                    // so it sounds like we have to wait for the action duration and trigger than a listener!
//
//                    currentAction.setActionRunning(false);
//                    currentAction.postPerform();
//                    isExecutingAction = false;
//
//                    currentActionStack.pop();
//                }
//            }
//
//            // agent is not on target position and has to move
//            return;
//        }
//
////        final GeWorldStates agentsBelieves = GeWorld.getInstance().getWorldStates();
//        final HashMap<String, Integer> agentsBelieves = new HashMap<>();
//        if (currentActionStack.isEmpty()) {
//            for (GeGoal subgoal : goals) {
//                final Optional<GePlan> plan = planner.planCheapest(agentsBelieves, possibleActions, subgoal.getStatesToReach());
//                if (plan.isPresent()) {
//                    currentActionStack.addAll(plan.get().getActions());
//                    currentGoal = subgoal;
//                    break;
//                }
//            }
//        }
//
//        // move to target position
//        if (!currentActionStack.isEmpty()) {
//            if (currentAction.prePerform()) {
//                currentAction.setActionRunning(true);
////                currentActionStack.pop();
//                currentAction = currentActionStack.peek();
//                currentAction.setActionRunning(true);
////                currentAction.getAgent().setDestination(currentAction.getTarget().getTargetPosition());
//            } else {
//                // ------ wait action duration ------ //
//                // so it sounds like we have to wait for the action duration and trigger than a listener!
//
//                currentAction.setActionRunning(false);
//                currentAction.postPerform();
//                currentActionStack.pop();
//            }
//        }
    }

    public boolean calculateActionPlan() {
        if (currentActionStack.isEmpty()) {
            for (final GeGoal goal : goals) {
                final Optional<GePlan> plan = planner.planCheapest(agentsBelieves, possibleActions, goal.getStatesToReach());
                if (plan.isPresent()) {
                    currentActionStack.addAll(plan.get().getActions());
                    currentGoal = goal;
                    return true;
                }
            }
        }

        return false;
    }

}
