package lib.gecom;


import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class GePlanner {

    @NonNull
    public Optional<Queue<GeAction>> plan(
            @NonNull final List<GeAction> possibleActions,
            @NonNull final HashMap<String, Integer> goal, // @TODO do we have state objects? what are those Maps?????
            @NonNull final GeWorldStates worldStates
    ) {
        final List<GeAction> usableActions = new ArrayList<>();
        possibleActions.forEach(possibleAction -> {
            if (possibleAction.isAchievable()) {
                usableActions.add(possibleAction);
            }
        });

        final List<GeNode> leaves = new ArrayList<>();
        final GeNode start = new GeNode(null, GeWorld.getInstance().getWorldStates().getStates(), null, 0f);

        boolean success = buildGraph(start, leaves, usableActions, goal);

        if (!success) {
            System.out.println("No solution found!");
            // @TODO throw exception? and log!
            return Optional.empty();
        }

        // TODO look to dijkstra's algorithm.... for the following:

        GeNode cheapest = null;
        for (GeNode leaf : leaves) {
            if (cheapest == null) {
                cheapest = leaf;
            } else if (leaf.cost < cheapest.cost) {
                cheapest = leaf;
            }
        }

        // @TODO return a list of actions (linked list)
        List<GeAction> result = new ArrayList<>();
        GeNode node = cheapest;
        while (node != null) {
            if (node.action != null) {
                result.add(node.action);
            }
            node = node.parent;
        }
        Collections.reverse(result); // TEST THAT

        // could return a PriorityQueue with nodes; it is then sorted
        return Optional.of(new LinkedList<GeAction>(result));
    }

    private boolean buildGraph(
            @NonNull final GeNode parent,
            @NonNull final List<GeNode> foundPlans,
            @NonNull final List<GeAction> usableActions,
            @NonNull final HashMap<String, Integer> goal
    ) {
        boolean foundPath = false;

        // copy the effects of the compatible action to the new state (copied from parent state) for be next node
        // for each compatible action, add we branch of from this node and add a new branch / node to the graph
        for (final GeAction action : usableActions) {
            if (action.isAchievableGiven(parent.getState())) {
                final HashMap<String, Integer> currentState = (HashMap<String, Integer>) parent.getState().clone(); // copy the state! (depp clone is not necessary because String and Integers are immutable)
                for (final Map.Entry<String, Integer> effect : action.getAfterEffects().entrySet()) {
                    if (currentState.containsKey(effect.getKey())) {
                        currentState.put(effect.getKey(), effect.getValue());
                    }
                }

                final GeNode derivedNodeFromPerformedAction = new GeNode(parent, currentState, action, parent.cost + action.getCost());

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
        // Check if all keys from 'goal' are present in 'stateToCompareWith'
        // We can be confident that the values are the same because we are simply updating the state with the effects of
        // the action.
        return stateToCompareWith.keySet().containsAll(goal.keySet());
    }

    @NonNull
    private List<GeAction> subsetOfActions(
            @NonNull final List<GeAction> usableActions,
            @NonNull final GeAction actionToRemove
    ) {
        return usableActions.stream()
//                .filter(action -> !action.getName().equals(actionToRemove.getName()))
                .filter(action -> action.equals(actionToRemove))
                .collect(Collectors.toList());
    }


    //@TODO can we use our old node?
    @Getter
    public class GeNode {
        @Nullable
        public final GeNode parent;
        @NonNull
        public final HashMap<String, Integer> state;
        @Nullable
        public final GeAction action;
        @NonNull
        public Float cost;

        public GeNode(
                @Nullable final GeNode parent,
                @NonNull final HashMap<String, Integer> state,
                @Nullable final GeAction action,
                @NonNull final Float cost
        ) {
            this.parent = parent;
//            this.state = (HashMap<String, Integer>) state.clone(); // @TODO make a copy!
            this.state = state;
            this.action = action;
            this.cost = cost;
        }
    }

}
