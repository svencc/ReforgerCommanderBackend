package lib.javaconcavehull.main.concavehull;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class DistancedPoint {

    private final Double distance;
    private final Point point;

}