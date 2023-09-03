package lib.goap.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@AllArgsConstructor
public class Path<VertexType, EdgeType extends Edge> {

    @NonNull
    private final List<VertexType> vertexList;
    @NonNull
    private final List<EdgeType> edgeList;
    @NonNull
    private final VertexType startVertex;
    @NonNull
    private final VertexType endVertex;

}
