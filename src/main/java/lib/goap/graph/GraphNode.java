package lib.goap.graph;

import lib.goap.GoapAction;
import lib.goap.GoapState;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GraphNode {

    @NonNull
    private final HashMap<WeightedPath<GraphNode, WeightedEdge>, HashSet<GoapState>> storedEffectStates = new HashMap<>();
    @Getter
    @NonNull
    private final List<WeightedPath<GraphNode, WeightedEdge>> pathsToThisNode = new ArrayList<>();
    @Getter
    @Nullable
    private GoapAction action = null;
    @Getter
    @Nullable
    private HashSet<GoapState> preconditions;
    @Getter
    @Nullable
    private HashSet<GoapState> effects;

    /**
     * @param preconditions the HashSet of preconditions the node has. Each preconditions
     *                      have to be met before another node can be connected to this
     *                      node.
     * @param effects       the HashSet of effects the node has on the graph. Effects get
     *                      added together along the graph to hopefully meet a goalState.
     */
    public GraphNode(
            @Nullable final HashSet<GoapState> preconditions,
            @Nullable final HashSet<GoapState> effects
    ) {
        this.preconditions = preconditions;
        this.effects = effects;
    }

    /**
     * @param goapAction the action whose effects and preconditions are being used to
     *                   define the node.
     */
    public GraphNode(@Nullable final GoapAction goapAction) {
        if (goapAction != null) {
            this.preconditions = goapAction.getPreconditions();
            this.effects = goapAction.getEffects();
            this.action = goapAction;
        }
    }

    // -------------------- Functions

    /**
     * Function for inserting existing GraphNodes values into the current one.
     *
     * @param newGraphNode the node whose properties are going to be copied.
     */
    public void overwriteOwnProperties(@Nullable final GraphNode newGraphNode) {
        if (newGraphNode != null) {
            this.action = newGraphNode.action;
            this.preconditions = newGraphNode.preconditions;
            this.effects = newGraphNode.effects;
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
            @Nullable final WeightedPath<GraphNode, WeightedEdge> pathToPreviousNode,
            @NonNull final WeightedPath<GraphNode, WeightedEdge> newPath
    ) {
        final List<GraphNode> newPathNodeList = newPath.getVertexList();
        boolean notInSet = true;

        if (pathsToThisNode.isEmpty()) {
            notInSet = true;
        } else {
            for (final WeightedPath<GraphNode, WeightedEdge> storedPath : pathsToThisNode) {
                List<GraphNode> nodeList = storedPath.getVertexList();
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
            if (newPath.getEndVertex().action != null) {
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
            @Nullable final WeightedPath<GraphNode, WeightedEdge> pathToPreviousNode,
            @NonNull final WeightedPath<GraphNode, WeightedEdge> path
    ) {
        HashSet<GoapState> combinedNodeEffects;
        final List<GoapState> statesToBeRemoved = new ArrayList<>();

        // No path leading to the previous node = node is starting point =>
        // sublist of all effects
        if (pathToPreviousNode == null) {
            combinedNodeEffects = new HashSet<>(path.getStartVertex().effects);
        } else {
            combinedNodeEffects = new HashSet<>(
                    pathToPreviousNode.getEndVertex().getEffectState(pathToPreviousNode));
        }

        // Mark effects to be removed
        for (final GoapState nodeWorldState : combinedNodeEffects) {
            for (final GoapState pathNodeEffect : effects) {
                if (nodeWorldState.effect.equals(pathNodeEffect.effect)) {
                    statesToBeRemoved.add(nodeWorldState);
                }
            }
        }

        // Remove marked effects from the state
        for (final GoapState stateToRemove : statesToBeRemoved) {
            combinedNodeEffects.remove(stateToRemove);
        }

        // Add all effects from the current node to the HashSet
        for (final GoapState effect : this.effects) {
            combinedNodeEffects.add(effect);
        }

        return combinedNodeEffects;
    }

    @NonNull
    public HashSet<GoapState> getEffectState(@NonNull final WeightedPath<GraphNode, WeightedEdge> pathKey) {
        return this.storedEffectStates.get(pathKey);
    }

}
