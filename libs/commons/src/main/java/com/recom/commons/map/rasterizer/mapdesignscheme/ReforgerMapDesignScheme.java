package com.recom.commons.map.rasterizer.mapdesignscheme;

import lombok.Getter;

@Getter
public class ReforgerMapDesignScheme extends MapDesignScheme {

    // Base color config
    private final int baseColorTerrain = 0xFFF1F1F3;
    private final int baseColorWater = 0xFFA1D2E8;
    private final int baseColorForest = 0xFFA1D2E8;

    // Contour line config
    private final int baseColorContourBackground = 0x00000000;
    private final int baseColorContourLine = 0x55E2A750;
    private final int contourLineStepSize = 20;

    // Sun config (shadowing)
    private final int sunAzimutDeg = 315; // usually 315
    private final int sunElevationDeg = 30; // usually 30-45
    private final int shadowMapAlpha = 0x66FFFFFF;
//    private final int shadowMapAlpha = 0x66E2E2E2;

}
