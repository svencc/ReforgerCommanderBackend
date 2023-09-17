package lib.clipboard.goap.planner;


import lib.clipboard.goap.graph.DirectedWeightedGraph;
import lib.clipboard.goap.graph.IWeightedGraph;
import lib.clipboard.goap.graph.WeightedEdge;
import lib.clipboard.goap.graph.Node;

public class GoapPlanner extends GoapPlannerBase {

    @Override
    protected <EdgeType extends WeightedEdge> IWeightedGraph<Node, EdgeType> generateGraphObject() {
        return new DirectedWeightedGraph<>();
    }

}
