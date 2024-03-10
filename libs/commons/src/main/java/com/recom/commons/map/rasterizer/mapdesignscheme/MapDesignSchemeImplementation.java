package com.recom.commons.map.rasterizer.mapdesignscheme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapDesignSchemeImplementation extends MapDesignScheme {

    // Base color config
    @Builder.Default
    private int baseColorTerrain = 0xFFF1F1F3;
    @Builder.Default
    private int baseColorWater = 0xFFAEE4FF;
    @Builder.Default
    private int baseColorForestBackground = 0x00000000;
    @Builder.Default
    private int baseColorForest = 0xFFA1D2E8;
    @Builder.Default
    private int forestCellSizeInMeter = 10;
    @Builder.Default
    private int baseColorStructureBackground = 0x00000000;
    @Builder.Default
    private int baseColorStructure = 0xFFB8BCBF;
    @Builder.Default
    private int structureCellSizeInMeter = 10;


    // Contour line config
    @Builder.Default
    private int baseColorContourBackground = 0x00000000;
    @Builder.Default
    private int baseColorContourLineTerrain = 0x44BEAA8C;
    @Builder.Default
    private final int baseColorContourLineCoast = 0xAA597482;
    @Builder.Default
    private int baseColorContourLineWater = 0x22597482;
    @Builder.Default
    private float brightnessModifierPrimaryLinesAboveSeaLevel = 0.6f;
    @Builder.Default
    private float brightnessModifierSecondaryLinesAboveSeaLevel = 1f;
    @Builder.Default
    private final float brightnessModifierPrimaryLinesBelowSeaLevel = 0.25f;
    @Builder.Default
    private final float brightnessModifierSecondaryLinesBelowSeaLevel = 1f;
    @Builder.Default
    private int contourLineStepSize = 20;
    @Builder.Default
    private int mainContourLineStepSize = 100;


    // Sun config (shadowing)
    @Builder.Default
    private int sunAzimutDeg = 315;
    @Builder.Default
    private int sunElevationDeg = 35;
    @Builder.Default
    private int shadowMapAlpha = 0x66FFFFFF;

}
