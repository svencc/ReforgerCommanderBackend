package lib.goap.planner;

import lib.goap.GoapAction;
import lib.goap.GoapState;
import lib.goap.graph.*;
import lib.goap.unit.IGoapUnit;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;

@Getter
public abstract class GoapPlannerBase implements GoapPlannerable {

    @Nullable
    private IGoapUnit goapUnit;
    @Nullable
    private GraphNode startNode;
    @Nullable
    private List<GraphNode> endNodes;


    /**
     * Function for testing if all preconditions in a given HashSet are also in
     * another HashSet (effects) with the same values.
     *
     * @param preconditions HashSet of states which are present.
     * @param effects       HashSet of states which are required.
     * @return true or false depending on if all preconditions are met with the
     * given effects.
     */
    protected static boolean areAllPreconditionsMet(
            @NonNull final HashSet<GoapState> preconditions,
            @NonNull final HashSet<GoapState> effects
    ) {
        boolean preconditionsMet = true;

        for (GoapState precondition : preconditions) {
            boolean currentPreconditionMet = false;

            for (GoapState effect : effects) {
                if (precondition.effect.equals(effect.effect)) {
                    if (precondition.value.equals(effect.value)) {
                        currentPreconditionMet = true;
                    } else {
                        preconditionsMet = false;
                    }
                }
            }

            if (!preconditionsMet || !currentPreconditionMet) {
                preconditionsMet = false;

                break;
            }
        }
        return preconditionsMet;
    }

