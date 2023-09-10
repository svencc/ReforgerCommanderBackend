package lib.maze.searchstrategy;

import lib.graph.DijkstraResult;
import lib.graph.WeightedGraph;
import lib.graph.WeightedGraphTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DijkstraStrategyTest {

    @BeforeAll
    static void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    void testCityGraph_searchMST() {
        // Arrange
        final WeightedGraph<String> cityGraph = WeightedGraphTest.createUSMapGraph();

        // Act
        final DijkstraStrategy<String> dijkstraStrategy = new DijkstraStrategy<>();
        final DijkstraResult dijkstraResult = dijkstraStrategy.dijkstra(cityGraph, "Los Angeles");
        final Map<String, Double> nameDistance = dijkstraStrategy.distanceArrayToDistanceMap(cityGraph, dijkstraResult.getDistance());

        // Assert
        assertEquals(15, nameDistance.size());
        assertEquals(2474.0, nameDistance.get("New York"));
        assertEquals(1992.0, nameDistance.get("Detroit"));
        assertEquals(1026.0, nameDistance.get("Seattle"));
        assertEquals(1754.0, nameDistance.get("Chicago"));
        assertEquals(2388.0, nameDistance.get("Washington"));
        assertEquals(2340.0, nameDistance.get("Miami"));
        assertEquals(348.0, nameDistance.get("San Francisco"));
        assertEquals(1965.0, nameDistance.get("Atlanta"));
        assertEquals(357.0, nameDistance.get("Phoenix"));
        assertEquals(0.0, nameDistance.get("Los Angeles"));
        assertEquals(1244.0, nameDistance.get("Dallas"));
        assertEquals(2511.0, nameDistance.get("Philadelphia"));
        assertEquals(50.0, nameDistance.get("Riverside"));
        assertEquals(2605.0, nameDistance.get("Boston"));
        assertEquals(1372.0, nameDistance.get("Houston"));

        System.out.println("\n\nDistance from Los Angeles:");
        nameDistance.forEach((name, distance) -> System.out.printf("%s: %.2f\n", name, distance));


        System.out.println("\n\nShortest path from Los Angeles to Boston:");
        final String stringifyWeightedPath = cityGraph.stringifyWeightedPath(dijkstraStrategy.pathToPathMap(
                cityGraph.indexOf("Los Angeles"),
                cityGraph.indexOf("Boston"),
                dijkstraResult.getPathMap()
        ));
        System.out.println(stringifyWeightedPath);

        final String expectedShortestPath = """
                Total weight: 2605.0
                Los Angeles --(50.0)--> Riverside
                Riverside --(1704.0)--> Chicago
                Chicago --(238.0)--> Detroit
                Detroit --(613.0)--> Boston
                """.stripIndent();
        assertEquals(expectedShortestPath, stringifyWeightedPath);
    }

}