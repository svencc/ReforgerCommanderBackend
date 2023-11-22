package lib.clipboard.graph;


import lombok.Getter;
import lombok.NonNull;

@Getter
public class WeightedEdge extends Edge implements Comparable<WeightedEdge> {

    protected final double weight;

    public WeightedEdge(
            final int from,
            final int to,
            final double weight
    ) {
        super(from, to);
        this.weight = weight;
    }

    @NonNull
    @Override
    public WeightedEdge reversed() {
        return new WeightedEdge(to, from, weight);
    }

    @Override
    public String toString() {
        return String.format("%s --(%.2f)--> %s", from, weight, to);
    }

    @Override
    public int compareTo(@NonNull final WeightedEdge other) {
        final Double mine = weight;
        final Double theirs = other.weight;

        return mine.compareTo(theirs);
    }
}
