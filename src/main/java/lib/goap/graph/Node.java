package lib.goap.graph;

import lib.goap.action.GoapActionBase;
import lib.goap.state.GoapState;
import lib.goap.state.WorldAspect;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Node {

    @NonNull
    private final HashMap<WeightedPath<Node, WeightedEdge>, HashSet<GoapState>> storedEffectStates = new HashMap<>();

    @Getter
    @NonNull
    private final List<WeightedPath<Node, WeightedEdge>> pathsToThisNode = new ArrayList<>();

    @Getter
    @Nullable
    private GoapActionBase action;

    @Getter
    @Nullable
    private Set<GoapState> preconditions;

    @Getter
    @Nullable
    private Set<GoapState> effects;

    /**
     * @param preconditions the HashSet of preconditions the node has. Each preconditions
     *                      have to be met before another node can be connected to this
     *                      node.
     * @param effects       the HashSet of effects the node has on the graph. Effects get
     *                      added together along the graph to hopefully meet a goalState.
     */
    public Node(
            @Nullable final Set<GoapState> preconditions,
            @Nullable final Set<GoapState> effects
    ) {
        this.preconditions = preconditions;
        this.effects = effects;
    }

    /**
     * @param worldAspects The worldAspects that are used to define the effects of the node.
     */
    public Node(
            @NonNull final Set<WorldAspect> worldAspects
    ) {
        this.preconditions = null;
        this.effects = worldAspects.stream()
                .map((aspect -> (GoapState) aspect))
                .collect(Collectors.toSet());
    }

    /**
     * @param goapAction the action whose effects and preconditions are being used to
     *                   define the node.
     */
    public Node(@Nullable final GoapActionBase goapAction) {
        if (goapAction != null) {
            this.preconditions = goapAction.getPreconditions();
            this.effects = goapAction.getEffects();
            this.action = goapAction;
        }
    }

    /**
     * Default constructor for a Node.
     */
    public Node() {
        this.preconditions = null;
        this.effects = null;
    }

    /**
     * Function for inserting existing Nodes values into the current one.
     *
     * @param newNode the node whose properties are going to be copied.
     */
    public void overwriteOwnProperties(@Nullable final Node newNode) {
        if (newNode != null) {
            this.action = newNode.action;
            this.preconditions = newNode.preconditions;
            this.effects = newNode.effects;
        }
    }

    /**
     * Function for adding paths to a node so that the order in which the node
     * is accessed is saved (Important!). If these would not be stored invalid
     * orders of actions could be added to the graph as a node can return
     * multiple access paths!
     *
     * @param pathToPreviousNode the path with which the previous node is accessed.
     * @param newPath            the path with which the node is accessed.
     */
    public void addGraphPath(
            @Nullable final WeightedPath<Node, WeightedEdge> pathToPreviousNode,
            @NonNull final WeightedPath<Node, WeightedEdge> newPath
    ) {
        final List<Node> newPathNodeList = newPath.getNodeList();
        boolean notInSet = true;

        if (pathsToThisNode.isEmpty()) {
            notInSet = true;
        } else {
            for (final WeightedPath<Node, WeightedEdge> storedPath : pathsToThisNode) {
                List<Node> nodeList = storedPath.getNodeList();
                boolean isSamePath = true;

                for (int i = 0; i < nodeList.size() && isSamePath; i++) {
                    if (!nodeList.get(i).equals(newPathNodeList.get(i))) {
                        isSamePath = false;
                    }
                }

                if (isSamePath) {
                    notInSet = false;

                    break;
                }
            }
        }

        if (notInSet) {
            pathsToThisNode.add(newPath);
            if (newPath.getEndNode().action != null) {
                storedEffectStates.put(newPath, addPathEffectsTogether(pathToPreviousNode, newPath));
            }
        }
    }

    /**
     * Function for adding all effects in a path together to get the effect at
     * the last node in the path.
     *
     * @param pathToPreviousNode to the previous node so that not all effects need to be added
     *                           together again and again. The reference to this is the key in
     *                           the last elements storedPathEffects HashTable.
     * @param path               the path on which all effects are getting added together.
     * @return the HashSet of effects at the last node in the path.
     */
    private HashSet<GoapState> addPathEffectsTogether(
            @Nullable final WeightedPath<Node, WeightedEdge> pathToPreviousNode,
            @NonNull final WeightedPath<Node, WeightedEdge> path
    ) {
        HashSet<GoapState> combinedNodeEffects;
        final List<GoapState> statesToBeRemoved = new ArrayList<>();

        // No path leading to the previous node = node is starting point =>
        // sublist of all effects
        if (pathToPreviousNode == null) {
            combinedNodeEffects = new HashSet<>(path.getStartNode().effects);
        } else {
            combinedNodeEffects = new HashSet<>(
                    pathToPreviousNode.getEndNode().getEffectState(pathToPreviousNode));
        }

        // Mark effects to be removed
        for (final GoapState nodeWorldState : combinedNodeEffects) {
            for (final GoapState pathNodeEffect : effects) {
                if (nodeWorldState.getEffect().equals(pathNodeEffect.getEffect())) {
                    statesToBeRemoved.add(nodeWorldState);
                }
            }
        }

        // Remove marked effects from the state
        for (final GoapState stateToRemove : statesToBeRemoved) {
            combinedNodeEffects.remove(stateToRemove);
        }

        // Add all effects from the current node to the HashSet
        for (final GoapState effect : effects) {
            combinedNodeEffects.add(effect);
        }

        return combinedNodeEffects;
    }

    @NonNull
    public HashSet<GoapState> getEffectState(@NonNull final WeightedPath<Node, WeightedEdge> pathKey) {
        return this.storedEffectStates.get(pathKey);
    }

}
