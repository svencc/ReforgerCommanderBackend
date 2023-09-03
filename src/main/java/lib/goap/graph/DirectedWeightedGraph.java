package lib.goap.graph;

import lombok.NonNull;

public class DirectedWeightedGraph<VertexType, EdgeType extends WeightedEdge>
        extends DirectedGraph<VertexType, EdgeType>
        implements IWeightedGraph<VertexType, EdgeType>
{

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
