package com.recom.dto.map.mapcomposer;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapDesignSchemeDto {

    // Base terrain config
    public Integer baseColorTerrain;
    public Integer baseColorWater;
    public Integer baseColorForest;

    // Contour line config
    public Integer baseColorContourBackground;
    public Integer baseColorContourLineTerrain;
    public Integer baseColorContourLineWater;
    public Integer contourLineStepSize;

    // Sun config (shadowing)
    public Integer sunAzimutDeg;
    public Integer sunElevationDeg;
    public Integer shadowMapAlpha;

}
