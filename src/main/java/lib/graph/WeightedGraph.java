package lib.graph;

import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public class WeightedGraph<VERTEX_TYPE> extends BaseGraph<VERTEX_TYPE, WeightedEdge> {

    public WeightedGraph(@NonNull final List<VERTEX_TYPE> vertices) {
        super(vertices);
    }

    public static double totalWeight(@NonNull final List<WeightedEdge> path) {
        return path.stream()
                .mapToDouble(e -> e.weight)
                .sum();
    }

    public void addEdge(
            final int from,
            final int to,
            final double weight
    ) {
        addEdge(new WeightedEdge(from, to, weight));
    }

    public void addEdge(@NonNull final WeightedEdge edge) {
        edges.get(edge.from).add(edge);
        edges.get(edge.to).add(edge.reversed());
    }

    public void addEdge(
            @NonNull final VERTEX_TYPE fromVertex,
            @NonNull final VERTEX_TYPE toVertex,
            final double weight
    ) {
        addEdge(new WeightedEdge(indexOf(fromVertex), indexOf(toVertex), weight));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getVertexCount(); i++) {
            sb.append(vertexAt(i))
                    .append(" -> ")
                    .append(Arrays.toString(
                            edgesOf(i).stream()
                                    .map(we -> String.format("(%s, %.2f)", vertexAt(we.to), we.weight))
                                    .toArray()
                    ))
                    .append("\n");
        }
        return sb.toString();
    }

}
