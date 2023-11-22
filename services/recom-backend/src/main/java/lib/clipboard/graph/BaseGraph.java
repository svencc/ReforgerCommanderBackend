package lib.clipboard.graph;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseGraph<VERTEX_TYPE, EDGE_TYPE extends Edge> {

    @NonNull
    protected final ArrayList<ArrayList<EDGE_TYPE>> edges = new ArrayList<>();
    @NonNull
    private final ArrayList<VERTEX_TYPE> vertices = new ArrayList<>();

    public BaseGraph(@NonNull final List<VERTEX_TYPE> vertices) {
        this.vertices.addAll(vertices);
        for (final VERTEX_TYPE vertex : vertices) {
            edges.add(new ArrayList<>());
        }
    }

    public int getEdgeCount() {
        return edges.stream()
                .mapToInt(List::size)
                .sum();
    }

    public int addVertex(@NonNull final VERTEX_TYPE vertex) {
        vertices.add(vertex);
        edges.add(new ArrayList<>());
        return getVertexCount() - 1;
    }

    public int getVertexCount() {
        return vertices.size();
    }

    @NonNull
    public List<VERTEX_TYPE> neighborsOf(@NonNull final VERTEX_TYPE vertex) {
        return neighborsOf(indexOf(vertex));
    }

    @NonNull
    public List<VERTEX_TYPE> neighborsOf(final int index) {
        return edges.get(index).stream()
                .map(edge -> vertexAt(edge.to))
                .toList();
    }

    public int indexOf(@NonNull final VERTEX_TYPE vertex) {
        return vertices.indexOf(vertex);
    }

    @NonNull
    public VERTEX_TYPE vertexAt(final int index) {
        return vertices.get(index);
    }

    @NonNull
    public List<EDGE_TYPE> edgesOf(@NonNull final VERTEX_TYPE vertex) {
        return edgesOf(indexOf(vertex));
    }

    @NonNull
    public List<EDGE_TYPE> edgesOf(final int index) {
        return List.copyOf(edges.get(index));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getVertexCount(); i++) {
            sb.append(vertexAt(i))
                    .append(" -> ")
                    .append(Arrays.toString(neighborsOf(i).toArray()))
                    .append("\n");
        }

        return sb.toString();
    }

}
