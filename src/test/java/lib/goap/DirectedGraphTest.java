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
		assertNotEquals(null, g.getVertices());
		assertFalse(g.getVertices().isEmpty());
		assertEquals(vertexCount, g.getVertices().size());
	}

	@Test
	public void edges() {
		// Arrange
		final int vertexCount = 5;
		final int edgeCount = 2;

		// Act
		final DirectedGraph<Integer, Edge> g = createBasicConnectedTestGraph(vertexCount, edgeCount);

		// Assert
		assertTrue(g.containsEdge(0, 1));
		assertFalse(g.containsEdge(3, 4));
		assertTrue(g.getEdges().size() == edgeCount);

		g.removeEdge(0, 1);

		assertFalse(g.containsEdge(0, 1));
		assertFalse(g.getEdges().isEmpty());
		assertTrue(g.getEdges().size() == (edgeCount - 1));
	}

	@NonNull
	public static DirectedGraph<Integer, Edge> createBasicTestGraph(final int vertexCount) {
		final DirectedGraph<Integer, Edge> g = new DirectedGraph<Integer, Edge>();

		for (int i = 0; i < vertexCount; i++) {
			g.addVertex(i);
		}

		return g;
	}

	@NonNull
	public static DirectedGraph<Integer, Edge> createBasicConnectedTestGraph(
			final int vertexCount,
			final int edgeCount
	) {
		final DirectedGraph<Integer, Edge> g = createBasicTestGraph(vertexCount);

		for (int i = 0; i < edgeCount; i++) {
			g.addEdge(i, i + 1, new Edge());
		}
		return g;
	}

}
