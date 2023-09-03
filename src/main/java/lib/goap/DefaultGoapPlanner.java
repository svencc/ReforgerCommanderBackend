package lib.goap;


import lib.goap.graph.DirectedWeightedGraph;
import lib.goap.graph.IWeightedGraph;
import lib.goap.graph.WeightedEdge;

/**
 * DefaultGoapPlanner.java --- The default implementation of the GoapPlanner.
 * 
 * @author P H - 15.03.2017
 *
 */
public class DefaultGoapPlanner extends GoapPlanner {

	@Override
	protected <EdgeType extends WeightedEdge> IWeightedGraph<GraphNode, EdgeType> generateGraphObject() {
		return new DirectedWeightedGraph<GraphNode, EdgeType>();
	}

}
