package lib.maze.searchstrategy;

import lib.graph.WeightedEdge;
import lib.graph.WeightedGraph;
import lib.graph.WeightedGraphTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

class JarnikMSTStrategyTest {

    @Test
    void testCityGraph_searchMST() {
        // Arrange
        final WeightedGraph<String> cityGraph = WeightedGraphTest.createUSMapGraph();

        // Act
        final JarnikMSTStrategy jarnikMSTStrategy = new JarnikMSTStrategy();
        final List<WeightedEdge> mst = jarnikMSTStrategy.spanMST(cityGraph, 0);

        if (mst.isEmpty()) {
            fail("failed to find a minimum spanning tree");
        } else {
            System.out.println(cityGraph.stringifyWeightedPath(mst));
        }
    }

}