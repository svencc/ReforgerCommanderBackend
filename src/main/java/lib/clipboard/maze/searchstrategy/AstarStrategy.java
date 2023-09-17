package lib.clipboard.maze.searchstrategy;

import lib.clipboard.maze.Node;
import lombok.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

/**
 * A* Search
 */
public class AstarStrategy {

    @NonNull
    public <T> Optional<Node<T>> search(
            @NonNull final T initialState,
            @NonNull final Predicate<T> goalTest,
            @NonNull final Function<T, List<T>> successors,
            @NonNull final ToDoubleFunction<T> heuristic
    ) {
        // frontier is where we've yet to go
        final PriorityQueue<Node<T>> frontier = new PriorityQueue<>();
        frontier.offer(Node.astarStartNode(initialState, heuristic.applyAsDouble(initialState)));

        // explored is where we've already been
        final Map<T, Double> explored = new HashMap<>();
        explored.put(initialState, 0.0);

        // keep going while there is more to explore
        while (!frontier.isEmpty()) {
            final Node<T> currentNode = frontier.poll();
            final T currentState = currentNode.getState();

            // if we found the goal, we're done
            if (goalTest.test(currentState)) {
                return Optional.of(currentNode);
            }

            // check where we can go next and haven't explored
            for (final T child : successors.apply(currentState)) {
                // cost=1 here assumes a simple grid, need a cost function for more sophisticated apps
                final double newCost = currentNode.getCost() + 1;
                if (!explored.containsKey(child) || explored.get(child) > newCost) {
                    explored.put(child, newCost);
                    frontier.offer(Node.astarNode(child, currentNode, newCost, heuristic.applyAsDouble(child)));
                }
            }
        }

        return Optional.empty(); // never found the goal
    }

}
