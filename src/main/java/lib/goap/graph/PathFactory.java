package lib.goap.graph;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public class PathFactory {

    /**
     * Function for generating a simple Path. The given information are being
     * checked against the given Graph.
     *
     * @param graph      the Graph the information are being checked against.
     * @param start      the starting node of the Path.
     * @param end        the end node of the Path.
     * @param nodeList   the List of all nodes of the Path. These elements also get
     *                   checked to secure their conformity with the given Graph.
     * @param edgeList   the List of all edges of the Path. These elements also get
     *                   checked to secure their conformity with the given Graph.
     * @param <NodeType> the type of node being used inside the Path.
     * @param <EdgeType> the type of Edge being used to connect all nodes inside the
     *                   Path.
     * @return a Path leading from one point inside the Graph to another one.
     */
    @Nullable
    public static <NodeType, EdgeType extends Edge> Path<NodeType, EdgeType> generatePath(
            @NonNull final IGraph<NodeType, EdgeType> graph,
            @NonNull final NodeType start,
            @NonNull final NodeType end,
            @NonNull final List<NodeType> nodeList,
            @NonNull final List<EdgeType> edgeList
    ) {
        if (validateStartAndEnd(start, end, nodeList) && validateConnections(graph, nodeList)) {
            return new Path<>(nodeList, edgeList, start, end);
        } else {
            return null;//@TODO -> Exception
        }
    }

    /**
     * Function for validating the given start and end nodes.
     *
     * @param start      the provided start node.
     * @param end        the provided end node.
     * @param nodeList   the List of all nodes.
     * @param <NodeType> the type of node being used inside the Graph / Path.
     * @return true or false depending on if the provided nodes are indeed the
     * start and the end nodes.
     */
    protected static <NodeType> boolean validateStartAndEnd(
            @NonNull final NodeType start,
            @NonNull final NodeType end,
            @NonNull final List<NodeType> nodeList
    ) {
        return nodeList.contains(start)
                && nodeList.contains(end)
                && nodeList.get(0).equals(start)
                && nodeList.get(nodeList.size() - 1).equals(end);
    }

    /**
     * Function for validating all nodes and edges of the given Lists.
     *
     * @param graph      the graph the information is being checked against.
     * @param nodeList   the List of all nodes of the Path being created.
     * @param <NodeType> the type of nodes being used inside the Graph / Path.
     * @param <EdgeType> the type of Edge being used to connect all nodes inside the
     *                   Graph / Path.
     * @return true or false depending on if the provided Lists match the given
     * Graph.
     */
    protected static <NodeType, EdgeType extends Edge> boolean validateConnections(
            @NonNull final IGraph<NodeType, EdgeType> graph,
            @NonNull final List<NodeType> nodeList
    ) {
        boolean success = true;
        NodeType previousNode = null;

        for (final NodeType node : nodeList) {
            if (previousNode != null && !graph.containsEdge(previousNode, node)) {
                success = false;
                break;
            }

            previousNode = node;
        }

        return success;
    }

    /**
     * Function for generating a WeightedPath. The given information are being
     * checked against the given Graph.
     *
     * @param graph      the Graph the information are being checked against.
     * @param start      the starting node of the WeightedPath.
     * @param end        the end node of the WeightedPath.
     * @param nodeList   the List of all nodes of the WeightedPath. These elements
     *                   also get checked to secure their conformity with the given
     *                   Graph.
     * @param edgeList   the List of all edges of the WeightedPath. These elements also
     *                   get checked to secure their conformity with the given Graph.
     * @param <NodeType> the type of node being used inside the DirectedWeightedPath.
     * @param <EdgeType> the type of Edge being used to connect all nodes inside the
     *                   DirectedWeightedPath.
     * @return a WeightedPath leading from one point inside the Graph to another
     * one.
     */
    @Nullable
    public static <NodeType, EdgeType extends WeightedEdge> WeightedPath<NodeType, EdgeType> generateWeightedPath(
            @NonNull final IGraph<NodeType, EdgeType> graph,
            @NonNull final NodeType start,
            @NonNull final NodeType end,
            @NonNull final List<NodeType> nodeList,
            @NonNull final List<EdgeType> edgeList
    ) {
        if (validateStartAndEnd(start, end, nodeList) && validateConnections(graph, nodeList)) {
            return new WeightedPath<>(nodeList, edgeList, start, end);
        } else {
            return null; //@TODO -> Exception
        }
    }

}
