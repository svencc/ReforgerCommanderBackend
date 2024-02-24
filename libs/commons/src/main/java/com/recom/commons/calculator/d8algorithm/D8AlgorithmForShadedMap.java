package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.calculator.VectorCalculator;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.model.Vector3D;
import com.recom.commons.rasterizer.mapcolorscheme.MapShadowingScheme;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class D8AlgorithmForShadedMap {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();


    public int[][] calculateShadedMap(
            @NonNull final SlopeAndAspect[][] slopeAndAspectMap,
            @NonNull final MapShadowingScheme shadowingScheme
    ) {
        final int[][] shadingMap = new int[slopeAndAspectMap.length][slopeAndAspectMap[0].length];

        for (int x = 0; x < slopeAndAspectMap.length; x++) {
            for (int y = 0; y < slopeAndAspectMap[0].length; y++) {
                shadingMap[x][y] = calculateShading(slopeAndAspectMap[x][y], shadowingScheme);
            }
        }

        return shadingMap;
    }

    private int calculateShading(
            @NonNull final SlopeAndAspect slopeAndAspect,
            @NonNull final MapShadowingScheme shadowingScheme
    ) {
        final double slopeRad = Math.atan(slopeAndAspect.getSlope());

        final Vector3D terrainNormal = Vector3D.builder()
                .x(Math.cos(Math.toRadians(slopeAndAspect.getAspect().getAngle())) * Math.sin(slopeRad))
                .y(Math.sin(Math.toRadians(slopeAndAspect.getAspect().getAngle())) * Math.sin(slopeRad))
                .z(Math.cos(slopeRad))
                .build();

        // Berechnen Sie das Skalarprodukt der Vektoren für die Lichtintensität (wie stark das Licht auf die Zelle trifft; wie stark die beiden Vektoren aufeinander ausgerichtet sind).
        final Vector3D sunLightVector = shadowingScheme.getSunLightVector();
        final double dotProduct = VectorCalculator.dotProduct(sunLightVector, terrainNormal);
        final double brightness = Math.max(0, dotProduct);

        return colorCalculator.shade(shadowingScheme.getBaseColorTerrain(), brightness);
    }

}
