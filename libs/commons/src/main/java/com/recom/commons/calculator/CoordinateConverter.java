package com.recom.commons.calculator;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CoordinateConverter {

    public double threeDeeZToTwoDeeY(
            final double yCoordinate,
            final double demHeightInMeter
    ) {
        return (demHeightInMeter) - yCoordinate;
    }

}
