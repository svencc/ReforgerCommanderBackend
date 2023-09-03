package lib.goap.graph;

import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class WeightedPath<VertexType, EdgeType extends WeightedEdge> extends Path<VertexType, EdgeType> {

    private double totalWeight = 0.;

    public WeightedPath(
            @NonNull final List<VertexType> vertexList,
            @NonNull final List<EdgeType> edgeList,
            @NonNull final VertexType startVertex,
            @NonNull final VertexType endVertex
    ) {
        super(vertexList, edgeList, startVertex, endVertex);

        for (final EdgeType edge : edgeList) {
            totalWeight += edge.getWeight();
        }
    }

}
