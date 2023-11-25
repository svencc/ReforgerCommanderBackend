package com.recom.maze.searchstrategy;

import com.recom.maze.Node;
import lombok.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Depth First Search
 */
public class DSFStrategy implements GoalSearchableStrategy {

    @NonNull
    public <T> Optional<Node<T>> search(
            @NonNull final T initialState,
            @NonNull final Predicate<T> goalTest,
            @NonNull final Function<T, List<T>> successors
    ) {
        // frontier is where we've yet to go
        final Stack<Node<T>> frontier = new Stack<>();
        frontier.push(Node.dfsStartNode(initialState));

        // explored is where we've already been
        final Set<T> explored = new HashSet<>();
        explored.add(initialState);

        // keep going while there is more to explore
        while (!frontier.isEmpty()) {
            final Node<T> currentNode = frontier.pop();
            final T currentState = currentNode.getState();

            // if we found the goal, we're done
            if (goalTest.test(currentState)) {
                return Optional.of(currentNode);
            }

            // check where we can go next and haven't explored
            for (final T child : successors.apply(currentState)) {
                if (explored.contains(child)) {
                    continue; // skip children we already explored
                }
                explored.add(child);
                frontier.push(Node.dfsNode(child, currentNode));
            }
        }

        return Optional.empty(); // never found the goal
    }

}
