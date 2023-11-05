package com.recom.dto.map.scanner.topography;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.event.listener.generic.MapLocatedDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapTopographyEntityDto implements MapLocatedDto {

    @Schema
    @JsonProperty()
    private Float surfaceHeight;

    @Schema
    @JsonProperty()
    private Float oceanHeight;

    @Schema
    @JsonProperty()
    private Float oceanBaseHeight;

    @Schema
    @JsonProperty()
    private List<BigDecimal> coordinates;

}
