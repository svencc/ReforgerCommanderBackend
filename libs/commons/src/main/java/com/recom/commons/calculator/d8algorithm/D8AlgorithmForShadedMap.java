package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.calculator.VectorCalculator;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.model.Vector3D;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class D8AlgorithmForShadedMap {

    @NonNull
    private final ARGBCalculator colorCalculator = new ARGBCalculator();


    @NonNull
    public int[][] generateShadedMap(
            @NonNull final SlopeAndAspect[][] slopeAndAspectMap,
            @NonNull final MapDesignScheme shadowingScheme
    ) {
        final int mapHeight = slopeAndAspectMap.length;
        final int mapWidth = slopeAndAspectMap[0].length;
        final int[][] shadingMap = new int[mapHeight][mapWidth];

        for (int demY = 0; demY < mapHeight; demY++) {
            for (int demX = 0; demX < mapWidth; demX++) {
                shadingMap[demY][demX] = calculateShading(slopeAndAspectMap[demY][demX], shadowingScheme);
            }
        }

        return shadingMap;
    }

    private int calculateShading(
            @NonNull final SlopeAndAspect slopeAndAspect,
            @NonNull final MapDesignScheme shadowingScheme
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

        return colorCalculator.modifyBrightness(shadowingScheme.getShadowMapAlpha(), brightness);
    }

}
