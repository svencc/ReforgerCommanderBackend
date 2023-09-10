package lib.graph;

import lib.maze.searchstrategy.JarnikMSTStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class WeightedGraphTest {

    @Test
    void testCityGraph_toStringMethod() {
        Locale.setDefault(Locale.US);

        // Arrange (15 biggest cities in USA)
        final WeightedGraph<String> cityGraph = createUSMapGraph();
        final String expectedGraphString = """
                Seattle -> [(Chicago, 1737.00), (San Francisco, 678.00)]
                San Francisco -> [(Seattle, 678.00), (Riverside, 386.00), (Los Angeles, 348.00)]
                Los Angeles -> [(San Francisco, 348.00), (Riverside, 50.00), (Phoenix, 357.00)]
                Riverside -> [(San Francisco, 386.00), (Los Angeles, 50.00), (Phoenix, 307.00), (Chicago, 1704.00)]
                Phoenix -> [(Los Angeles, 357.00), (Riverside, 307.00), (Dallas, 887.00), (Houston, 1015.00)]
                Chicago -> [(Seattle, 1737.00), (Riverside, 1704.00), (Dallas, 805.00), (Atlanta, 588.00), (Detroit, 238.00)]
                Boston -> [(Detroit, 613.00), (New York, 190.00)]
                New York -> [(Detroit, 482.00), (Boston, 190.00), (Philadelphia, 81.00)]
                Atlanta -> [(Dallas, 721.00), (Houston, 702.00), (Chicago, 588.00), (Washington, 543.00), (Miami, 604.00)]
                Miami -> [(Houston, 968.00), (Atlanta, 604.00), (Washington, 923.00)]
                Dallas -> [(Phoenix, 887.00), (Chicago, 805.00), (Atlanta, 721.00), (Houston, 225.00)]
                Houston -> [(Phoenix, 1015.00), (Dallas, 225.00), (Atlanta, 702.00), (Miami, 968.00)]
                Detroit -> [(Chicago, 238.00), (Boston, 613.00), (Washington, 396.00), (New York, 482.00)]
                Philadelphia -> [(New York, 81.00), (Washington, 123.00)]
                Washington -> [(Atlanta, 543.00), (Miami, 923.00), (Detroit, 396.00), (Philadelphia, 123.00)]
                """.stripIndent();

        // Act & Assert
        assertEquals(15, cityGraph.getVertexCount());
        assertEquals(52, cityGraph.getEdgeCount());
        assertEquals(expectedGraphString, cityGraph.toString());

        System.out.println(cityGraph);
    }

    public static WeightedGraph<String> createUSMapGraph() {
        final WeightedGraph<String> cityGraph = new WeightedGraph<>(
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

        cityGraph.addEdge("Seattle", "Chicago", 1737);
        cityGraph.addEdge("Seattle", "San Francisco", 678);
        cityGraph.addEdge("San Francisco", "Riverside", 386);
        cityGraph.addEdge("San Francisco", "Los Angeles", 348);
        cityGraph.addEdge("Los Angeles", "Riverside", 50);
        cityGraph.addEdge("Los Angeles", "Phoenix", 357);
        cityGraph.addEdge("Riverside", "Phoenix", 307);
        cityGraph.addEdge("Riverside", "Chicago", 1704);
        cityGraph.addEdge("Phoenix", "Dallas", 887);
        cityGraph.addEdge("Phoenix", "Houston", 1015);
        cityGraph.addEdge("Dallas", "Chicago", 805);
        cityGraph.addEdge("Dallas", "Atlanta", 721);
        cityGraph.addEdge("Dallas", "Houston", 225);
        cityGraph.addEdge("Houston", "Atlanta", 702);
        cityGraph.addEdge("Houston", "Miami", 968);
        cityGraph.addEdge("Atlanta", "Chicago", 588);
        cityGraph.addEdge("Atlanta", "Washington", 543);
        cityGraph.addEdge("Atlanta", "Miami", 604);
        cityGraph.addEdge("Miami", "Washington", 923);
        cityGraph.addEdge("Chicago", "Detroit", 238);
        cityGraph.addEdge("Detroit", "Boston", 613);
        cityGraph.addEdge("Detroit", "Washington", 396);
        cityGraph.addEdge("Detroit", "New York", 482);
        cityGraph.addEdge("Boston", "New York", 190);
        cityGraph.addEdge("New York", "Philadelphia", 81);
        cityGraph.addEdge("Philadelphia", "Washington", 123);

        return cityGraph;
    }

}