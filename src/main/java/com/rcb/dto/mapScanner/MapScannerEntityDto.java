package com.rcb.dto.mapScanner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapScannerEntityDto {

    @Schema
    @JsonProperty()
    private String entityId;

    @Schema
    @JsonProperty()
    private String className;

    @Schema
    @JsonProperty()
    private String resourceName;

    @Schema
    @JsonProperty()
    @Builder.Default
    private List<BigDecimal> rotationX = new ArrayList<>();

    @Schema
    @JsonProperty()
    @Builder.Default
    private List<BigDecimal> rotationY = new ArrayList<>();

    @Schema
    @JsonProperty()
    @Builder.Default
    private List<BigDecimal> rotationZ = new ArrayList<>();

    @Schema
    @JsonProperty()
    @Builder.Default
    private List<BigDecimal> coords = new ArrayList<>();

}
