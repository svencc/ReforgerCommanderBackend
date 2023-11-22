package lib.clipboard.graph;

import lib.clipboard.maze.Node;
import lib.clipboard.maze.searchstrategy.BSFStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class UnweightedGraphTest {

    @Test
    void testCityGraph_toStringMethod() {
        // Arrange (15 biggest cities in USA)
        final UnweightedGraph<String> cityGraph = createUSMapGraph();
        final String expectedGraphString = """
                Seattle -> [Chicago, San Francisco]
                San Francisco -> [Seattle, Riverside, Los Angeles]
                Los Angeles -> [San Francisco, Riverside, Phoenix]
                Riverside -> [San Francisco, Los Angeles, Phoenix, Chicago]
                Phoenix -> [Los Angeles, Riverside, Dallas, Houston]
                Chicago -> [Seattle, Riverside, Dallas, Atlanta, Detroit]
                Boston -> [Detroit, New York]
                New York -> [Detroit, Boston, Philadelphia]
                Atlanta -> [Dallas, Houston, Chicago, Washington, Miami]
                Miami -> [Houston, Atlanta, Washington]
                Dallas -> [Phoenix, Chicago, Atlanta, Houston]
                Houston -> [Phoenix, Dallas, Atlanta, Miami]
                Detroit -> [Chicago, Boston, Washington, New York]
                Philadelphia -> [New York, Washington]
                Washington -> [Atlanta, Miami, Detroit, Philadelphia]
                """.stripIndent();

        // Act & Assert
        assertEquals(15, cityGraph.getVertexCount());
        assertEquals(52, cityGraph.getEdgeCount());
        assertEquals(expectedGraphString, cityGraph.toString());
        System.out.println(cityGraph);
    }

    private UnweightedGraph<String> createUSMapGraph() {
        final UnweightedGraph<String> cityGraph = new UnweightedGraph<>(
                List.of(
                        "Seattle",
                        "San Francisco",
                        "Los Angeles",
                        "Riverside",
                        "Phoenix",
                        "Chicago",
                        "Boston",
                        "New York",
                        "Atlanta",
                        "Miami",
                        "Dallas",
                        "Houston",
                        "Detroit",
                        "Philadelphia",
                        "Washington"
                )
        );

        cityGraph.addEdge("Seattle", "Chicago");
        cityGraph.addEdge("Seattle", "San Francisco");
        cityGraph.addEdge("San Francisco", "Riverside");
        cityGraph.addEdge("San Francisco", "Los Angeles");
        cityGraph.addEdge("Los Angeles", "Riverside");
        cityGraph.addEdge("Los Angeles", "Phoenix");
        cityGraph.addEdge("Riverside", "Phoenix");
        cityGraph.addEdge("Riverside", "Chicago");
        cityGraph.addEdge("Phoenix", "Dallas");
        cityGraph.addEdge("Phoenix", "Houston");
        cityGraph.addEdge("Dallas", "Chicago");
        cityGraph.addEdge("Dallas", "Atlanta");
        cityGraph.addEdge("Dallas", "Houston");
        cityGraph.addEdge("Houston", "Atlanta");
        cityGraph.addEdge("Houston", "Miami");
        cityGraph.addEdge("Atlanta", "Chicago");
        cityGraph.addEdge("Atlanta", "Washington");
        cityGraph.addEdge("Atlanta", "Miami");
        cityGraph.addEdge("Miami", "Washington");
        cityGraph.addEdge("Chicago", "Detroit");
        cityGraph.addEdge("Detroit", "Boston");
        cityGraph.addEdge("Detroit", "Washington");
        cityGraph.addEdge("Detroit", "New York");
        cityGraph.addEdge("Boston", "New York");
        cityGraph.addEdge("New York", "Philadelphia");
        cityGraph.addEdge("Philadelphia", "Washington");

        return cityGraph;
    }

    @Test
    void testCityGraph_withBSFSearchAlgorithm() {
        // Arrange
        final UnweightedGraph<String> cityGraph = createUSMapGraph();
        final BSFStrategy bsfSearch = new BSFStrategy();

        // Act
        final Optional<Node<String>> path = bsfSearch.search("Boston", (v) -> v.equals("Miami"), cityGraph::neighborsOf);

        if (path.isEmpty()) {
            fail("No path from Boston to Miami");
        } else {
            System.out.println("Route from Boston to Miami:");
            final List<String> nodeList = Node.nodeToPath(path.get());
            System.out.println(nodeList);
        }
    }

}