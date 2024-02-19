package com.recom.commons.calculator;

import com.recom.commons.model.Vector3D;

public class VectorCalculator {

    public static double dotProduct(
            final Vector3D sunLightVector,
            final Vector3D terrainNormal
    ) {
        return sunLightVector.getX() * terrainNormal.getX() +
                sunLightVector.getY() * terrainNormal.getY() +
                sunLightVector.getZ() * terrainNormal.getZ();
    }

}
