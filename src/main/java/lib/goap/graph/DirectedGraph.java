package lib.goap.graph;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

@NoArgsConstructor
public class DirectedGraph<VertexType, EdgeType extends Edge> implements IGraph<VertexType, EdgeType> {

    @NonNull
    protected final HashMap<VertexType, HashMap<VertexType, EdgeType>> graphContent = new HashMap<>();


    @Override
    public void addVertex(@NonNull final VertexType vertex) {
        this.graphContent.put(vertex, new HashMap<>());
    }

    @Override
    public void addEdge(
            @NonNull final VertexType firstVertex,
            @NonNull final VertexType secondVertex,
            @NonNull final EdgeType edge
    ) {
        graphContent.get(firstVertex).put(secondVertex, edge);
    }

    @Override
    public boolean containsEdge(
            @NonNull final VertexType firstVertex,
            @NonNull final VertexType secondVertex
    ) {
        return graphContent.get(firstVertex).containsKey(secondVertex);
    }

    @Override
    public void removeEdge(
            @NonNull final VertexType firstVertex,
            @NonNull final VertexType secondVertex
    ) {
        graphContent.get(firstVertex).remove(secondVertex);
    }


    @NonNull
    @Override
    public HashSet<VertexType> getVertices() {
        return new HashSet<>(this.graphContent.keySet());
    }

    @NonNull
    @Override
    public HashSet<EdgeType> getEdges() {
        return this.graphContent.values()
                .stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Nullable
    @Override
    public EdgeType getEdge(
            @NonNull final VertexType firstVertex,
            @NonNull final VertexType secondVertex
    ) {
        return graphContent.get(firstVertex).getOrDefault(secondVertex, null);
    }

}
