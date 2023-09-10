package lib.graph;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class DijkstraResult {

    private final double[] distance;
    private final Map<Integer, WeightedEdge> pathMap;

}
