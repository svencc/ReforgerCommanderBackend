package com.recom.commons.rasterizer.mapcolorscheme;

import com.recom.commons.model.Vector3D;

public abstract class MapShadowingScheme {

    private Double cachedSunAzimutRad = null;
    private Double cachedSunElevationRad = null;
    private Vector3D cachedSunLightVectorX = null;


    public abstract int getBaseColorTerrain();

    public abstract int getBaseColorWater();

    public abstract int getBaseColorForest();


    public abstract int getBaseColorContourLine();
    public abstract int getContourLineStepSize();
    public abstract int getNrOfContourLinesToBig();


    public abstract int getSunAzimutDeg();

    public abstract int getSunElevationDeg();

    public double getSunAzimutRad() {
        if (cachedSunAzimutRad == null) {
            cachedSunAzimutRad = Math.toRadians(getSunAzimutDeg());
        }

        return cachedSunAzimutRad;
    }

    public double getSunElevationRad() {
        if (cachedSunElevationRad == null) {
            cachedSunElevationRad = Math.toRadians(getSunElevationDeg());
        }

        return cachedSunElevationRad;
    }

    public Vector3D getSunLightVector() {
        if (cachedSunLightVectorX == null) {
            final double lx = Math.cos(getSunAzimutRad()) * Math.cos(getSunElevationRad());
            final double ly = Math.sin(getSunAzimutRad()) * Math.cos(getSunElevationRad());
            final double lz = Math.sin(getSunElevationRad());

            cachedSunLightVectorX = Vector3D.builder()
                    .x(lx)
                    .y(ly)
                    .z(lz)
                    .build();
        }

        return cachedSunLightVectorX;
    }

}
