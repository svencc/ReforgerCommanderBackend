package lib.clipboard.goap.graph;

import lombok.NonNull;

public interface IWeightedGraph<NodeType, EdgeType> extends IGraph<NodeType, EdgeType> {

    /**
     * Function for retrieving the weight of a specific edge inside the
     * IWeightedGraphs implementer.
     *
     * @param edge the edge whose weight is being searched for.
     * @return the weight of the given edge.
     */
    double getEdgeWeight(@NonNull final WeightedEdge edge);

    /**
     * Function for setting an edges weight inside the IWeightedGraphs
     * implementer.
     *
     * @param edge   the edge whose weight is being set.
     * @param weight the weight of the edge.
     */
    void setEdgeWeight(
            @NonNull final WeightedEdge edge,
            double weight
    );

}
