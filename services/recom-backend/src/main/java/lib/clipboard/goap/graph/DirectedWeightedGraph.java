package lib.clipboard.goap.graph;

import lombok.NonNull;

public class DirectedWeightedGraph<NodeType, EdgeType extends WeightedEdge>
        extends DirectedGraph<NodeType, EdgeType>
        implements IWeightedGraph<NodeType, EdgeType> {

    @Override
    public double getEdgeWeight(@NonNull final WeightedEdge edge) {
        return edge.getWeight();
    }

    @Override
    public void setEdgeWeight(
            @NonNull final WeightedEdge edge,
            double weight
    ) {
        edge.setWeight(weight);
    }

}
