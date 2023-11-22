package lib.clipboard.maze.searchstrategy;

import lib.clipboard.graph.DijkstraNode;
import lib.clipboard.graph.DijkstraResult;
import lib.clipboard.graph.WeightedEdge;
import lib.clipboard.graph.WeightedGraph;
import lombok.NonNull;

import java.util.*;

/**
 * A* Search
 */
public class DijkstraStrategy<VERTEX_TYPE> {

    @NonNull
    public DijkstraResult dijkstra(
            @NonNull final WeightedGraph<VERTEX_TYPE> graph,
            @NonNull final VERTEX_TYPE root
    ) {
        int startIndex = graph.indexOf(root);

        // distance container from start to each vertex
        final double[] distances = new double[graph.getVertexCount()];
//        Arrays.fill(distances, Double.MAX_VALUE);                         // would this make sense? // <<<<<<<<<<<<<<<<<<<<<
        distances[startIndex] = 0; // distance from start to start is 0   // <<<<<<<<<<<<<<<<<<<<< technically not needed; as default value is 0 in java new double[] arrays

        // track visited vertices
        final boolean[] visited = new boolean[graph.getVertexCount()];
        visited[startIndex] = true;

        // track paths to vertices
        final HashMap<Integer, WeightedEdge> pathMap = new HashMap<>(); // com.recom.dto.map to track paths to each node/vertex

        // track nodes to visit (frontier, sorted by distance due to priority queue)
        final PriorityQueue<DijkstraNode> priorityQueue = new PriorityQueue<>();

        // prefill priority queue with start node
        final DijkstraNode rootNode = new DijkstraNode(startIndex, 0);
        priorityQueue.offer(rootNode);

        while (!priorityQueue.isEmpty()) {
            final DijkstraNode currentNode = priorityQueue.poll(); // get nearest neighbour
            final double distanceFromRootToCurrentNode = distances[currentNode.getVertex()]; // distance already known; distances are updated later before next iteration

            // iterate through all neighbours/edges of current node (frontier)
            for (final WeightedEdge edge : graph.edgesOf(currentNode.getVertex())) {
                final int neighbourNodeIndex = edge.to;
                final double oldDistanceFromRootToNeighbourNode = distances[neighbourNodeIndex]; // old distance from root to explored neighbourNode // <<<<<<<<<<<<<<<<<<<<<
                final double pathWeight = distanceFromRootToCurrentNode + edge.getWeight(); // new distance from root to neighbourNode

                // if currentNode is not visited yet or the new distance is shorter than the old distance
                if (!visited[neighbourNodeIndex] || (oldDistanceFromRootToNeighbourNode > pathWeight)) {
                    visited[neighbourNodeIndex] = true; // mark as visited
                    distances[neighbourNodeIndex] = pathWeight; // update distance from source to currentNode
                    pathMap.put(neighbourNodeIndex, edge); // update with (shortest) path to currentNode
                    priorityQueue.offer(new DijkstraNode(neighbourNodeIndex, pathWeight)); // add currentNode to priority queue for exploration
                }
            }
        }

        return new DijkstraResult(distances, pathMap);
    }

    @NonNull
    public Map<VERTEX_TYPE, Double> distanceArrayToDistanceMap(
            @NonNull final WeightedGraph<VERTEX_TYPE> graph,
            final double[] distances
    ) {
        final Map<VERTEX_TYPE, Double> distanceMap = new HashMap<>();
        for (int i = 0; i < distances.length; i++) {
            distanceMap.put(graph.vertexAt(i), distances[i]);
        }

        return distanceMap;
    }

    @NonNull
    public List<WeightedEdge> pathToPathMap(
            final int start,
            final int end,
            @NonNull final Map<Integer, WeightedEdge> pathMap
    ) {
        if (pathMap.isEmpty()) {
            return List.of();
        }

        final LinkedList<WeightedEdge> path = new LinkedList<>();

        WeightedEdge edge = pathMap.get(end);
        path.add(edge);

        // build path from end to start
        while (edge.from != start) {
            final WeightedEdge nextEdge = pathMap.get(edge.from);
            path.add(nextEdge);
            edge = nextEdge;
        }

        // reverse path: from start to end
        Collections.reverse(path);

        return path;
    }

}
