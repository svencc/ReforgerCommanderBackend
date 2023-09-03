package lib.goap.graph;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashSet;

public interface IGraph<VertexType, EdgeType> {

    /**
     * Function for adding a vertex to the Graph.
     *
     * @param vertex the vertex being added.
     */
    void addVertex(@NonNull final VertexType vertex);

    /**
     * Function for adding an edge to the Graph.
     *
     * @param firstVertex  the vertex from which the edge is coming from.
     * @param secondVertex the vertex the edge is going to.
     * @param edge         the edge itself that is going to be added.
     */
    void addEdge(
            @NonNull final VertexType firstVertex,
            @NonNull final VertexType secondVertex,
            @NonNull final EdgeType edge
    );

    /**
     * Function to testing if an edge exists inside a Graph.
     *
     * @param firstVertex  the vertex from which the edge is coming from.
     * @param secondVertex the vertex the edge is going to.
     * @return true or false depending if the edge exists.
     */
    boolean containsEdge(
            @NonNull final VertexType firstVertex,
            @NonNull final VertexType secondVertex
    );

    /**
     * Function for removing an edge from a Graph.
     *
     * @param firstVertex  the vertex from which the edge is coming from.
     * @param secondVertex the vertex the edge is going to.
     */
    void removeEdge(
            @NonNull final VertexType firstVertex,
            @NonNull final VertexType secondVertex
    );

    /**
     * Function for retrieving all vertices inside the Graph.
     *
     * @return all vertices inside the Graph.
     */
    @NonNull
    HashSet<VertexType> getVertices();

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
     * @param firstVertex  the vertex from which the edge is coming from.
     * @param secondVertex the vertex the edge is going to.
     * @return the desired edge or null, if none is found.
     */
    @Nullable
    EdgeType getEdge(
            @NonNull final VertexType firstVertex,
            @NonNull final VertexType secondVertex
    );

}
