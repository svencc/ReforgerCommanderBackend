package lib.goap.planner;


import lib.goap.graph.DirectedWeightedGraph;
import lib.goap.graph.IWeightedGraph;
import lib.goap.graph.WeightedEdge;
import lib.goap.graph.GraphNode;

public class GoapPlanner extends GoapPlannerBase {

    @Override
    protected <EdgeType extends WeightedEdge> IWeightedGraph<GraphNode, EdgeType> generateGraphObject() {
        return new DirectedWeightedGraph<>();
    }

}
