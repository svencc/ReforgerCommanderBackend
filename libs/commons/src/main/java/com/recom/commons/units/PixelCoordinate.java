package com.recom.commons.units;

import com.recom.commons.units.calc.ScalingTool;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class PixelCoordinate {

    private int x;
    private int y;

    public static PixelCoordinate of(
            final int x,
            final int y
    ) {
        return new PixelCoordinate(x, y);
    }

    public static PixelCoordinate of(
            final double x,
            final double y
    ) {
        return new PixelCoordinate((int) x, (int) y);
    }

    @NonNull
    public PixelCoordinate scaled(final int scaleFactor) {
        return PixelCoordinate.of(ScalingTool.scaleDimension(getX(), scaleFactor), ScalingTool.scaleDimension(getY(), scaleFactor));
    }
}
