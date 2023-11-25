package com.recom.goapcom.plan;


import com.recom.goapcom.action.GeAction;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.*;

@Slf4j
public class GePlanner {

    @NonNull
    public Optional<GePlan> planCheapest(
            @NonNull final HashMap<String, Integer> agentsBelieves,
            @NonNull final List<GeAction> possibleActions,
            @NonNull final HashMap<String, Integer> goal
    ) {
        final PriorityQueue<GePlan> plans = plan(agentsBelieves, possibleActions, goal);

        if (plans.isEmpty()) {
            return Optional.empty();
        } else {
            return plans.stream()
                    .min(Comparable::compareTo);
        }
    }

    @NonNull
    public PriorityQueue<GePlan> plan(
            @NonNull final HashMap<String, Integer> agentsBelieves,
            @NonNull final List<GeAction> possibleActions,
            @NonNull final HashMap<String, Integer> goal
    ) {
        final List<GeAction> usableActions = new ArrayList<>();
        possibleActions.forEach(possibleAction -> {
            if (possibleAction.isAchievable()) {
                usableActions.add(possibleAction);
            }
        });

        final List<GePlanningNode> leaves = new ArrayList<>();
        final GePlanningNode startNode = new GePlanningNode(null, agentsBelieves, null, 0f);

        boolean success = buildGraph(startNode, leaves, usableActions, goal);

        if (!success) {
            log.error("No solution found!");
            return new PriorityQueue<>();
        }

        final PriorityQueue<GePlan> plans = new PriorityQueue<>();
        for (@NonNull final GePlanner.GePlanningNode currentPlansLastNode : leaves) {
            final ArrayList<GeAction> currentPlansActions = new ArrayList<>();
            GePlanningNode node = currentPlansLastNode;
            while (node != null) {
                if (node.action != null) {
                    currentPlansActions.add(node.action);
                }
                node = node.parent;
            }
            Collections.reverse(currentPlansActions);
            final GePlan plan = GePlan.builder()
                    .actions(new LinkedList<>(currentPlansActions))
                    .cost(currentPlansLastNode.cost)
                    .build();

            plans.add(plan);
        }

        return plans;
    }

    private boolean buildGraph(
            @NonNull final GePlanner.GePlanningNode parent,
            @NonNull final List<GePlanningNode> foundPlans,
            @NonNull final List<GeAction> usableActions,
            @NonNull final HashMap<String, Integer> goal
    ) {
        boolean foundPath = false;

        // copy the effects of the compatible action to the new state (copied from parent state) for be next node
        // for each compatible action, add we branch of from this node and add a new branch / node to the graph
        for (final GeAction action : usableActions) {
            if (action.arePreconditionsMet(parent.getState())) {
                final HashMap<String, Integer> currentState = (HashMap<String, Integer>) parent.getState().clone(); // copy the state! (depp clone is not necessary because String and Integers are immutable)
                for (final Map.Entry<String, Integer> effect : action.getEffects().entrySet()) {
                    if (currentState.containsKey(effect.getKey())) {
                        currentState.put(effect.getKey(), effect.getValue());
                    }
                }

                final GePlanningNode derivedNodeFromPerformedAction = new GePlanningNode(parent, currentState, action, parent.cost + action.getCost());

                if (goalAchieved(goal, currentState)) {
                    // stop building the graph and return the path; as we found a path
                    foundPlans.add(derivedNodeFromPerformedAction);
                    foundPath = true;
                } else {
                    // we remove the current action from the list of usable actions
                    final List<GeAction> subsetOfActions = subsetOfActions(usableActions, action); // subset of actions without the current action
                    // we recursively call this method again with the new node and the new subset of actions
                    final boolean found = buildGraph(derivedNodeFromPerformedAction, foundPlans, subsetOfActions, goal);
                    if (found) {
                        foundPath = true;
                    }
                }
            }
        }

        return foundPath;
    }

    private boolean goalAchieved(
            @NonNull final HashMap<String, Integer> goal,
            @NonNull final HashMap<String, Integer> stateToCompareWith
    ) {
        return goal.entrySet().stream()
                .allMatch(precondition -> stateToCompareWith.containsKey(precondition.getKey()) && stateToCompareWith.get(precondition.getKey()).equals(precondition.getValue()));
    }

    @NonNull
    private List<GeAction> subsetOfActions(
            @NonNull final List<GeAction> usableActions,
            @NonNull final GeAction actionToRemove
    ) {
        final ArrayList<GeAction> copy = new ArrayList<>(usableActions);
        copy.removeIf(action -> action.equals(actionToRemove));

        return copy;
    }


    @Getter
    private static class GePlanningNode {

        @Nullable
        public final GePlanningNode parent;

        @NonNull
        public final HashMap<String, Integer> state;

        @Nullable
        public final GeAction action;

        @NonNull
        public Float cost;

        public GePlanningNode(
                @Nullable final GePlanningNode parent,
                @NonNull final HashMap<String, Integer> state,
                @Nullable final GeAction action,
                @NonNull final Float cost
        ) {
            this.parent = parent;
            this.state = state;
            this.action = action;
            this.cost = cost;
        }
    }

}
