package lib.goap;

import lib.goap.graph.DirectedGraph;
import lib.goap.graph.Edge;
import lib.goap.graph.Path;
import lib.goap.graph.PathFactory;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

    @Test
    public void creation() {
        // Arrange
        final int vertexCount = 5;
        final int edgeCount = 3;

        // Act
        final Optional<Path<Integer, Edge>> pathToTest = Optional.ofNullable(createBasicTestPath(vertexCount, edgeCount));

        // Assert
        assertTrue(pathToTest.isPresent());
        assertNotEquals(null, pathToTest.get());
        assertTrue(pathToTest.get().getNodeList().size() > 0);
        assertTrue(pathToTest.get().getNodeList().size() == (edgeCount + 1));
        assertTrue(pathToTest.get().getEdgeList().size() > 0);
        assertTrue(pathToTest.get().getEdgeList().size() == edgeCount);
        assertEquals(Integer.valueOf(0), pathToTest.get().getStartNode());
        assertEquals(Integer.valueOf(edgeCount), pathToTest.get().getEndNode());
    }

    @Nullable
    public static Path<Integer, Edge> createBasicTestPath(
            final int vertexCount,
            final int edgeCount
    ) {
        final DirectedGraph<Integer, Edge> g = DirectedGraphTest.createBasicConnectedTestGraph(vertexCount, edgeCount);

        // Vertices and edges retrieved with a breadthSearch or DepthSearch
        // would be better / ideal.
        final List<Integer> vertices = new ArrayList<>();
        final List<Edge> edges = new ArrayList<>();

        for (int i = 0; i <= edgeCount; i++) {
            if (i < edgeCount) {
                edges.add(g.getEdge(i, i + 1));
            }
            vertices.add(i);
        }

        return PathFactory.generatePath(g, vertices.get(0), vertices.get(vertices.size() - 1), vertices, edges);
    }
}
