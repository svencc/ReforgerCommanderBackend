package lib.maze;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class Node<T> implements Comparable<Node<T>> {

    @NonNull
    private final T state; // DFS only
    @NonNull
    private final Optional<Node<T>> parent; // DFS only
    private final double cost; // used in A* not in DFS
    private final double heuristic;// used in A* not in DFS

    // DFS Node
    private Node(@NonNull final T state, @Nullable final Node<T> parent) {
        this.state = state;
        this.parent = Optional.ofNullable(parent);
        this.cost = 0;
        this.heuristic = 0;
    }

    // A* Node
    private Node(@NonNull final T state, @Nullable final Node<T> parent, final double cost, final double heuristic) {
        this.state = state;
        this.parent = Optional.ofNullable(parent);
        this.cost = cost;
        this.heuristic = heuristic;
    }

    public static <T> Node<T> astarStartNode(
            @NonNull final T state,
            final double heuristic
    ) {
        return new Node<>(state, null, 0, heuristic);
    }

    public static <T> Node<T> astarNode(
            @NonNull final T state,
            @Nullable final Node<T> parent,
            final double cost,
            final double heuristic
    ) {
        return new Node<>(state, parent, cost, heuristic);
    }

    public static <T> Node<T> dfsStartNode(@NonNull final T state) {
        return new Node<>(state, null);
    }

    public static <T> Node<T> dfsNode(
            @NonNull final T state,
            @NonNull final Node<T> parent
    ) {
        return new Node<>(state, parent);
    }

    @NonNull
    public static <T> List<T> nodeToPath(@NonNull final Node<T> node) {
        final List<T> path = new ArrayList<>();
        path.add(node.getState());

        // work backwards from end to front
        Node<T> currentNode = node;
        while (currentNode.getParent().isPresent()) {
            currentNode = currentNode.getParent().get();
            path.add(0, currentNode.getState());
        }

        return path;
    }

    @Override
    public int compareTo(@NonNull final Node<T> other) {
        final Double mine = cost + heuristic;
        final Double theirs = other.cost + other.heuristic;
        return mine.compareTo(theirs);
    }

}
