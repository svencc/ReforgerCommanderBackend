package lib.gecom;


import lib.gecom.action.GeAction;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;

public class GePlanner {

    @NonNull
    public Optional<Queue<GeAction>> planCheapest(
            @NonNull final HashMap<String, Integer> agentsBelieves,
            @NonNull final List<GeAction> possibleActions,
            @NonNull final HashMap<String, Integer> goal
    ) {
        final List<GeNode> leaves = plan(agentsBelieves, possibleActions, goal);

        if (leaves.isEmpty()) {
            return Optional.empty();
        }

        // look for cheapest; @TODO look to dijkstra's algorithm
        // iam sure there is a better way to do this than here!

        GeNode cheapest = null;
        for (GeNode leaf : leaves) {
            if (cheapest == null) {
                cheapest = leaf;
            } else if (leaf.cost < cheapest.cost) {
                cheapest = leaf;
            }
        }

        // @TODO return a list of actions (linked list)
        final List<GeAction> result = new ArrayList<>();
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

    public List<GeNode> plan(
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

        final List<GeNode> leaves = new ArrayList<>();
        final GeNode start = new GeNode(null, agentsBelieves, null, 0f);

        boolean success = buildGraph(start, leaves, usableActions, goal);

        if (!success) {
            System.out.println("No solution found!");
            // @TODO throw exception? and log!
            return List.of();
        }

        return leaves;

//        // TODO look to dijkstra's algorithm.... for the following:
//        GeNode cheapest = null;
//        for (GeNode leaf : leaves) {
//            if (cheapest == null) {
//                cheapest = leaf;
//            } else if (leaf.cost < cheapest.cost) {
//                cheapest = leaf;
//            }
//        }
//
//        // @TODO return a list of actions (linked list)
//        final List<GeAction> result = new ArrayList<>();
//        GeNode node = cheapest;
//        while (node != null) {
//            if (node.action != null) {
//                result.add(node.action);
//            }
//            node = node.parent;
//        }
//        Collections.reverse(result); // TEST THAT
//
//        // could return a PriorityQueue with nodes; it is then sorted
//        return Optional.of(new LinkedList<GeAction>(result));
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
            if (action.arePreconditionsMet(parent.getState())) {
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
        return goal.entrySet().stream()
                .allMatch(
                        precondition -> {
                            if (!stateToCompareWith.containsKey(precondition.getKey())) {
                                return false;
                            } else {
                                return stateToCompareWith.get(precondition.getKey()).equals(precondition.getValue());
                            }
                        }
                );
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
