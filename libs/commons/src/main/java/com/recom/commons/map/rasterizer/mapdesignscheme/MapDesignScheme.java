package com.recom.commons.map.rasterizer.mapdesignscheme;

import com.recom.commons.model.Vector3D;
import org.springframework.lang.Nullable;

public abstract class MapDesignScheme {

    @Nullable
    private Double cachedSunAzimutRad = null;
    @Nullable
    private Double cachedSunElevationRad = null;
    @Nullable
    private Vector3D cachedSunLightVectorX = null;

    // clear all caches
    public void clearCache() {
        cachedSunAzimutRad = null;
        cachedSunElevationRad = null;
        cachedSunLightVectorX = null;
    }



    // Base terrain config
    public abstract int getBaseColorTerrain();
    public abstract int getBaseColorWater();
    public abstract int getBaseColorForestBackground();
    public abstract int getBaseColorForest();
    public abstract int getForestCellSizeInMeter();
    public abstract int getBaseColorStructureBackground();
    public abstract int getBaseColorStructure();
    public abstract int getStructureCellSizeInMeter();



    // Contour line config
    public abstract int getBaseColorContourBackground();
    public abstract int getBaseColorContourLineTerrain();
    public abstract int getBaseColorContourLineCoast();
    public abstract int getBaseColorContourLineWater();
    public abstract float getTransparencyModifierPrimaryLinesAboveSeaLevel();
    public abstract float getTransparencyModifierSecondaryLinesAboveSeaLevel();
    public abstract float getTransparencyModifierPrimaryLinesBelowSeaLevel();
    public abstract float getTransparencyModifierSecondaryLinesBelowSeaLevel();
    public abstract int getContourLineStepSize();
    public abstract int getMainContourLineStepSize();



    // Sun config (shadowing)
    public abstract int getSunAzimutDeg();
    public abstract int getSunElevationDeg();
    public abstract int getShadowMapAlpha();

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
