package lib.goap.graph;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashSet;

public interface IGraph<NodeType, EdgeType> {

    /**
     * Function for adding a node to the Graph.
     *
     * @param node the node being added.
     */
    void addNode(@NonNull final NodeType node);

    /**
     * Function for adding an edge to the Graph.
     *
     * @param firstNode  the node from which the edge is coming from.
     * @param secondNode the node the edge is going to.
     * @param edge       the edge itself that is going to be added.
     */
    void addEdge(
            @NonNull final NodeType firstNode,
            @NonNull final NodeType secondNode,
            @NonNull final EdgeType edge
    );

    /**
     * Function to testing if an edge exists inside a Graph.
     *
     * @param firstNode  the node from which the edge is coming from.
     * @param secondNode the node the edge is going to.
     * @return true or false depending if the edge exists.
     */
    boolean containsEdge(
            @NonNull final NodeType firstNode,
            @NonNull final NodeType secondNode
    );

    /**
     * Function for removing an edge from a Graph.
     *
     * @param firstNode  the node from which the edge is coming from.
     * @param secondNode the node the edge is going to.
     */
    void removeEdge(
            @NonNull final NodeType firstNode,
            @NonNull final NodeType secondNode
    );

    /**
     * Function for retrieving all nodes inside the Graph.
     *
     * @return all nodes inside the Graph.
     */
    @NonNull
    HashSet<NodeType> getNodes();

    /**
     * Function for retrieving all edges inside the Graph.
     *
     * @return all edges inside the Graph.
     */
    @NonNull
    HashSet<EdgeType> getEdges();

    /**
     * Function for retrieving a specific edge in the Graph.
     *
     * @param firstNode  the node from which the edge is coming from.
     * @param secondNode the node the edge is going to.
     * @return the desired edge or null, if none is found.
     */
    @Nullable
    EdgeType getEdge(
            @NonNull final NodeType firstNode,
            @NonNull final NodeType secondNode
    );

}
