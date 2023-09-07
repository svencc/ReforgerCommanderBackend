package lib.goap.graph;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

@NoArgsConstructor
public class DirectedGraph<NodeType, EdgeType extends Edge> implements IGraph<NodeType, EdgeType> {

    @NonNull
    protected final HashMap<NodeType, HashMap<NodeType, EdgeType>> graphContent = new HashMap<>();


    @Override
    public void addNode(@NonNull final NodeType node) {
        graphContent.put(node, new HashMap<>());
    }

    @Override
    public void addEdge(
            @NonNull final NodeType firstNode,
            @NonNull final NodeType secondNode,
            @NonNull final EdgeType edge
    ) {
        graphContent.get(firstNode).put(secondNode, edge);
    }

    @Override
    public boolean containsEdge(
            @NonNull final NodeType firstNode,
            @NonNull final NodeType secondNode
    ) {
        return graphContent.get(firstNode).containsKey(secondNode);
    }

    @Override
    public void removeEdge(
            @NonNull final NodeType firstNode,
            @NonNull final NodeType secondNode
    ) {
        graphContent.get(firstNode).remove(secondNode);
    }


    @NonNull
    @Override
    public HashSet<NodeType> getNodes() {
        return new HashSet<>(graphContent.keySet());
    }

    @NonNull
    @Override
    public HashSet<EdgeType> getEdges() {
        return graphContent.values()
                .stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Nullable
    @Override
    public EdgeType getEdge(
            @NonNull final NodeType firstNode,
            @NonNull final NodeType secondNode
    ) {
        return graphContent.get(firstNode).getOrDefault(secondNode, null);
    }

}
