package com.recom.maze.searchstrategy;

import com.recom.maze.Node;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface GoalSearchableStrategy {

    @NonNull
    <T> Optional<Node<T>> search(
            @NonNull final T initialState,
            @NonNull final Predicate<T> goalTest,
            @NonNull final Function<T, List<T>> successors
    );

}
