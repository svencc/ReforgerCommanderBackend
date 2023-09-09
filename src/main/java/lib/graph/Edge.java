package lib.graph;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Edge {

    public final int from;
    public final int to;

    @NonNull
    public Edge reversed() {
        return new Edge(to, from);
    }

    @Override
    public String toString() {
        return String.format("%d -> %d", from, to);
    }

}
