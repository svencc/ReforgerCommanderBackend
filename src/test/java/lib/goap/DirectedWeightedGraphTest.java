package lib.goap;


import lib.goap.graph.DirectedWeightedGraph;
import lib.goap.graph.WeightedEdge;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DirectedWeightedGraphTest {

    @Test
    public void weights() {
        // Arrange
        final int vertexCount = 5;
        final int edgeCount = 2;

        // Act
        final DirectedWeightedGraph<Integer, WeightedEdge> g = createBasicConnectedTestWeightedGraph(vertexCount, edgeCount);

        // Assert
        assertTrue(g.getEdgeWeight(g.getEdge(0, 1)) == 0);
    }

    @NonNull
    public static DirectedWeightedGraph<Integer, WeightedEdge> createBasicConnectedTestWeightedGraph(
            final int vertexCount,
            final int edgeCount
    ) {
        final DirectedWeightedGraph<Integer, WeightedEdge> g = createBasicTestWeightedGraph(vertexCount);

        for (int i = 0; i < edgeCount; i++) {
            g.addEdge(i, i + 1, new WeightedEdge());
        }

        return g;
    }

    @NonNull
    public static DirectedWeightedGraph<Integer, WeightedEdge> createBasicTestWeightedGraph(final int vertexCount) {
        final DirectedWeightedGraph<Integer, WeightedEdge> g = new DirectedWeightedGraph<Integer, WeightedEdge>();

        for (int i = 0; i < vertexCount; i++) {
            g.addVertex(i);
        }

        return g;
    }
}
