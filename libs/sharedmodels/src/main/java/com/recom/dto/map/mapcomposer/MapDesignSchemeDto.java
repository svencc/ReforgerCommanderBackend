package com.recom.dto.map.mapcomposer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapDesignSchemeDto {

    // Base terrain config
    @Nullable
    @Schema
    @JsonProperty()
    public Integer baseColorTerrain;
    @Nullable
    @Schema
    @JsonProperty()
    public Integer baseColorWater;
    @Nullable
    @Schema
    @JsonProperty()
    public Integer baseColorForest;


    // Contour line config
    @Nullable
    @Schema
    @JsonProperty()
    public Integer baseColorContourBackground;
    @Nullable
    @Schema
    @JsonProperty()
    public Integer baseColorContourLineTerrain;
    @Nullable
    @Schema
    @JsonProperty()
    public Integer baseColorContourLineWater;
    @Nullable
    @Schema
    @JsonProperty()
    public Integer contourLineStepSize;


    // Sun config (shadowing)
    @Nullable
    @Schema
    @JsonProperty()
    public Integer sunAzimutDeg;
    @Nullable
    @Schema
    @JsonProperty()
    public Integer sunElevationDeg;
    @Nullable
    @Schema
    @JsonProperty()
    public Integer shadowMapAlpha;

}
