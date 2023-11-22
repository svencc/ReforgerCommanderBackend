package lib.clipboard.goap.graph;

import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class WeightedPath<NodeType, EdgeType extends WeightedEdge> extends Path<NodeType, EdgeType> {

    private double totalWeight = 0.;

    public WeightedPath(
            @NonNull final List<NodeType> nodeList,
            @NonNull final List<EdgeType> edgeList,
            @NonNull final NodeType startNode,
            @NonNull final NodeType endNode
    ) {
        super(nodeList, edgeList, startNode, endNode);

        for (final EdgeType edge : edgeList) {
            totalWeight += edge.getWeight();
        }
    }

}
