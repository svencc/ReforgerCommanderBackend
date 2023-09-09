package lib.graph;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.IntConsumer;

public class WeightedGraph<VERTEX_TYPE> extends BaseGraph<VERTEX_TYPE, WeightedEdge> {

    public WeightedGraph(@NonNull final List<VERTEX_TYPE> vertices) {
        super(vertices);
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

    @NonNull
    public List<WeightedEdge> mst(final int start) {
        final List<WeightedEdge> result = new ArrayList<>();

        if (start < 0 || start >= getVertexCount() - 1) {
            return result;
        }
        final PriorityQueue<WeightedEdge> priorityQueue = new PriorityQueue<>(); // track edges with the lowest weight
        final boolean[] visitedVertices = new boolean[getVertexCount()]; // track visited vertices

        // add all edges of start vertex to priority queue
        final IntConsumer transmitEdgeToPriorityQueueVisitor = vertexIndex -> {
            visitedVertices[vertexIndex] = true;
            for (final WeightedEdge edge : edgesOf(vertexIndex)) {
                // add all edges of vertex to priority queue
                if (!visitedVertices[edge.to]) {
                    priorityQueue.add(edge);
                }
            }
        };

        // it all starts with the start vertex
        // it prefills the priority queue with all edges of the start vertex
        transmitEdgeToPriorityQueueVisitor.accept(start);

        while (!priorityQueue.isEmpty()) {
            final WeightedEdge edge = priorityQueue.poll();
            if (visitedVertices[edge.to]) {
                continue; // don't revisit vertex again
            }
            // add edge with the lowest weight (due to presorting by priority queue) to the result list
            result.add(edge);
            transmitEdgeToPriorityQueueVisitor.accept(edge.to); // add all edges of new vertex to priority queue
        }

        return result;
    }

    @NonNull
    public String stringifyWeightedPath(@NonNull final List<WeightedEdge> path) {
        final StringBuilder sb = new StringBuilder();
        return sb.append("Total weight: ")
                .append(totalWeight(path))
                .append("\n")
                .append(String.join("\n", path.stream()
                        .map(we -> String.format("%s --(%s)--> %s", vertexAt(we.from), we.weight, vertexAt(we.to)))
                        .toList())
                )
                .append("\n")
                .toString();
    }

    public static double totalWeight(@NonNull final List<WeightedEdge> path) {
        return path.stream()
                .mapToDouble(e -> e.weight)
                .sum();
    }

}
