package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.rasterizer.mapcolorscheme.MapDesignScheme;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class D8AlgorithmForSlopeMap {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();


    public int[][] generateSlopeMap(
            @NonNull final SlopeAndAspect[][] slopeAndAspects,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int[][] slopeMap = new int[slopeAndAspects.length][slopeAndAspects[0].length];

        for (int x = 0; x < slopeAndAspects.length; x++) {
            for (int y = 0; y < slopeAndAspects[0].length; y++) {
                slopeMap[x][y] = calculateSlopeColor(slopeAndAspects[x][y], mapScheme);
            }
        }

        return slopeMap;
    }

    private int calculateSlopeColor(
            @NonNull final SlopeAndAspect slopeAndAspect,
            @NonNull final MapDesignScheme mapScheme
    ) {
        // @TODO: Refactor this method to use the MapScheme !!!
        final double slopePercentage = slopeAndAspect.getSlope() * 100; // Conversion to percentage
        if (slopePercentage > 90) {
            return colorCalculator.compose(255, 75, 0, 130); // Indigo (#4B0082) - Extremely steep
        } else if (slopePercentage > 80) {
            return colorCalculator.compose(255, 0, 0, 255); // Blue (#0000FF) - Very steep
        } else if (slopePercentage > 70) {
            return colorCalculator.compose(255, 0, 191, 255); // Deep Sky Blue (#00BFFF) - Steep
        } else if (slopePercentage > 60) {
            return colorCalculator.compose(255, 135, 206, 235); // Sky Blue (#87CEEB) - Moderately steep
        } else if (slopePercentage > 50) {
            return colorCalculator.compose(255, 0, 255, 127); // Spring Green (#00FF7F) - Moderate
        } else if (slopePercentage > 40) {
            return colorCalculator.compose(255, 173, 255, 47); // Green Yellow (#ADFF2F) - Slight incline
        } else if (slopePercentage > 30) {
            return colorCalculator.compose(255, 255, 255, 0); // Yellow (#FFFF00) - Very slight incline
        } else if (slopePercentage > 20) {
            return colorCalculator.compose(255, 255, 165, 0); // Orange (#FFA500) - Barely noticeable incline
        } else if (slopePercentage > 10) {
            return colorCalculator.compose(255, 255, 69, 0); // Red Orange (#FF4500) - Minimal incline
        } else {
            return colorCalculator.compose(255, 0, 128, 0); // Green (#008000) - Flat
        }
    }

}
