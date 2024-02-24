package com.recom.dto.map.topography;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HeightMapDescriptorDto implements Serializable {


    @Schema
    @JsonProperty()
    private String mapName;

    @Schema
    @JsonProperty()
    private Float stepSize;

    @Schema
    @JsonProperty()
    private Integer scanIterationsX;

    @Schema
    @JsonProperty()
    private Integer scanIterationsZ;

    @Schema
    @JsonProperty()
    private Float seaLevel;

    @Schema
    @JsonProperty()
    private Float maxWaterDepth;

    @Schema
    @JsonProperty()
    private Float maxHeight;

    @Schema
    @JsonProperty()
    private float[][] heightMap;

}