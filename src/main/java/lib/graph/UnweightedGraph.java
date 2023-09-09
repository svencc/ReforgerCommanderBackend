package lib.graph;

import lombok.NonNull;

import java.util.List;

public class UnweightedGraph<VERTEX_TYPE> extends BaseGraph<VERTEX_TYPE, Edge> {

    public UnweightedGraph(@NonNull final List<VERTEX_TYPE> vertices) {
        super(vertices);
    }

    public void addEdge(
            final int from,
            final int to
    ) {
        addEdge(new Edge(from, to));
    }

    public void addEdge(@NonNull final Edge edge) {
        edges.get(edge.from).add(edge);
        edges.get(edge.to).add(edge.reversed());
    }

    public void addEdge(
            @NonNull final VERTEX_TYPE fromVertex,
            @NonNull final VERTEX_TYPE toVertex
    ) {
        addEdge(new Edge(indexOf(fromVertex), indexOf(toVertex)));
    }

}
