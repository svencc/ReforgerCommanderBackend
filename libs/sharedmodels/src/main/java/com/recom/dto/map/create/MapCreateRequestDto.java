package com.recom.dto.map.create;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapCreateRequestDto implements Serializable {

    @NotBlank
    @Schema
    @JsonProperty()
    private String mapName;

    @NotNull
    @Schema
    @JsonProperty()
    private BigDecimal dimensionXInMeter; // map 2D widthX -> round down

    @NotNull
    @Schema
    @JsonProperty()
    private BigDecimal dimensionYInMeter; // map depthZ -> round down

    @NotNull
    @Schema
    @JsonProperty()
    private BigDecimal dimensionZInMeter; // map 2D heightY -> round down

    @NotNull
    @Schema
    @JsonProperty()
    private BigDecimal oceanBaseHeight;

}

