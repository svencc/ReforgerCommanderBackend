package com.recom.dto.map.topography;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapTopographyDataPointDto implements Serializable {

    @NotBlank
    @Schema
    @JsonProperty()
    private String mapName;

    @Schema
    @JsonProperty()
    private float oceanHeight;

    @Schema
    @JsonProperty()
    private float coordinates;

}