package lib.clipboard.maze.searchstrategy;

import lib.clipboard.graph.WeightedEdge;
import lib.clipboard.graph.WeightedGraph;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.IntConsumer;

/**
 * A* Search
 */
public class JarnikMSTStrategy {

    @NonNull
    public <EDGE_TYPE> List<WeightedEdge> spanMST(
            @NonNull final WeightedGraph<EDGE_TYPE> graph,
            final int start
    ) {
        final List<WeightedEdge> result = new ArrayList<>();

        if (start < 0 || start >= graph.getVertexCount() - 1) {
            return result;
        }
        final PriorityQueue<WeightedEdge> priorityQueue = new PriorityQueue<>(); // track edges with the lowest weight
        final boolean[] visitedVertices = new boolean[graph.getVertexCount()]; // track visited vertices

        // add all edges of start vertex to priority queue
        final IntConsumer transmitEdgeToPriorityQueueVisitor = vertexIndex -> {
            visitedVertices[vertexIndex] = true;
            for (final WeightedEdge edge : graph.edgesOf(vertexIndex)) {
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

}
