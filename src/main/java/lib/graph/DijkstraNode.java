package lib.graph;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DijkstraNode implements Comparable<DijkstraNode> {

    private final int vertex;
    private final double distance;

    @Override
    public int compareTo(@NonNull final DijkstraNode other) {
        final Double mine = distance;
        final Double theirs = other.distance;

        return mine.compareTo(theirs);
    }

}