    /**
     * Convenience function for adding a weighted edge to an existing Graph.
     *
     * @param graph        the Graph the edge is added to.
     * @param firstVertex  start vertex.
     * @param secondVertex target vertex.
     * @param edge         edge reference.
     * @param weight       the weight of the edge being created.
     * @return true or false depending on if the creation of the edge was
     * successful or not.
     */
    protected static <V, E extends WeightedEdge> boolean addEgdeWithWeigth(
            @NonNull final IWeightedGraph<V, E> graph, V firstVertex,
            @NonNull final V secondVertex,
            @NonNull final E edge,
            final double weight
    ) {
        try {
            graph.addEdge(firstVertex, secondVertex, edge);
            graph.setEdgeWeight(graph.getEdge(firstVertex, secondVertex), weight);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Function for adding a new node to a given GraphPath. The new node is
     * added to a sublist of the provided path vertexSet.
     *
     * @param graph         the graph the path is located in.
     * @param baseGraphPath the path to which a node is being added.
     * @param nodeToAdd     the node which shall be added.
     * @return a graphPath with a given node as the end element, updated
     * vertexSet, edgeSet and weight.
     */
    protected static WeightedPath<GraphNode, WeightedEdge> addNodeToGraphPath(
            @NonNull final IWeightedGraph<GraphNode, WeightedEdge> graph,
            @NonNull final WeightedPath<GraphNode, WeightedEdge> baseGraphPath,
            @NonNull final GraphNode nodeToAdd
    ) {
        final List<GraphNode> vertices = new ArrayList<>(baseGraphPath.getVertexList());
        final List<WeightedEdge> edges = new ArrayList<>(baseGraphPath.getEdgeList());

        vertices.add(nodeToAdd);
        edges.add(graph.getEdge(baseGraphPath.getEndVertex(), nodeToAdd));

        return PathFactory.generateWeightedPath(graph, baseGraphPath.getStartVertex(), nodeToAdd, vertices, edges);
    }

    /**
     * Sorting function for the paths leading to a node based on their combined
     * edge weights (ascending).
     *
     * @param node the node whose paths leading to it are being sorted.
     */
    protected static void sortPathsLeadingToNode(@NonNull final GraphNode node) {
        node.getPathsToThisNode().sort(Comparator.comparingDouble(WeightedPath::getTotalWeight));
    }

    /**
     * Function for extracting all Actions from a GraphPath.
     *
     * @param path      the Path from which the actions are being extracted.
     * @param startNode the starting node needs to be known as it contains no action.
     * @param endNode   the end node needs to be known since it contains no action.
     * @return a Queue in which all actions from the given path are listed.
     */
    protected static Queue<GoapAction> extractActionsFromGraphPath(
            @NonNull final WeightedPath<GraphNode, WeightedEdge> path,
            @NonNull final GraphNode startNode,
            @NonNull final GraphNode endNode
    ) {
        final Queue<GoapAction> actionQueue = new LinkedList<GoapAction>();

        for (GraphNode node : path.getVertexList()) {
            if (!node.equals(startNode) && !node.equals(endNode)) {
                actionQueue.add(node.getAction());
            }
        }

        return actionQueue;
    }

    protected abstract <EdgeType extends WeightedEdge> IWeightedGraph<GraphNode, EdgeType> generateGraphObject();

    /**
     * Generate a plan (Queue of GoapActions) which is then performed by the
     * assigned GoapUnit. A search algorithm is not needed as each node contains
     * each path to itself. Therefore each goal contains a list of paths leading
     * starting from the worldState through multiple node directly to it. The
     * goals and their paths can be sorted according to these and the importance
     * of each goal with the weight provided by each node inside the graph.
     *
     * @param goapUnit the GoapUnit the plan gets generated for.
     * @return a generated plan (Queue) of GoapActions, that the GoapUnit has to
     * perform to archive the desired goalState OR null, if no plan was
     * generated.
     */
    public Queue<GoapAction> plan(@NonNull final IGoapUnit goapUnit) {
        Queue<GoapAction> createdPlan = null;
        this.goapUnit = goapUnit;
        startNode = new GraphNode(null);
        endNodes = new ArrayList<>();

        try {
            sortGoalStates();

            // The Integer.MaxValue indicates that the goal was passed by the
            // changeGoalImmediatly function. An empty Queue is returned instead
            // of null because null would result in the IdleState to call this
            // function again. An empty Queue is finished in one cycle with no
            // effect at all.
            if (this.goapUnit.getGoalState().get(0).importance == Integer.MAX_VALUE) {
                final List<GoapState> goalState = new ArrayList<GoapState>();

                goalState.add(this.goapUnit.getGoalState().get(0));

                createdPlan = searchGraphForActionQueue(createGraph(goalState));

                if (createdPlan == null) {
                    createdPlan = new LinkedList<>();
                }

                this.goapUnit.getGoalState().remove(0);
            } else {
                createdPlan = searchGraphForActionQueue(createGraph());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return createdPlan;
    }

    // ---------------------------------------- Edges

    /**
     * Function for sorting a goapUnits goalStates (descending). The most
     * important goal has the highest importance value.
     *
     * @return the sorted goal list of the goapUnit.
     */
    protected List<GoapState> sortGoalStates() {
        final List<GoapState> goalState = goapUnit.getGoalState();
        if (goalState.size() > 1) {
            goalState.sort((o1, o2) -> o2.importance.compareTo(o1.importance));
        }

        return goalState;
    }

    /**
     * Convenience function for all current goalStates.
     *
     * @see #createGraph(List goalState)
     */
    protected IWeightedGraph<GraphNode, WeightedEdge> createGraph() {
        return createGraph(goapUnit.getGoalState());
    }

    /**
     * Function to create a graph based on all possible unit actions of the
     * GoapUnit for (a) specific goal/-s.
     *
     * @param goalState list of states the action queue has to fulfill.
     * @return a DirectedWeightedGraph generated from all possible unit actions
     * for a goal.
     */
    protected IWeightedGraph<GraphNode, WeightedEdge> createGraph(@NonNull final List<GoapState> goalState) {
        final IWeightedGraph<GraphNode, WeightedEdge> generatedGraph = this.generateGraphObject();
        addVertices(generatedGraph, goalState);
        addEdges(generatedGraph);

        return generatedGraph;
    }

    /**
     * Function for adding vertices to a graph.
     *
     * @param graph     the graph the vertices are being added to.
     * @param goalState List of States the unit has listed as their goals.
     */
    protected void addVertices(
            @NonNull final IWeightedGraph<GraphNode, WeightedEdge> graph,
            @NonNull final List<GoapState> goalState
    ) {
        // The effects from the world state as well as the precondition of each
        // goal have to be set at the beginning, since these are the effects the
        // unit tries to archive with its actions. Also the startNode has to
        // overwrite the existing GraphNode as an initialization of a new Object
        // would not be reflected to the function caller.
        final GraphNode start = new GraphNode(null, this.goapUnit.getWorldState());
        startNode.overwriteOwnProperties(start);
        graph.addVertex(startNode);

        for (final GoapState state : goalState) {
            final HashSet<GoapState> goalStateHash = new HashSet<GoapState>();
            goalStateHash.add(state);

            final GraphNode end = new GraphNode(goalStateHash, null);
            graph.addVertex(end);
            endNodes.add(end);
        }

        final HashSet<GoapAction> possibleActions = extractPossibleActions();

        // Afterward all other possible actions have to be added as well.
        if (possibleActions != null) {
            for (GoapAction goapAction : possibleActions) {
                graph.addVertex(new GraphNode(goapAction));
            }
        }
    }

    /**
     * Needed to check if the available actions can actually be performed
     *
     * @return all possible actions which are actually available for the unit.
     */
    protected HashSet<GoapAction> extractPossibleActions() {
        final HashSet<GoapAction> possibleActions = new HashSet<GoapAction>();

        try {
            for (GoapAction goapAction : this.goapUnit.getAvailableActions()) {
                if (goapAction.checkProceduralPrecondition(this.goapUnit)) {
                    possibleActions.add(goapAction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return possibleActions;
    }

    /**
     * Function for adding (all) edges in the provided graph based on the
     * GoapUnits actions and their combined effects along the way. The way this
     * is archived is by first adding all default nodes, whose preconditions are
     * met by the effect of the beginning state (worldState). All further
     * connections base on these first connected nodes in the Queue. Elements
     * connected are getting added to a Queue which is then being worked on.
     *
     * @param graph the graph the edges are being added to.
     */
    protected void addEdges(@NonNull final IWeightedGraph<GraphNode, WeightedEdge> graph) {
        final Queue<GraphNode> nodesToWorkOn = new LinkedList<>();
        addDefaultEdges(graph, nodesToWorkOn);

        // Check each already connected once node against all other nodes to
        // find a possible match between the combined effects of the path + the
        // worldState and the preconditions of the current node.
        while (!nodesToWorkOn.isEmpty()) {
            final GraphNode node = nodesToWorkOn.poll();
            // Select only node to which a path can be created (-> targets!)
            if (!node.equals(this.startNode) && !this.endNodes.contains(node)) {
                tryToConnectNode(graph, node, nodesToWorkOn);
            }
        }
    }

    /**
     * Function for adding the edges to the graph which are the connection from
     * the starting node to all default accessible nodes (= actions). These
     * nodes either have no precondition or their preconditions are all in the
     * effect HashSet of the starting node. These default edges are needed since
     * all further connections rely on them as nodes in the further steps are
     * not allowed to connect to the starting node anymore.
     *
     * @param graph         the graph the edges are getting added to.
     * @param nodesToWorkOn the Queue in which nodes which got connected are getting added
     *                      to.
     */
    protected void addDefaultEdges(
            @NonNull final IWeightedGraph<GraphNode, WeightedEdge> graph,
            @NonNull final Queue<GraphNode> nodesToWorkOn
    ) {

        // graphNode.action != null -> start and ends
        for (final GraphNode graphNode : graph.getVertices()) {
            if (!startNode.equals(graphNode) && graphNode.getAction() != null && (graphNode.getPreconditions().isEmpty()
                    || areAllPreconditionsMet(graphNode.getPreconditions(), startNode.getEffects()))) {
                addEgdeWithWeigth(graph, this.startNode, graphNode, new WeightedEdge(), 0);
                if (!nodesToWorkOn.contains(graphNode)) {
                    nodesToWorkOn.add(graphNode);
                }

                // Add the path to the node to the GraphPath list in the node
                // since this is the first step inside the graph.
                final List<GraphNode> vertices = new ArrayList<GraphNode>();
                final List<WeightedEdge> edges = new ArrayList<WeightedEdge>();

                vertices.add(startNode);
                vertices.add(graphNode);

                edges.add(graph.getEdge(startNode, graphNode));

                final WeightedPath<GraphNode, WeightedEdge> graphPathToDefaultNode = PathFactory.generateWeightedPath(graph, startNode, graphNode, vertices, edges);

                graphNode.addGraphPath(null, graphPathToDefaultNode);
            }
        }
    }

    /**
     * Function for trying to connect a given node to all other nodes in the
     * graph besides the starting node.
     *
     * @param graph         the graph in which the provided nodes are located.
     * @param node          the node which is being connected to another node.
     * @param nodesToWorkOn the Queue to which any connected nodes are being added to work
     *                      on these connections in further iterations.
     * @return true or false depending on if the node was connected to another
     * node.
     */
    protected boolean tryToConnectNode(
            @NonNull final IWeightedGraph<GraphNode, WeightedEdge> graph,
            @NonNull final GraphNode node,
            @NonNull final Queue<GraphNode> nodesToWorkOn
    ) {
        boolean connected = false;

        for (final GraphNode otherNodeInGraph : graph.getVertices()) {
            // End nodes can not have a edge towards another node and the target
            // node must not be itself. Also there must not already be an edge
            // in the graph.
            // && !graph.containsEdge(node, nodeInGraph) has to be added
            // or loops occur which lead to a crash. This leads to the case
            // where no
            // alternative routes are being stored inside the pathsToThisNode
            // list. This is because of the use of a Queue, which loses the
            // memory of which nodes were already connected.
            if (!node.equals(otherNodeInGraph) && !this.startNode.equals(otherNodeInGraph)
                    && !graph.containsEdge(node, otherNodeInGraph)) {

                // Every saved path to this node is checked if any of these
                // produce a suitable effect set regarding the preconditions of
                // the current node.
                for (final WeightedPath<GraphNode, WeightedEdge> pathToListNode : node.getPathsToThisNode()) {
                    if (areAllPreconditionsMet(otherNodeInGraph.getPreconditions(), node.getEffectState(pathToListNode))) {
                        connected = true;

                        addEgdeWithWeigth(graph, node, otherNodeInGraph, new WeightedEdge(),
                                node.getAction().generateCost(this.goapUnit));

                        otherNodeInGraph.addGraphPath(pathToListNode,
                                addNodeToGraphPath(graph, pathToListNode, otherNodeInGraph));

                        nodesToWorkOn.add(otherNodeInGraph);

                        // break; // TODO: Possible Change: If enabled then only
                        // one Path from the currently checked node is
                        // transferred to another node. All other possible Paths
                        // will not be considered and not checked.
                    }
                }
            }
        }
        return connected;
    }

    /**
     * Function for searching a graph for the lowest cost of a series of actions
     * which have to be taken to archive a certain goal which has most certainly
     * the highest importance.
     *
     * @param graph the graph of GoapActions the unit has to take in order to
     *              archive a goal.
     * @return the Queue of GoapActions which has the lowest cost to archive a
     * goal.
     */
    protected Queue<GoapAction> searchGraphForActionQueue(IWeightedGraph<GraphNode, WeightedEdge> graph) {
        Queue<GoapAction> actionQueue = null;

        for (int i = 0; i < this.endNodes.size() && actionQueue == null; i++) {
            sortPathsLeadingToNode(this.endNodes.get(i));

            for (int j = 0; j < this.endNodes.get(i).getPathsToThisNode().size() && actionQueue == null; j++) {
                actionQueue = extractActionsFromGraphPath(this.endNodes.get(i).getPathsToThisNode().get(j), this.startNode,
                        this.endNodes.get(i));
            }
        }
        return actionQueue;
    }

}
