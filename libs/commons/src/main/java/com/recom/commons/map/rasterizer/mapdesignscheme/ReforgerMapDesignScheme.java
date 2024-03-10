package com.recom.commons.map.rasterizer.mapdesignscheme;

import lombok.Getter;

@Getter
public class ReforgerMapDesignScheme extends MapDesignScheme {

    // Base color config
    private final int baseColorTerrain = 0xFFF1F1F3;
    private final int baseColorWater = 0xFFAEE4FF;

    private final int baseColorForest = 0xFFA1D2E8;
    private final int baseColorForestBackground = 0x00000000;
    private final int forestCellSizeInMeter = 10;

    private final int baseColorStructure = 0xFF37BEC8;
    private final int baseColorStructureBackground = 0x00000000;
    private final int structureCellSizeInMeter = 10;

    // Contour line config
    private final int baseColorContourBackground = 0x00000000;
    private final int baseColorContourLineTerrain = 0x44BEAA8C;
    private final int baseColorContourLineCoast = 0xAA597482;
    private final int baseColorContourLineWater = 0x22597482;
    private final float brightnessModifierPrimaryLinesAboveSeaLevel = 0.6f;
    private final float brightnessModifierSecondaryLinesAboveSeaLevel = 1f;
    private final float brightnessModifierPrimaryLinesBelowSeaLevel = 0.25f;
    private final float brightnessModifierSecondaryLinesBelowSeaLevel = 1f;
    private final int contourLineStepSize = 20;
    private final int mainContourLineStepSize = 100;

    // Sun config (shadowing)
    private final int sunAzimutDeg = 315; // usually 315
    private final int sunElevationDeg = 45; // usually 30-45
    private final int shadowMapAlpha = 0x66FFFFFF; // 0x66E2E2E2;

}
