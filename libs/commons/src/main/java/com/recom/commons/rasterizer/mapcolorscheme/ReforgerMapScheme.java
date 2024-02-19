package com.recom.commons.rasterizer.mapcolorscheme;

import lombok.Getter;

@Getter
public class ReforgerMapScheme extends MapShadowingScheme {

    private final int baseColorTerrain = 0xFFF1F1F3;
    private final int baseColorWater = 0xFFA1D2E8;
    private final int baseColorForest = 0xFFA1D2E8;

    private final int sunAzimutDeg = 315; // usually 315
    private final int sunElevationDeg = 45; // usually 30-45

}
