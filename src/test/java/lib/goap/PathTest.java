package lib.goap;

import lib.goap.graph.DirectedGraph;
import lib.goap.graph.Edge;
import lib.goap.graph.Path;
import lib.goap.graph.PathFactory;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

    @Test
    public void creation() {
        // Arrange
        final int vertexCount = 5;
        final int edgeCount = 3;

        // Act
        final Path<Integer, Edge> p = createBasicTestPath(vertexCount, edgeCount);

        // Assert
        assertNotEquals(null, p);
        assertTrue(p.getVertexList().size() > 0);
        assertTrue(p.getVertexList().size() == (edgeCount + 1));
        assertTrue(p.getEdgeList().size() > 0);
        assertTrue(p.getEdgeList().size() == edgeCount);
        assertEquals(Integer.valueOf(0), p.getStartVertex());
        assertEquals(Integer.valueOf(edgeCount), p.getEndVertex());
    }

    @Nullable
    public static Path<Integer, Edge> createBasicTestPath(
            final int vertexCount,
            final int edgeCount
    ) {
        final DirectedGraph<Integer, Edge> g = DirectedGraphTest.createBasicConnectedTestGraph(vertexCount, edgeCount);

        // Vertices and edges retrieved with a breadthSearch or DepthSearch
        // would be better / ideal.
        final List<Integer> vertices = new ArrayList<Integer>();
        final List<Edge> edges = new ArrayList<Edge>();

        for (int i = 0; i <= edgeCount; i++) {
            if (i < edgeCount) {
                edges.add(g.getEdge(i, i + 1));
            }
            vertices.add(i);
        }

        return PathFactory.generatePath(g, vertices.get(0), vertices.get(vertices.size() - 1), vertices, edges);
    }
}
