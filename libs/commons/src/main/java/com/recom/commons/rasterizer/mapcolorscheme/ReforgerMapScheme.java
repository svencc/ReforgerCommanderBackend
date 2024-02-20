package com.recom.commons.rasterizer.mapcolorscheme;

import lombok.Getter;

@Getter
public class ReforgerMapScheme extends MapShadowingScheme {

    // Base color config
    private final int baseColorTerrain = 0xFFF1F1F3;
    private final int baseColorWater = 0xFFA1D2E8;
    private final int baseColorForest = 0xFFA1D2E8;

    // Contour line config
    private final int baseColorContourLine = 0xFF9B9B64;
    private final int contourLineStepSize = 25;
    private final int nrOfContourLinesToBig = 4;

    // Sun config (shadowing)
    private final int sunAzimutDeg = 315; // usually 315
    private final int sunElevationDeg = 30; // usually 30-45

}
