package lib.goap.planner;

import lib.goap.action.GoapActionBase;
import lib.goap.graph.*;
import lib.goap.state.GoapState;
import lib.goap.unit.IGoapUnit;
import lombok.Getter;
import lombok.NonNull;
import org.hibernate.mapping.Array;
import org.springframework.lang.Nullable;

import java.util.*;

@Getter
public abstract class GoapPlannerBase implements GoapPlannerable {

    @Nullable
    private IGoapUnit goapUnit;
    @Nullable
    private Node startNode;
    @Nullable
    private List<Node> endNodes;


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
            @NonNull final Set<GoapState> preconditions,
            @NonNull final Set<GoapState> effects
    ) {
        boolean preconditionsMet = true;

        for (GoapState precondition : preconditions) {
            boolean currentPreconditionMet = false;

            for (GoapState effect : effects) {
                if (precondition.getEffect().equals(effect.getEffect())) {
                    if (precondition.getValue().equals(effect.getValue())) {
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
    protected static WeightedPath<Node, WeightedEdge> addNodeToGraphPath(
            @NonNull final IWeightedGraph<Node, WeightedEdge> graph,
            @NonNull final WeightedPath<Node, WeightedEdge> baseGraphPath,
            @NonNull final Node nodeToAdd
    ) {
        final List<Node> nodes = new ArrayList<>(baseGraphPath.getNodeList());
        final List<WeightedEdge> edges = new ArrayList<>(baseGraphPath.getEdgeList());

        nodes.add(nodeToAdd);
        edges.add(graph.getEdge(baseGraphPath.getEndNode(), nodeToAdd));

        return PathFactory.generateWeightedPath(graph, baseGraphPath.getStartNode(), nodeToAdd, nodes, edges);
    }

    /**
     * Sorting function for the paths leading to a node based on their combined
     * edge weights (ascending).
     *
     * @param node the node whose paths leading to it are being sorted.
     */
    protected static void sortPathsLeadingToNode(@NonNull final Node node) {
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
    protected static Queue<GoapActionBase> extractActionsFromGraphPath(
            @NonNull final WeightedPath<Node, WeightedEdge> path,
            @NonNull final Node startNode,
            @NonNull final Node endNode
    ) {
        final Queue<GoapActionBase> actionQueue = new LinkedList<GoapActionBase>();

        for (Node node : path.getNodeList()) {
            if (!node.equals(startNode) && !node.equals(endNode)) {
                actionQueue.add(node.getAction());
            }
        }

        return actionQueue;
    }

    protected abstract <EdgeType extends WeightedEdge> IWeightedGraph<Node, EdgeType> generateGraphObject();

    /**
     * Generate a plan (Queue of GoapActions) which is then performed by the
     * assigned GoapUnit. A search algorithm is not needed as each node contains
     * each path to itself. Therefore, each goal contains a list of paths leading
     * starting from the worldState through multiple node directly to it. The
     * goals and their paths can be sorted according to these and the importance
     * of each goal with the weight provided by each node inside the graph.
     *
     * @param goapUnit the GoapUnit the plan gets generated for.
     * @return a generated plan (Queue) of GoapActions, that the GoapUnit has to
     * perform to archive the desired goalState OR null, if no plan was
     * generated.
     */
    public Queue<GoapActionBase> planActions(@NonNull final IGoapUnit goapUnit) {
        Queue<GoapActionBase> createdPlan = null;
        this.goapUnit = goapUnit;
        startNode = new Node();
        endNodes = new ArrayList<>();

        try {
            sortGoalStates();
            // The Integer.MaxValue indicates that the goal was passed by the
            // changeGoalImmediatly function. An empty Queue is returned instead
            // of null because null would result in the IdleState to call this
            // function again. An empty Queue is finished in one cycle with no
            // effect at all.

            // if the topGoalState has the highest importance, then it is the one and only goalState
            if (goapUnit.getGoalState().get(0).getImportance() == Integer.MAX_VALUE) {
                final GoapState topGoalState = goapUnit.getGoalState().get(0);
                createdPlan = searchGraphForActionQueue(createGraph(List.of(topGoalState)));

                if (createdPlan == null) {
                    createdPlan = new LinkedList<>();
                }

                goapUnit.getGoalState().remove(0);
            } else {
                createdPlan = searchGraphForActionQueue(createGraph());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return createdPlan;
    }

    /**
     * Function for sorting a goapUnits goalStates (descending). The most
     * important goal has the highest importance value.
     */
    protected void sortGoalStates() {
        goapUnit.getGoalState().sort(Comparator.comparing(GoapState::getImportance, Comparator.nullsLast(Comparator.reverseOrder())));
        // @TODO ist das richtig herum sortiert??????????
//        if (goapUnit.getGoalState().size() > 1) {
//            goapUnit.getGoalState().sort((o1, o2) -> o2.getImportance().compareTo(o1.getImportance()));
//        }
    }

    /**
     * Convenience function for all current goalStates.
     *
     * @see #createGraph(List goalState)
     */
    protected IWeightedGraph<Node, WeightedEdge> createGraph() {
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
    protected IWeightedGraph<Node, WeightedEdge> createGraph(@NonNull final List<GoapState> goalState) {
        final IWeightedGraph<Node, WeightedEdge> generatedGraph = generateGraphObject();
        addNodes(generatedGraph, goalState);
        addEdges(generatedGraph);

        return generatedGraph;
    }

    /**
     * Function for adding nodes to a graph.
     *
     * @param graph     the graph the nodes are being added to.
     * @param goalState List of States the unit has listed as their goals.
     */
    protected void addNodes(
            @NonNull final IWeightedGraph<Node, WeightedEdge> graph,
            @NonNull final List<GoapState> goalState
    ) {
        // The effects from the world state as well as the precondition of each
        // goal have to be set at the beginning, since these are the effects the
        // unit tries to archive with its actions. Also, the startNode has to
        // overwrite the existing Node as an initialization of a new Object
        // would not be reflected to the function caller.
        final Node start = new Node(goapUnit.getWorldState());
        startNode.overwriteOwnProperties(start);
        graph.addNode(startNode);

        for (final GoapState state : goalState) {
            final HashSet<GoapState> goalStatePreconditions = new HashSet<>();
            goalStatePreconditions.add(state);

            final Node end = new Node(goalStatePreconditions, null);
            graph.addNode(end);

            endNodes.add(end);
        }

        final HashSet<GoapActionBase> possibleActions = extractPossibleActions();

        // @TODO warum werden hier die actions nochmal hinzugefügt? Ich dachte nur states werden hinzugefügt?
        // Afterward all other possible actions have to be added as well.
        if (possibleActions != null) {
            for (final GoapActionBase goapAction : possibleActions) {
                graph.addNode(new Node(goapAction));
            }
        }
    }

    /**
     * Needed to check if the available actions can actually be performed
     *
     * @return all possible actions which are actually available for the unit.
     */
    protected HashSet<GoapActionBase> extractPossibleActions() {
        final HashSet<GoapActionBase> possibleActions = new HashSet<>();

        try {
            for (final GoapActionBase goapAction : goapUnit.getAvailableActions()) {
                if (goapAction.checkProceduralPrecondition(goapUnit)) {
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
    protected void addEdges(@NonNull final IWeightedGraph<Node, WeightedEdge> graph) {
        final Queue<Node> nodesToWorkOn = new LinkedList<>();
        addDefaultEdges(graph, nodesToWorkOn);

        // Check each already connected once node against all other nodes to
        // find a possible match between the combined effects of the path + the
        // worldState and the preconditions of the current node.
        while (!nodesToWorkOn.isEmpty()) {
            final Node node = nodesToWorkOn.poll();
            // Select only node to which a path can be created (-> targets!)
            if (!node.equals(startNode) && !endNodes.contains(node)) {
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
            @NonNull final IWeightedGraph<Node, WeightedEdge> graph,
            @NonNull final Queue<Node> nodesToWorkOn
    ) {

        // Node.action != null -> start and ends
        for (final Node node : graph.getNodes()) {
            if (!startNode.equals(node) && node.getAction() != null && (node.getPreconditions().isEmpty()
                    || areAllPreconditionsMet(node.getPreconditions(), startNode.getEffects()))) {
                addEgdeWithWeigth(graph, startNode, node, new WeightedEdge(), 0);
                if (!nodesToWorkOn.contains(node)) {
                    nodesToWorkOn.add(node);
                }

                // Add the path to the node to the GraphPath list in the node
                // since this is the first step inside the graph.
                final List<Node> nodes = new ArrayList<Node>();
                final List<WeightedEdge> edges = new ArrayList<WeightedEdge>();

                nodes.add(startNode);
                nodes.add(node);

                edges.add(graph.getEdge(startNode, node));

                final WeightedPath<Node, WeightedEdge> graphPathToDefaultNode = PathFactory.generateWeightedPath(graph, startNode, node, nodes, edges);

                node.addGraphPath(null, graphPathToDefaultNode);
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
            @NonNull final IWeightedGraph<Node, WeightedEdge> graph,
            @NonNull final Node node,
            @NonNull final Queue<Node> nodesToWorkOn
    ) {
        boolean connected = false;

        for (final Node otherNodeInGraph : graph.getNodes()) {
            // End nodes can not have a edge towards another node and the target
            // node must not be itself. Also there must not already be an edge
            // in the graph.
            // && !graph.containsEdge(node, nodeInGraph) has to be added
            // or loops occur which lead to a crash. This leads to the case
            // where no
            // alternative routes are being stored inside the pathsToThisNode
            // list. This is because of the use of a Queue, which loses the
            // memory of which nodes were already connected.
            if (!node.equals(otherNodeInGraph) && !startNode.equals(otherNodeInGraph)
                    && !graph.containsEdge(node, otherNodeInGraph)) {

                // Every saved path to this node is checked if any of these
                // produce a suitable effect set regarding the preconditions of
                // the current node.
                for (final WeightedPath<Node, WeightedEdge> pathToListNode : node.getPathsToThisNode()) {
                    if (areAllPreconditionsMet(otherNodeInGraph.getPreconditions(), node.getEffectState(pathToListNode))) {
                        connected = true;

                        addEgdeWithWeigth(graph, node, otherNodeInGraph, new WeightedEdge(),
                                node.getAction().generateCost(goapUnit));

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
    @Nullable
    protected Queue<GoapActionBase> searchGraphForActionQueue(@NonNull IWeightedGraph<Node, WeightedEdge> graph) {
        Queue<GoapActionBase> actionQueue = null;

        for (int i = 0; i < endNodes.size() && actionQueue == null; i++) {
            sortPathsLeadingToNode(endNodes.get(i));

            for (int j = 0; j < endNodes.get(i).getPathsToThisNode().size() && actionQueue == null; j++) {
                actionQueue = extractActionsFromGraphPath(endNodes.get(i).getPathsToThisNode().get(j), startNode,
                        endNodes.get(i));
            }
        }
        return actionQueue;
    }

}
