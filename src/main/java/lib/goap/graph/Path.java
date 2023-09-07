package lib.goap.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@AllArgsConstructor
public class Path<NodeType, EdgeType extends Edge> {

    @NonNull
    private final List<NodeType> nodeList;
    @NonNull
    private final List<EdgeType> edgeList;
    @NonNull
    private final NodeType startNode;
    @NonNull
    private final NodeType endNode;

}
