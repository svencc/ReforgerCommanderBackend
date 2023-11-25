package com.recom.maze.searchstrategy;

import com.recom.maze.graph.WeightedEdge;
import com.recom.maze.graph.WeightedGraph;
import com.recom.maze.graph.WeightedGraphTest;
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