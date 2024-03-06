package com.recom.commons.calculator;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CoordinateConverter {

    public double threeDeeZToTwoDeeY(
            final double y,
            final int demHeight,
            final double stepSize
    ) {
        return (demHeight * stepSize) - y;
    }

}
