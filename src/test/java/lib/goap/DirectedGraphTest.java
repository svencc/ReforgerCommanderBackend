package lib.goap;


import lib.goap.graph.DirectedGraph;
import lib.goap.graph.Edge;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DirectedGraphTest {

    @Test
    public void vertices() {
        // Arrange
        final int vertexCount = 5;

        // Act
        final DirectedGraph<Integer, Edge> g = createBasicTestGraph(vertexCount);

        // Assert
        assertNotEquals(null, g);
        assertNotEquals(null, g.getNodes());
        assertFalse(g.getNodes().isEmpty());
        assertEquals(vertexCount, g.getNodes().size());
    }

    @NonNull
    public static DirectedGraph<Integer, Edge> createBasicTestGraph(final int vertexCount) {
        final DirectedGraph<Integer, Edge> directedGraph = new DirectedGraph<>();

        for (int i = 0; i < vertexCount; i++) {
            directedGraph.addNode(i);
        }

        return directedGraph;
    }

    @Test
    public void edges() {
        // Arrange
        final int vertexCount = 5;
        final int edgeCount = 2;

        // Act
        final DirectedGraph<Integer, Edge> graphToTest = createBasicConnectedTestGraph(vertexCount, edgeCount);

        // Assert
        assertTrue(graphToTest.containsEdge(0, 1));
        assertFalse(graphToTest.containsEdge(3, 4));
        assertTrue(graphToTest.getEdges().size() == edgeCount);

        graphToTest.removeEdge(0, 1);

        assertFalse(graphToTest.containsEdge(0, 1));
        assertFalse(graphToTest.getEdges().isEmpty());
        assertTrue(graphToTest.getEdges().size() == (edgeCount - 1));
    }

    @NonNull
    public static DirectedGraph<Integer, Edge> createBasicConnectedTestGraph(
            final int vertexCount,
            final int edgeCount
    ) {
        final DirectedGraph<Integer, Edge> basicTestGraph = createBasicTestGraph(vertexCount);

        for (int i = 0; i < edgeCount; i++) {
            basicTestGraph.addEdge(i, i + 1, new Edge());
        }

        return basicTestGraph;
    }

}
